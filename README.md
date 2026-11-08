# Les Écrans de Villeneuve

Démonstrateur de la billetterie du multiplexe pour le projet final de Bachelor 3 Informatique. Le domaine Vente gère un stock de fauteuils partagé, la réservation de dix minutes, la confirmation de paiement, l’expiration et les ventes déjà payées chez SortiesFlash.

**Équipe** — noms et numéro à renseigner.  
**Dépôt distant** — URL à renseigner avant le rendu.

## Démarrer

Installez Java 21 et Maven 3.9 ou ouvrez le module Maven dans IntelliJ.

```sh
cd code/vente
mvn verify
mvn spring-boot:run
```

- [Page de réservation](http://127.0.0.1:8080/)
- [Swagger](http://127.0.0.1:8080/swagger-ui.html)
- [Dossier d’architecture en PDF](docs/DAT-Ecrans-de-Villeneuve.pdf) et [source Word](docs/DAT-Ecrans-de-Villeneuve.docx)
- [Diagrammes éditables et exports](docs/diagrammes/README.md)
- [Déroulé de la restitution](docs/restitution.md)

Pour démontrer le verrou optimiste JPA sur les séances avec H2, arrêtez la première application et lancez `mvn spring-boot:run -Dspring-boot.run.profiles=jpa` depuis `code/vente`. Le module utilise des simulateurs de paiement et de publication. Les commandes restent en mémoire dans les deux profils.

## État du rendu

Les huit étapes, les scénarios de paiement tardif et dupliqué, la concurrence, le plan, la page de réservation, l’adapter H2 et l’annulation de séance sont implémentés. Le DAT distingue la cible du démonstrateur. Renseignez l’équipe et le lien du dépôt, puis publiez la version à remettre avec le tag `v1.0` et déposez le lien sur Moodle.
