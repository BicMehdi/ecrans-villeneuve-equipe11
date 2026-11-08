package com.ecrans.vente.application.service;

import com.ecrans.vente.application.exception.SeanceInconnue;
import com.ecrans.vente.application.port.in.AnnulerSeance;
import com.ecrans.vente.application.port.out.Commandes;
import com.ecrans.vente.application.port.out.PublicateurEvenements;
import com.ecrans.vente.application.port.out.Seances;
import com.ecrans.vente.domaine.commun.SeanceId;
import java.time.Clock;
import java.time.LocalDateTime;

public record AnnulerSeanceService(Seances seances, Commandes commandes, PublicateurEvenements publicateur,
                                  Clock horloge) implements AnnulerSeance {
    @Override
    public void executer(SeanceId id) {
        synchronized (commandes) {
            var seance = seances.parId(id).orElseThrow(() -> new SeanceInconnue(id));
            if (!seance.annulee()) {
                seance.annuler();
                seances.enregistrer(seance);
            }
            var maintenant = LocalDateTime.now(horloge);
            for (var commande : commandes.parSeance(id)) {
                switch (commande.statut()) {
                    case PAYEE -> commande.aRembourser(maintenant);
                    case EN_ATTENTE_PAIEMENT -> commande.expirer(maintenant);
                    default -> { continue; }
                }
                commandes.enregistrer(commande);
                commande.retirerEvenements().forEach(publicateur::publier);
            }
        }
    }
}
