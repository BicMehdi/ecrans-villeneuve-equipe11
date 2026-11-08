package com.ecrans.vente.application.service;

import com.ecrans.vente.application.exception.CommandeInconnue;
import com.ecrans.vente.application.exception.SeanceInconnue;
import com.ecrans.vente.application.port.in.ConfirmerPaiement;
import com.ecrans.vente.application.port.out.Commandes;
import com.ecrans.vente.application.port.out.PublicateurEvenements;
import com.ecrans.vente.application.port.out.Seances;
import com.ecrans.vente.domaine.commande.Commande;
import com.ecrans.vente.domaine.commande.StatutCommande;
import com.ecrans.vente.domaine.commun.CommandeId;
import com.ecrans.vente.domaine.seance.PlacesIndisponibles;
import com.ecrans.vente.domaine.seance.Seance;
import com.ecrans.vente.domaine.seance.VenteFermee;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * La vente des places et le passage à PAYEE sont sérialisés dans le démonstrateur.
 * En production, une seule transaction garantit ce passage pour les deux agrégats (ADR-0002).
 */
public class ConfirmerPaiementService implements ConfirmerPaiement {

    private final Seances seances;
    private final Commandes commandes;
    private final PublicateurEvenements publicateur;
    private final Clock horloge;

    public ConfirmerPaiementService(Seances seances, Commandes commandes, PublicateurEvenements publicateur,
                                    Clock horloge) {
        this.seances = seances;
        this.commandes = commandes;
        this.publicateur = publicateur;
        this.horloge = horloge;
    }

    @Override
    public void executer(CommandeId id) {
        synchronized (commandes) {
            LocalDateTime maintenant = LocalDateTime.now(horloge);
            Commande commande = commandes.parId(id).orElseThrow(() -> new CommandeInconnue(id));
            if (commande.statut() == StatutCommande.PAYEE || commande.statut() == StatutCommande.A_REMBOURSER) return;
            Seance seance = seances.parId(commande.seance()).orElseThrow(() -> new SeanceInconnue(commande.seance()));
            try {
                seance.vendre(id, commande.places(), maintenant);
                commande.payer(maintenant);
                seances.enregistrer(seance);
            } catch (PlacesIndisponibles | VenteFermee refus) {
                commande.aRembourser(maintenant);
            }
            commandes.enregistrer(commande);
            commande.retirerEvenements().forEach(publicateur::publier);
        }
    }
}
