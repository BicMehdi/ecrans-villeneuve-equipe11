package com.ecrans.vente.application.port.in;

import com.ecrans.vente.domaine.commun.SeanceId;

public interface AnnulerSeance {
    void executer(SeanceId seance);
}
