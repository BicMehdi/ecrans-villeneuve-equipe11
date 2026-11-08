package com.ecrans.vente.bonus;

import com.ecrans.vente.config.ApplicationVente;
import com.ecrans.vente.domaine.commande.StatutCommande;
import com.ecrans.vente.domaine.commun.PlaceId;
import com.ecrans.vente.domaine.evenement.PaiementARembourser;
import com.ecrans.vente.domaine.seance.VenteFermee;
import com.ecrans.vente.support.HorlogeReglable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static com.ecrans.vente.support.Donnees.*;
import static org.assertj.core.api.Assertions.*;

class PlanEtAnnulationTest {
    private final HorlogeReglable horloge = HorlogeReglable.a("20:02");
    private final ApplicationVente app = new ApplicationVente(horloge);

    @BeforeEach
    void publier() {
        app.seances.enregistrer(seanceS42());
    }

    @Test
    void le_plan_libere_une_place_expiree_avant_le_planificateur() {
        var demande = demande("G7", "PLEIN");
        app.reserverDesPlaces.executer(demande);
        assertThat(app.consulterPlan.executer(S42).get(PlaceId.de("G7"))).isFalse();
        horloge.avancerDeMinutes(10);
        assertThat(app.consulterPlan.executer(S42).get(PlaceId.de("G7"))).isTrue();
    }

    @Test
    void place_gratuite_confirmee_sans_lien_de_paiement() {
        var reservation = app.reserverDesPlaces.executer(demande("G7", "ABONNE"));
        assertThat(reservation.total().centimes()).isZero();
        assertThat(reservation.urlPaiement()).isNull();
        assertThat(app.commandes.parId(reservation.commande()).orElseThrow().statut()).isEqualTo(StatutCommande.PAYEE);
        assertThat(app.consulterPlan.executer(S42).get(PlaceId.de("G7"))).isFalse();
        assertThat(app.publicateur.publies()).hasSize(1);
    }

    @Test
    void annuler_rembourse_et_expire_une_seule_fois() {
        var payee = app.reserverDesPlaces.executer(demande("G7", "PLEIN")).commande();
        app.confirmerPaiement.executer(payee);
        var attente = app.reserverDesPlaces.executer(demande("G8", "PLEIN")).commande();
        app.annulerSeance.executer(S42);
        app.annulerSeance.executer(S42);
        assertThat(app.commandes.parId(payee).orElseThrow().statut()).isEqualTo(StatutCommande.A_REMBOURSER);
        assertThat(app.commandes.parId(attente).orElseThrow().statut()).isEqualTo(StatutCommande.EXPIREE);
        assertThat(app.publicateur.publies().stream().filter(PaiementARembourser.class::isInstance)).hasSize(1);
        assertThatThrownBy(() -> app.reserverDesPlaces.executer(demande("G8", "PLEIN"))).isInstanceOf(VenteFermee.class);
        app.confirmerPaiement.executer(attente);
        app.confirmerPaiement.executer(attente);
        assertThat(app.commandes.parId(attente).orElseThrow().statut()).isEqualTo(StatutCommande.A_REMBOURSER);
        assertThat(app.publicateur.publies().stream().filter(PaiementARembourser.class::isInstance)).hasSize(2);
    }
}
