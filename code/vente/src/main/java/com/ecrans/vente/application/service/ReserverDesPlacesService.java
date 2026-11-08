package com.ecrans.vente.application.service;

import com.ecrans.vente.application.port.out.GrilleTarifaire;
import com.ecrans.vente.application.exception.SeanceInconnue;
import com.ecrans.vente.application.port.in.DemandeReservation;
import com.ecrans.vente.application.port.in.Reservation;
import com.ecrans.vente.application.port.in.ReserverDesPlaces;
import com.ecrans.vente.application.port.out.Commandes;
import com.ecrans.vente.application.port.out.ConflitDeVersion;
import com.ecrans.vente.application.port.out.Paiements;
import com.ecrans.vente.application.port.out.Seances;
import com.ecrans.vente.domaine.commande.Billet;
import com.ecrans.vente.domaine.commande.Commande;
import com.ecrans.vente.domaine.commande.Tarif;
import com.ecrans.vente.domaine.commun.CommandeId;
import com.ecrans.vente.application.port.out.PublicateurEvenements;
import com.ecrans.vente.domaine.seance.Seance;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Orchestration seulement : charger, faire décider le domaine, enregistrer, puis appeler le PSP.
 *
 * Le démonstrateur sérialise les écritures sur la séance et la commande dans son processus.
 * La cible les écrit dans une transaction PostgreSQL (ADR-0002). L'appel au PSP suit les écritures.
 */
public class ReserverDesPlacesService implements ReserverDesPlaces {

    /** Bonus : nombre d'essais quand une autre vente a modifié la séance en même temps. */
    static final int ESSAIS_MAX = 3;

    private final Seances seances;
    private final Commandes commandes;
    private final GrilleTarifaire grille;
    private final Paiements paiements;
    private final Clock horloge;
    private final PublicateurEvenements publicateur;

    public ReserverDesPlacesService(Seances seances, Commandes commandes, GrilleTarifaire grille,
                                    Paiements paiements, Clock horloge, PublicateurEvenements publicateur) {
        this.seances = seances;
        this.commandes = commandes;
        this.grille = grille;
        this.paiements = paiements;
        this.horloge = horloge;
        this.publicateur = publicateur;
    }

    public ReserverDesPlacesService(Seances seances, Commandes commandes, GrilleTarifaire grille,
                                    Paiements paiements, Clock horloge) {
        this(seances, commandes, grille, paiements, horloge, evenement -> {});
    }

    @Override
    public Reservation executer(DemandeReservation demande) {
        CommandeId id = CommandeId.nouvelle();
        List<Billet> billets = demande.billets().stream()
                .map(billet -> new Billet(billet.place(),
                        new Tarif(billet.categorie(), grille.prix(demande.seance(), billet.categorie()))))
                .toList();
        Commande commande;
        synchronized (commandes) {
            commande = enregistrer(demande, id, billets);
        }
        var total = commande.total();
        String url = total.centimes() == 0 ? null : paiements.demanderPaiement(id, total);
        return new Reservation(id, total, url, commande.expireA());
    }

    private Commande enregistrer(DemandeReservation demande, CommandeId id, List<Billet> billets) {
        for (int essai = 1; ; essai++) {
            LocalDateTime maintenant = LocalDateTime.now(horloge);
            Seance seance = seances.parId(demande.seance()).orElseThrow(() -> new SeanceInconnue(demande.seance()));
            LocalDateTime fin = maintenant.plus(Seance.DUREE_BLOCAGE);
            Commande commande = Commande.creer(id, demande.seance(), billets, fin);
            boolean gratuite = commande.total().centimes() == 0;
            if (gratuite) {
                seance.vendre(id, commande.places(), maintenant);
                commande.payer(maintenant);
            } else {
                seance.bloquer(commande.places(), id, maintenant);
            }
            try {
                seances.enregistrer(seance);
            } catch (ConflitDeVersion conflit) {
                if (essai == ESSAIS_MAX) throw conflit;
                continue;
            }
            commandes.enregistrer(commande);
            if (gratuite) commande.retirerEvenements().forEach(publicateur::publier);
            return commande;
        }
    }
}
