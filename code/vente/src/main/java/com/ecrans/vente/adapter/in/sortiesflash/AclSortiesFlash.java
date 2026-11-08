package com.ecrans.vente.adapter.in.sortiesflash;

import com.ecrans.vente.application.port.in.DemandeBillet;
import com.ecrans.vente.application.port.in.VentePartenaire;
import com.ecrans.vente.domaine.commande.Categorie;
import com.ecrans.vente.domaine.commun.Montant;
import com.ecrans.vente.domaine.commun.PlaceId;
import java.util.ArrayList;
import java.util.List;

public record AclSortiesFlash(CorrespondanceSeances correspondance) {

    public VentePartenaire traduire(CommandeSortiesFlash commande) {
        if (commande.show_ref() == null || commande.show_ref().isBlank()
                || commande.partner_order_id() == null || commande.partner_order_id().isBlank()
                || commande.paid_amount_cents() == null || commande.seats() == null
                || commande.ticket_types() == null || commande.seats().size() != commande.ticket_types().size()) {
            throw new IllegalArgumentException("Référence, montant, places et catégories correspondantes requis");
        }
        var seance = correspondance.resoudre(commande.show_ref());
        List<DemandeBillet> billets = new ArrayList<>();
        for (int i = 0; i < commande.seats().size(); i++) {
            String place = commande.seats().get(i);
            String type = commande.ticket_types().get(i);
            if (place == null || type == null) throw new IllegalArgumentException("Place et catégorie requises");
            Categorie categorie = switch (type) {
                case "ADULT" -> Categorie.PLEIN;
                case "STUDENT" -> Categorie.REDUIT;
                case "CHILD" -> Categorie.ENFANT;
                default -> throw new IllegalArgumentException("Catégorie partenaire inconnue : " + type);
            };
            billets.add(new DemandeBillet(PlaceId.de(place), categorie));
        }
        return new VentePartenaire("SortiesFlash", commande.partner_order_id(), seance, billets,
                Montant.centimes(commande.paid_amount_cents()));
    }
}
