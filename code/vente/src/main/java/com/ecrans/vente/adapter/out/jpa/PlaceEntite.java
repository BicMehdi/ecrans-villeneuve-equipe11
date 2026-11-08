package com.ecrans.vente.adapter.out.jpa;

import com.ecrans.vente.domaine.commun.CommandeId;
import com.ecrans.vente.domaine.seance.EtatPlace;
import com.ecrans.vente.domaine.seance.StatutPlace;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;

@Embeddable
public class PlaceEntite {
    @Enumerated(EnumType.STRING)
    private StatutPlace statut;
    private String commande;
    private LocalDateTime bloqueeJusqua;

    protected PlaceEntite() {}

    PlaceEntite(EtatPlace etat) {
        statut = etat.statut();
        commande = etat.commande() == null ? null : etat.commande().valeur();
        bloqueeJusqua = etat.bloqueeJusqua();
    }

    EtatPlace versDomaine() {
        return new EtatPlace(statut, commande == null ? null : new CommandeId(commande), bloqueeJusqua);
    }
}
