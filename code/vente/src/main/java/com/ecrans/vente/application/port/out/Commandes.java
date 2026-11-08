package com.ecrans.vente.application.port.out;

import com.ecrans.vente.domaine.commande.Commande;
import com.ecrans.vente.domaine.commun.CommandeId;

import com.ecrans.vente.domaine.commun.SeanceId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Port secondaire : la collection des commandes. */
public interface Commandes {
    Optional<Commande> parId(CommandeId id);

    List<Commande> enAttenteExpireesA(LocalDateTime maintenant);

    List<Commande> parSeance(SeanceId seance);

    void enregistrer(Commande commande);
}
