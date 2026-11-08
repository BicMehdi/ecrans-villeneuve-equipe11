package com.ecrans.vente.etape8;

import com.ecrans.vente.adapter.in.sortiesflash.AclSortiesFlash;
import com.ecrans.vente.adapter.in.sortiesflash.CommandeSortiesFlash;
import com.ecrans.vente.adapter.in.sortiesflash.CorrespondanceSeances;
import com.ecrans.vente.config.ApplicationVente;
import com.ecrans.vente.domaine.commande.Categorie;
import com.ecrans.vente.domaine.commande.StatutCommande;
import com.ecrans.vente.domaine.commun.Montant;
import com.ecrans.vente.domaine.commun.PlaceId;
import com.ecrans.vente.domaine.evenement.VentePartenaireEnregistree;
import com.ecrans.vente.domaine.seance.StatutPlace;
import com.ecrans.vente.support.HorlogeReglable;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static com.ecrans.vente.support.Donnees.*;
import static org.assertj.core.api.Assertions.*;

class VentePartenaireTest {
    private final AclSortiesFlash acl = new AclSortiesFlash(new CorrespondanceSeances(Map.of("DEMO", S42)));

    @Test
    void traduit_le_contrat_partenaire() {
        var vente = acl.traduire(new CommandeSortiesFlash("DEMO", List.of("G7", "G8"),
                List.of("ADULT", "STUDENT"), "SF-1", 1840L));
        assertThat(vente.seance()).isEqualTo(S42);
        assertThat(vente.billets().getLast().categorie()).isEqualTo(Categorie.REDUIT);
        assertThat(vente.montantPaye()).isEqualTo(Montant.centimes(1840));
    }

    @Test
    void conserve_le_montant_partenaire_et_vend_sans_payvia() {
        var app = new ApplicationVente(HorlogeReglable.a("20:02"));
        app.seances.enregistrer(seanceS42());
        var vente = acl.traduire(new CommandeSortiesFlash("DEMO", List.of("G7"),
                List.of("ADULT"), "SF-1", 1000L));
        var id = app.enregistrerVentePartenaire.executer(vente);
        var commande = app.commandes.parId(id).orElseThrow();
        assertThat(commande.statut()).isEqualTo(StatutCommande.PAYEE);
        assertThat(commande.total()).isEqualTo(Montant.centimes(1150));
        assertThat(app.seances.parId(S42).orElseThrow().etat(PlaceId.de("G7")).statut()).isEqualTo(StatutPlace.VENDUE);
        assertThat(app.publicateur.publies()).contains(
                new VentePartenaireEnregistree(id, "SortiesFlash", "SF-1", Montant.centimes(1000), le12Novembre("20:02")));
    }
}
