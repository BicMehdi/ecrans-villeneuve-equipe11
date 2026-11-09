# Les Écrans de Villeneuve

Démonstrateur de la billetterie du multiplexe pour le projet final de Bachelor 3 Informatique. Le domaine Vente gère un stock de fauteuils partagé, la réservation de dix minutes, la confirmation de paiement, l’expiration et les ventes déjà payées chez SortiesFlash.

**Équipe 11** — Mehdi, Jordan et Christ  
**Dépôt distant** — [BicMehdi/ecrans-villeneuve-equipe11](https://github.com/BicMehdi/ecrans-villeneuve-equipe11)

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

Le profil `jpa` démontre le verrou optimiste avec H2. Le profil `postgres` active le même adapter JPA sur PostgreSQL : consultez [`code/vente/README.md`](code/vente/README.md) pour les variables et la commande Docker. Les commandes et les événements restent en mémoire dans ce prototype ; la séance et sa version sont persistées.

## Répartition du travail

- **Mehdi** — intégration Spring Boot, API/Swagger, profil PostgreSQL et préparation de la démonstration.
- **Jordan** — modèle `Seance`/`Commande`, scénarios de réservation-paiement et tests automatisés.
- **Christ** — analyse DDD, diagrammes, DAT, ADR et risques d’architecture.
- **En commun** — relecture, essais fonctionnels, préparation de l’oral et validation du rendu.

## État du rendu

Les huit étapes, les scénarios de paiement tardif et dupliqué, la concurrence, le plan, la page de réservation, les adapters H2/PostgreSQL et l’annulation de séance sont implémentés. Le DAT distingue la cible du démonstrateur. Le [protocole de vérification](docs/verification.md) permet de rejouer la charge de 420 fauteuils. La version finale est identifiée par le tag `v1.0`.
