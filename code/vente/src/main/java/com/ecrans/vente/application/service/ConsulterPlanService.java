package com.ecrans.vente.application.service;

import com.ecrans.vente.application.exception.SeanceInconnue;
import com.ecrans.vente.application.port.in.ConsulterPlan;
import com.ecrans.vente.application.port.out.Seances;
import com.ecrans.vente.domaine.commun.PlaceId;
import com.ecrans.vente.domaine.commun.SeanceId;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public record ConsulterPlanService(Seances seances, Clock horloge) implements ConsulterPlan {
    @Override
    public Map<PlaceId, Boolean> executer(SeanceId id) {
        var seance = seances.parId(id).orElseThrow(() -> new SeanceInconnue(id));
        var maintenant = LocalDateTime.now(horloge);
        Map<PlaceId, Boolean> plan = new TreeMap<>();
        boolean ouverte = seance.venteOuverteA(maintenant);
        seance.places().forEach((place, etat) -> plan.put(place, ouverte && etat.estLibreA(maintenant)));
        return plan;
    }
}
