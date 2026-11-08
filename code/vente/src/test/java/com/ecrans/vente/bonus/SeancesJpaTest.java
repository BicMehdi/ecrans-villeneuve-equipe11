package com.ecrans.vente.bonus;

import com.ecrans.vente.VenteApplication;
import com.ecrans.vente.application.port.out.ConflitDeVersion;
import com.ecrans.vente.application.port.out.Seances;
import com.ecrans.vente.domaine.commun.PlaceId;
import com.ecrans.vente.domaine.commun.SeanceId;
import com.ecrans.vente.domaine.seance.Seance;
import com.ecrans.vente.domaine.seance.StatutPlace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import static com.ecrans.vente.support.Donnees.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = VenteApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("jpa")
class SeancesJpaTest {
    @Autowired
    Seances seances;

    @Test
    void conserve_les_places_et_refuse_une_version_perimee() {
        var id = new SeanceId("JPA-1");
        var place = PlaceId.de("G7");
        seances.enregistrer(Seance.ouverte(id, le12Novembre("20:30"), List.of(place)));
        var courante = seances.parId(id).orElseThrow();
        var perimee = seances.parId(id).orElseThrow();
        courante.vendre(C1, places("G7"), le12Novembre("20:02"));
        seances.enregistrer(courante);
        assertThat(seances.parId(id).orElseThrow().etat(place).statut()).isEqualTo(StatutPlace.VENDUE);
        assertThatThrownBy(() -> seances.enregistrer(perimee)).isInstanceOf(ConflitDeVersion.class);
        var aAnnuler = seances.parId(id).orElseThrow();
        aAnnuler.annuler();
        seances.enregistrer(aAnnuler);
        assertThat(seances.parId(id).orElseThrow().annulee()).isTrue();
    }
}
