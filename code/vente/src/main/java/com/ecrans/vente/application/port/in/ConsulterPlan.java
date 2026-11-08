package com.ecrans.vente.application.port.in;

import com.ecrans.vente.domaine.commun.PlaceId;
import com.ecrans.vente.domaine.commun.SeanceId;
import java.util.Map;

public interface ConsulterPlan {
    Map<PlaceId, Boolean> executer(SeanceId seance);
}
