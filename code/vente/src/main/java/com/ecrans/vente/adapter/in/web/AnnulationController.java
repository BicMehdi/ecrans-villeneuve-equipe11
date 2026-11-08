package com.ecrans.vente.adapter.in.web;

import com.ecrans.vente.application.port.in.AnnulerSeance;
import com.ecrans.vente.domaine.commun.SeanceId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnnulationController {
    private final AnnulerSeance annuler;

    public AnnulationController(AnnulerSeance annuler) {
        this.annuler = annuler;
    }

    @PostMapping("/programmation/seances/{seance}/annulation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void annuler(@PathVariable String seance) {
        annuler.executer(new SeanceId(seance));
    }
}
