package com.ecrans.vente.adapter.in.sortiesflash;

import com.ecrans.vente.application.port.in.EnregistrerVentePartenaire;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SortiesFlashController {
    private final AclSortiesFlash acl;
    private final EnregistrerVentePartenaire enregistrer;

    public SortiesFlashController(AclSortiesFlash acl, EnregistrerVentePartenaire enregistrer) {
        this.acl = acl;
        this.enregistrer = enregistrer;
    }

    @PostMapping("/partner/v2/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Reponse vendre(@RequestBody CommandeSortiesFlash commande) {
        var id = enregistrer.executer(acl.traduire(commande));
        return new Reponse(commande.partner_order_id(), id.valeur(), "CONFIRMED");
    }

    public record Reponse(String partner_order_id, String cinema_order_id, String status) {}
}
