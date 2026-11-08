# Vérification du rendu v1.0

## Tests automatisés

Depuis `code/vente`, exécuter `mvn verify -Pbonus` avec Java 21. L’exécution de référence donne **66 tests réussis**, dont les contrats HTTP, le paiement à 0 €, le verrou optimiste H2, les paiements tardifs ou dupliqués et l’annulation. Le profil Maven `bonus` active les trois classes bonus d’origine en conservant leurs annotations. Les fichiers de tests fournis sont identiques aux fichiers du ZIP de départ.

| Point évalué | Preuve dans le dépôt |
|---|---|
| Invariants et scénario complet | Tests des étapes 1 à 5 et `ScenarioSoireeDuJeudiTest` |
| Sens des dépendances | `ReserverDesPlacesService` dépend de `GrilleTarifaire`. Voir DAT §10 et les commandes ci-dessous. |
| Contrats HTTP et traduction des erreurs | `ApiVenteTest` et `VentePartenaireApiTest` |
| Prix externe distinct du prix de grille | `VentePartenaireTest` compare le prix de la commande et celui de `VentePartenaireEnregistree`. |
| Frontières, architecture, vues et décisions | DAT §3 à §9 et fichiers sources des diagrammes |
| Contraintes, hypothèses, risques | DAT §1, §2 et §11 |

Depuis `code/vente`, ces recherches avec ripgrep doivent être vides.

```text
rg -n '\.(application|adapter|config)\.|^import org\.springframework' src/main/java/com/ecrans/vente/domaine
rg -n '\.(adapter|config)\.|^import org\.springframework' src/main/java/com/ecrans/vente/application
rg -n '\.now\(\)' src/main/java/com/ecrans/vente/domaine src/main/java/com/ecrans/vente/application src/main/java/com/ecrans/vente/adapter
```

## Vérification de la page

Parcours vérifié dans Edge avec une largeur de 390 pixels. Sélection de A1 au tarif enfant, montant de 6,50 €, confirmation PayVia simulée, siège devenu indisponible. Le plan présentait 96 fauteuils et la page ne débordait pas horizontalement.

## Essai de charge HTTP

Depuis `code/vente`, construire le JAR et lancer une instance neuve.

```sh
mvn verify -Pbonus
java -jar target/ecrans-vente-1.0.0.jar --vente.demo.grande-salle=true
```

Depuis un autre terminal, appeler `node verification/charge.mjs http://127.0.0.1:8080 VIL-aaaammjj-2030-S3`. Remplacer `aaaammjj` par la date du lendemain au démarrage du serveur. Le script utilise Node 22 ou plus et les bibliothèques standard. Pour rejouer sous H2, redémarrer le JAR avec l’argument supplémentaire `--spring.profiles.active=jpa`.

Le script soumet 70 lots de six places à la fois au Web et à SortiesFlash, avec jusqu’à 16 paires en vol. Il exige exactement un statut 201 et un statut 409 par lot, confirme les commandes Web gagnantes par webhook et vérifie les 420 places en fin d’essai. Les latences mesurées par lot incluent la confirmation Web lorsqu’elle est nécessaire. Le temps de démarrage du serveur est exclu.

| Profil | Places | Commandes Web payées | Ventes partenaire | HTTP 409 | Durée | p95 par lot |
|---|---:|---:|---:|---:|---:|---:|
| Mémoire | 420 | 36 | 34 | 70 | 1,19 s | 637 ms |
| JPA / H2 | 420 | 34 | 36 | 70 | 2,23 s | 1053 ms |

Résultats observés localement, sur un seul processus applicatif avec deux canaux et paiements simulés. PostgreSQL, le réseau externe, l’authentification PayVia et les autres canaux restent à éprouver en recette.
