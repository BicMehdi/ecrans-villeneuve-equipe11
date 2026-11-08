package com.ecrans.vente.adapter.out.memoire;

import com.ecrans.vente.application.port.out.Commandes;
import com.ecrans.vente.domaine.commande.Billet;
import com.ecrans.vente.domaine.commande.Commande;
import com.ecrans.vente.domaine.commande.StatutCommande;
import com.ecrans.vente.domaine.commun.CommandeId;
import com.ecrans.vente.domaine.commun.SeanceId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Adapter secondaire en mémoire. Stocke des copies : oublier d'enregistrer se voit dans les tests. */
public class CommandesEnMemoire implements Commandes {

    private record LigneCommande(CommandeId id, SeanceId seance, List<Billet> billets, LocalDateTime expireA,
                                 StatutCommande statut) {
    }

    private final Map<CommandeId, LigneCommande> lignes = new ConcurrentHashMap<>();

    @Override
    public Optional<Commande> parId(CommandeId id) {
        return Optional.ofNullable(lignes.get(id))
                .map(CommandesEnMemoire::versDomaine);
    }

    @Override
    public List<Commande> enAttenteExpireesA(LocalDateTime maintenant) {
        return lignes.values().stream()
                .filter(l -> l.statut() == StatutCommande.EN_ATTENTE_PAIEMENT && !maintenant.isBefore(l.expireA()))
                .map(CommandesEnMemoire::versDomaine)
                .toList();
    }

    @Override
    public List<Commande> parSeance(SeanceId seance) {
        return lignes.values().stream().filter(l -> l.seance().equals(seance))
                .map(CommandesEnMemoire::versDomaine).toList();
    }

    private static Commande versDomaine(LigneCommande ligne) {
        return Commande.reconstituer(ligne.id(), ligne.seance(), ligne.billets(), ligne.expireA(), ligne.statut());
    }

    @Override
    public void enregistrer(Commande c) {
        lignes.put(c.id(), new LigneCommande(c.id(), c.seance(), c.billets(), c.expireA(), c.statut()));
    }

    public int nombre() {
        return lignes.size();
    }
}
