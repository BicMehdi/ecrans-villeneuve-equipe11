# Restitution devant le comité technique

Utiliser le DAT et ses diagrammes comme support. Répartir la parole entre les membres de l’équipe. Répéter une fois avec le temps annoncé par l’enseignant.

## Déroulé

1. **Le problème · 15 %** — Partir des quatorze doubles ventes en mars. Présenter le stock commun et les objectifs mesurables de la section 1.
2. **Le domaine · 30 %** — Projeter la carte des contextes. Expliquer les deux sens de « séance ». Justifier pourquoi la Vente porte les invariants les plus risqués et pourquoi Déclaration CNC peut rester un export par lot.
3. **L’hexagone · 30 %** — Suivre un appel depuis le contrôleur jusqu’aux agrégats, puis montrer le parcours nominal et l’expiration sur la séquence. Démontrer la réservation, le paiement simulé et le 409 sur une place déjà vendue. Garder un test ouvert en solution de repli.
4. **La concurrence · 20 %** — Expliquer la séparation de Seance et Commande, le verrou optimiste, les trois essais et le coût des conflits entre places différentes. Comparer le prototype en mémoire avec la transaction PostgreSQL et l’outbox prévues pour la cible.
5. **Une hypothèse · 5 %** — Expliquer pourquoi le partenaire rembourse son propre client et comment le cinéma rapproche les reversements.

## Démonstration en 90 secondes

- Démarrer une instance neuve. Garder ouverts la page de réservation, Swagger et le DAT.
- Sur S42, choisir A1 au plein tarif et A2 au tarif enfant. Réserver. Faire lire le total de 18,00 € et l’échéance à l’écran.
- Simuler le paiement, puis montrer les deux fauteuils désactivés. Dans Swagger, soumettre de nouveau A1 et montrer le HTTP 409.
- Pour illustrer l’expiration sans attendre dix minutes, ouvrir `ExpirerBlocagesTest` et suivre l’horloge réglable jusqu’à la libération du fauteuil.
- Si la démo bloque, projeter la séquence et ouvrir `ScenarioSoireeDuJeudiTest`. Montrer ensuite les chiffres de l’essai de charge du DAT §10.

## Questions à répéter

- **Deux acheteurs veulent G7.** Les deux peuvent lire la même version. Un seul enregistrement est accepté. Après relecture, le second voit la place indisponible.
- **PayVia répond après dix minutes.** La vente reste possible si toutes les places sont disponibles et si la séance accepte encore la vente. Sinon le service demande le remboursement.
- **PayVia envoie la même notification deux fois.** Le statut final déjà enregistré empêche de republier l’événement dans le prototype.
- **Pourquoi le prix partenaire est-il conservé à part ?** Le CNC s’appuie sur le montant payé par le spectateur. Le rapprochement du reversement utilise le tarif de grille diminué de 8 %.
- **Que prouve H2 ?** L’adapter recharge les séances et rejette les versions périmées. La transaction de production qui inclut commande, séance et outbox reste à mettre en place.

- **Que mesure l’essai de 420 places ?** Le comportement des deux canaux concurrents sur une même instance locale. Chaque lot a un seul gagnant. La mesure ne couvre ni le réseau externe ni les transactions de la cible PostgreSQL.
