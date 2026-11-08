package com.ecrans.vente.application.service;

import com.ecrans.vente.application.exception.SeanceInconnue;
import com.ecrans.vente.application.port.in.EnregistrerVentePartenaire;
import com.ecrans.vente.application.port.in.VentePartenaire;
import com.ecrans.vente.application.port.out.Commandes;
import com.ecrans.vente.application.port.out.GrilleTarifaire;
import com.ecrans.vente.application.port.out.PublicateurEvenements;
import com.ecrans.vente.application.port.out.Seances;
import com.ecrans.vente.domaine.commande.Billet;
import com.ecrans.vente.domaine.commande.Commande;
import com.ecrans.vente.domaine.commande.Tarif;
import com.ecrans.vente.domaine.commun.CommandeId;
import com.ecrans.vente.domaine.evenement.VentePartenaireEnregistree;
import java.time.Clock;
import java.time.LocalDateTime;

public record EnregistrerVentePartenaireService(Seances seances, Commandes commandes, GrilleTarifaire grille,
                                                 PublicateurEvenements publicateur, Clock horloge)
        implements EnregistrerVentePartenaire {

    @Override
    public CommandeId executer(VentePartenaire vente) {
        synchronized (commandes) {
            var maintenant = LocalDateTime.now(horloge);
            var id = CommandeId.nouvelle();
            var billets = vente.billets().stream()
                    .map(billet -> new Billet(billet.place(),
                            new Tarif(billet.categorie(), grille.prix(vente.seance(), billet.categorie()))))
                    .toList();
            var commande = Commande.creer(id, vente.seance(), billets, maintenant);
            var seance = seances.parId(vente.seance()).orElseThrow(() -> new SeanceInconnue(vente.seance()));
            seance.vendre(id, commande.places(), maintenant);
            commande.payer(maintenant);
            seances.enregistrer(seance);
            commandes.enregistrer(commande);
            commande.retirerEvenements().forEach(publicateur::publier);
            publicateur.publier(new VentePartenaireEnregistree(id, vente.partenaire(), vente.referencePartenaire(),
                    vente.montantPaye(), maintenant));
            return id;
        }
    }
}
