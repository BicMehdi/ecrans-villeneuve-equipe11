package com.ecrans.vente.adapter.in.web;

import com.ecrans.vente.application.port.in.DemandeBillet;
import com.ecrans.vente.application.port.in.DemandeReservation;
import com.ecrans.vente.application.port.in.ReserverDesPlaces;
import com.ecrans.vente.domaine.commande.Categorie;
import com.ecrans.vente.domaine.commun.PlaceId;
import com.ecrans.vente.domaine.commun.SeanceId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {
    private final ReserverDesPlaces reserver;

    public ReservationController(ReserverDesPlaces reserver) {
        this.reserver = reserver;
    }

    @PostMapping("/seances/{seance}/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationReponse reserver(@PathVariable String seance, @RequestBody ReservationRequete requete) {
        if (requete.billets() == null) throw new IllegalArgumentException("Liste des billets requise");
        var billets = requete.billets().stream().map(billet -> {
            if (billet == null || billet.place() == null || billet.categorie() == null) {
                throw new IllegalArgumentException("Place et catégorie requises pour chaque billet");
            }
            return new DemandeBillet(PlaceId.de(billet.place()), Categorie.valueOf(billet.categorie()));
        }).toList();
        var reservation = reserver.executer(new DemandeReservation(new SeanceId(seance), billets));
        return new ReservationReponse(reservation.commande().valeur(), reservation.total().centimes(),
                reservation.total().toString(), reservation.urlPaiement(), reservation.payerAvant());
    }
}
