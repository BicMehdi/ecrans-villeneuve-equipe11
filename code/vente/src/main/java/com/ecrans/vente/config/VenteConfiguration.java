package com.ecrans.vente.config;

import com.ecrans.vente.adapter.in.payvia.WebhookPayVia;
import com.ecrans.vente.adapter.in.sortiesflash.AclSortiesFlash;
import com.ecrans.vente.adapter.in.sortiesflash.CorrespondanceSeances;
import com.ecrans.vente.application.port.in.ConfirmerPaiement;
import com.ecrans.vente.application.port.in.EnregistrerVentePartenaire;
import com.ecrans.vente.application.port.in.ExpirerBlocages;
import com.ecrans.vente.application.port.in.ReserverDesPlaces;
import com.ecrans.vente.domaine.commun.PlaceId;
import com.ecrans.vente.domaine.commun.SeanceId;
import com.ecrans.vente.domaine.seance.Seance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ecrans.vente.adapter.out.memoire.SeancesEnMemoire;
import com.ecrans.vente.application.port.in.AnnulerSeance;
import com.ecrans.vente.application.port.in.ConsulterPlan;
import com.ecrans.vente.application.port.out.Seances;
import org.springframework.context.annotation.Profile;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

@Configuration
public class VenteConfiguration {
    private static final SeanceId DEMO = new SeanceId("S42");

    @Bean
    Clock horloge() {
        return Clock.systemDefaultZone();
    }

    @Bean
    ApplicationVente applicationVente(Clock horloge, Seances seances) {
        return new ApplicationVente(horloge, seances);
    }

    @Bean
    @Profile("!jpa")
    Seances seancesEnMemoire() {
        return new SeancesEnMemoire();
    }

    @Bean
    ConsulterPlan consulterPlan(ApplicationVente app) {
        return app.consulterPlan;
    }

    @Bean
    AnnulerSeance annulerSeance(ApplicationVente app) {
        return app.annulerSeance;
    }

    @Bean
    ReserverDesPlaces reserverDesPlaces(ApplicationVente app) {
        return app.reserverDesPlaces;
    }

    @Bean
    ConfirmerPaiement confirmerPaiement(ApplicationVente app) {
        return app.confirmerPaiement;
    }

    @Bean
    ExpirerBlocages expirerBlocages(ApplicationVente app) {
        return app.expirerBlocages;
    }

    @Bean
    WebhookPayVia webhookPayVia(ApplicationVente app) {
        return app.webhookPayVia;
    }

    @Bean
    EnregistrerVentePartenaire enregistrerVentePartenaire(ApplicationVente app) {
        return app.enregistrerVentePartenaire;
    }

    @Bean
    LocalDate dateDeDemonstration(Clock horloge) {
        return LocalDate.now(horloge).plusDays(1);
    }

    @Bean
    AclSortiesFlash aclSortiesFlash(LocalDate dateDeDemonstration) {
        String reference = "VIL-" + dateDeDemonstration.format(DateTimeFormatter.BASIC_ISO_DATE) + "-2030-S3";
        return new AclSortiesFlash(new CorrespondanceSeances(Map.of(reference, DEMO)));
    }

    @Bean
    @Profile("!postgres")
    CommandLineRunner seanceDeDemonstration(ApplicationVente app, LocalDate dateDeDemonstration,
                                              @Value("${vente.demo.grande-salle:false}") boolean grandeSalle) {
        return args -> {
            var plan = new ArrayList<PlaceId>();
            char dernierRang = grandeSalle ? 'N' : 'H';
            int placesParRang = grandeSalle ? 30 : 12;
            for (char rang = 'A'; rang <= dernierRang; rang++) {
                for (int n = 1; n <= placesParRang; n++) plan.add(new PlaceId(rang, n));
            }
            app.seances.enregistrer(Seance.ouverte(DEMO, dateDeDemonstration.atTime(20, 30), plan));
        };
    }
}
