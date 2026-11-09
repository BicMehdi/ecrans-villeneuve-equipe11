# Les Écrans de Villeneuve

Démonstrateur de la billetterie du multiplexe pour le projet final de Bachelor 3 Informatique. Le domaine Vente gère un stock de fauteuils partagé, la réservation de dix minutes, la confirmation de paiement, l’expiration et les ventes déjà payées chez SortiesFlash.

**Équipe 11** — Mehdi, Christ, Jordan  
**Dépôt distant** — [BicMehdi/ecrans-villeneuve-equipe11](https://github.com/BicMehdi/ecrans-villeneuve-equipe11)

## Contributions

Répartition visible dans le dépôt et `docs/restitution.md` (sans inventer de tâches absentes du journal Git) :

| Membre | Rôle / contribution attestée |
|---|---|
| Mehdi | Compte et dépôt GitHub (`BicMehdi`), mise à jour des métadonnées d’équipe / lien distant, préparation de la restitution orale (`docs/restitution.md`) |
| Christ | Membre de l’Équipe 11 (fiche orale J5) — périmètre détaillé à confirmer par l’équipe |
| Jordan | Membre de l’Équipe 11 (fiche orale J5) — périmètre détaillé à confirmer par l’équipe |

<!-- TODO: préciser qui a porté DAT, diagrammes, code Vente et parts d’oral si le passage n’est pas solo. -->

## Démarrer

Installez Java 21 et Maven 3.9 ou ouvrez le module Maven dans IntelliJ.

```sh
cd code/vente
mvn verify -Pbonus
mvn spring-boot:run
```

- [Page de réservation](http://127.0.0.1:8080/)
- [Swagger](http://127.0.0.1:8080/swagger-ui.html)
- [Dossier d’architecture en PDF](docs/DAT-Ecrans-de-Villeneuve.pdf) et [source Word](docs/DAT-Ecrans-de-Villeneuve.docx)
- [Diagrammes éditables et exports](docs/diagrammes/README.md)
- [Déroulé de la restitution](docs/restitution.md)

Pour démontrer le verrou optimiste JPA sur les séances avec H2, arrêtez la première application et lancez `mvn spring-boot:run -Dspring-boot.run.profiles=jpa` depuis `code/vente`. Le module utilise des simulateurs de paiement et de publication. Les commandes restent en mémoire dans les deux profils.

## État du rendu

Les huit étapes, les scénarios de paiement tardif et dupliqué, la concurrence, le plan, la page de réservation, l’adapter H2 et l’annulation de séance sont implémentés. Le DAT distingue la cible du démonstrateur. Le [protocole de vérification](docs/verification.md) permet de rejouer la charge de 420 fauteuils. Version à remettre : tag `v1.0` ; déposer le lien du dépôt sur Moodle avant les passages J5.
