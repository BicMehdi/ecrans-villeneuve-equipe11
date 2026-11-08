package com.ecrans.vente.adapter.in.web;

import com.ecrans.vente.application.exception.CommandeInconnue;
import com.ecrans.vente.application.exception.SeanceInconnue;
import com.ecrans.vente.application.port.out.ConflitDeVersion;
import com.ecrans.vente.domaine.commun.ErreurMetier;
import com.ecrans.vente.domaine.seance.PlacesIndisponibles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GestionErreurs {
    @ExceptionHandler({PlacesIndisponibles.class, ConflitDeVersion.class})
    ProblemDetail conflit(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler({SeanceInconnue.class, CommandeInconnue.class})
    ProblemDetail introuvable(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ErreurMetier.class)
    ProblemDetail refus(ErreurMetier e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail invalide(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail jsonInvalide() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Corps JSON invalide");
    }
}
