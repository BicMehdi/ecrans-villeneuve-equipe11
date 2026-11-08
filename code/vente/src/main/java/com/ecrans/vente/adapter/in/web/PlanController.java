package com.ecrans.vente.adapter.in.web;

import com.ecrans.vente.application.port.in.ConsulterPlan;
import com.ecrans.vente.domaine.commun.SeanceId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class PlanController {
    private final ConsulterPlan consulter;

    public PlanController(ConsulterPlan consulter) {
        this.consulter = consulter;
    }

    @GetMapping("/seances/{seance}/places")
    public List<PlaceDisponible> places(@PathVariable String seance) {
        return consulter.executer(new SeanceId(seance)).entrySet().stream()
                .map(e -> new PlaceDisponible(e.getKey().toString(), e.getValue())).toList();
    }

    public record PlaceDisponible(String place, boolean libre) {}
}
