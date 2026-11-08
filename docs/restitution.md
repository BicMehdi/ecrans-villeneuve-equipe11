# Restitution devant le comité technique

Utiliser le DAT et ses diagrammes comme support. Répartir la parole entre les membres de l’équipe. Répéter une fois avec le temps annoncé par l’enseignant.

## Déroulé

1. **Le problème · 15 %** — Partir des quatorze doubles ventes en mars. Présenter le stock commun et les objectifs mesurables de la section 1.
2. **Le domaine · 30 %** — Projeter la carte des contextes. Expliquer les deux sens de « séance ». Justifier pourquoi la Vente porte les invariants les plus risqués et pourquoi Déclaration CNC peut rester un export par lot.
3. **L’hexagone · 30 %** — Suivre un appel depuis le contrôleur jusqu’aux agrégats, puis montrer le parcours nominal et l’expiration sur la séquence. Démontrer la réservation, le paiement simulé et le 409 sur une place déjà vendue. Garder un test ouvert en solution de repli.
4. **La concurrence · 20 %** — Expliquer la séparation de Seance et Commande, le verrou optimiste, les trois essais et le coût des conflits entre places différentes. Comparer le prototype en mémoire avec la transaction PostgreSQL et l’outbox prévues pour la cible.
5. **Une hypothèse · 5 %** — Expliquer pourquoi le partenaire rembourse son propre client et comment le cinéma rapproche les reversements.

## Questions à répéter

- **Deux acheteurs veulent G7.** Les deux peuvent lire la même version. Un seul enregistrement est accepté. Après relecture, le second voit la place indisponible.
- **PayVia répond après dix minutes.** La vente reste possible si toutes les places sont disponibles et si la séance accepte encore la vente. Sinon le service demande le remboursement.
- **PayVia envoie la même notification deux fois.** Le statut final déjà enregistré empêche de republier l’événement dans le prototype.
- **Pourquoi le prix partenaire est-il conservé à part ?** Le CNC s’appuie sur le montant payé par le spectateur. Le rapprochement du reversement utilise le tarif de grille diminué de 8 %.
- **Que prouve H2 ?** L’adapter recharge les séances et rejette les versions périmées. La transaction de production qui inclut commande, séance et outbox reste à mettre en place.
