package com.ecrans.vente.adapter.in.sortiesflash;

import com.ecrans.vente.application.exception.SeanceInconnue;
import com.ecrans.vente.domaine.commun.SeanceId;
import java.util.Map;

public record CorrespondanceSeances(Map<String, SeanceId> references) {
    public CorrespondanceSeances {
        references = Map.copyOf(references);
    }

    public SeanceId resoudre(String reference) {
        SeanceId id = references.get(reference);
        if (id == null) throw new SeanceInconnue(new SeanceId(reference));
        return id;
    }
}
