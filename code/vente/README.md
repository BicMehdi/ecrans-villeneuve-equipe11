# Module Vente de places

Java 21, Spring Boot, Maven. Le domaine et l’application sont indépendants de Spring. `ApplicationVente` assemble les ports, les services et les adapters. `VenteConfiguration` expose cette composition à Spring et publie S42, demain à 20 h 30 en salle 3, avec les rangs A à H et 12 places par rang.

## Vérifications

```sh
mvn verify
mvn spring-boot:run
```

Le profil par défaut utilise les séances en mémoire. Pour H2, lancez `mvn spring-boot:run -Dspring-boot.run.profiles=jpa`. Après `mvn package`, le JAR peut aussi se lancer avec `java -jar target/ecrans-vente-1.0.0.jar`.

## Parcours de démonstration

1. Ouvrir `http://127.0.0.1:8080/` et réserver deux fauteuils avec leur catégorie. La page affiche la commande, le total et l’échéance.
2. Simuler le paiement. Le plan rend les fauteuils vendus indisponibles. Dans Swagger, refaire une réservation pour le même fauteuil donne 409.
3. Réserver un autre fauteuil sans payer. À dix minutes, le plan le rend disponible. Le planificateur marque la commande expirée au passage suivant.
4. Dans Swagger, poster une vente SortiesFlash pour d’autres fauteuils. La référence de démonstration est `VIL-aaaammjj-2030-S3` avec la date de demain au démarrage.
5. Simuler l’annulation de S42 en dernier. La séance ferme, les commandes en attente expirent et les commandes payées passent à `A_REMBOURSER`.

| Méthode et route | Usage |
|---|---|
| `POST /seances/{seance}/reservations` | `{"billets":[{"place":"A1","categorie":"PLEIN"}]}` |
| `POST /webhooks/payvia` | Notification `payment.succeeded` avec la commande dans `reference` |
| `POST /partner/v2/orders` | Vente déjà payée au format SortiesFlash |
| `GET /seances/{seance}/places` | Plan et disponibilités actuelles |
| `POST /programmation/seances/{seance}/annulation` | Simulation d’une annulation de programmation |

La page et les endpoints sont destinés à une démonstration locale. La catégorie `ABONNE` simule un droit à la gratuité déjà validé. Les paiements, les remboursements et les événements utilisent des simulateurs. Le DAT décrit l’authentification, les contrôles métier et la persistance prévus pour l’exploitation.
