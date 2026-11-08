package com.ecrans.vente.adapter.out.jpa;

import com.ecrans.vente.domaine.commun.PlaceId;
import com.ecrans.vente.domaine.commun.SeanceId;
import com.ecrans.vente.domaine.seance.EtatPlace;
import com.ecrans.vente.domaine.seance.Seance;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
public class SeanceEntite {
    @Id
    private String id;
    @Version
    private Long version;
    private LocalDateTime debut;
    private boolean annulee;
    // Chaque enregistrement incrémente la version, même si le plan reste identique.
    private long revision;
    @ElementCollection
    @MapKeyColumn(name = "code_place")
    private Map<String, PlaceEntite> places = new HashMap<>();

    protected SeanceEntite() {}

    SeanceEntite(Seance seance) {
        id = seance.id().valeur();
        appliquer(seance);
    }

    void appliquer(Seance seance) {
        debut = seance.debut();
        annulee = seance.annulee();
        revision++;
        places.clear();
        seance.places().forEach((place, etat) -> places.put(place.toString(), new PlaceEntite(etat)));
    }

    long versionDomaine() {
        return version == null ? 0 : version + 1;
    }

    Seance versDomaine() {
        Map<PlaceId, EtatPlace> etats = new HashMap<>();
        places.forEach((place, etat) -> etats.put(PlaceId.de(place), etat.versDomaine()));
        return Seance.reconstituer(new SeanceId(id), debut, etats, versionDomaine(), annulee);
    }
}
