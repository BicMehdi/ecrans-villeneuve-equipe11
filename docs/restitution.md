# Oral J5 — équipe 11

Préparez un passage de **7 minutes**, suivi de **5 minutes de questions**. Les trois membres parlent pendant l’oral et doivent pouvoir répondre sur l’ensemble du projet.

La fiche officielle associe Mehdi, Jordan et Christ à l’équipe 11 et indique un 7e passage.

## Déroulé chronométré

1. **0:00 à 2:15 · Mehdi — problème et contexte.** En mars, 14 fauteuils ont été vendus deux fois. Le site, l’application, les bornes, les guichets et les partenaires partagent donc un seul stock. Présenter les trois objectifs du DAT §1, puis la carte des contextes et la Vente comme cœur métier.
2. **2:15 à 4:35 · Jordan — architecture et démo.** Montrer le diagramme de composants ou la séquence. Le contrôleur traduit la requête HTTP, le cas d’usage orchestre, `Seance` contrôle les fauteuils et `Commande` garde le prix et l’état du paiement. Consacrer au maximum une minute à la démo et rappeler l’expiration après dix minutes.
3. **4:35 à 7:00 · Christ — décision et hypothèse.** Défendre l’ADR 0002 : le verrou par séance protège un lot avec une version commune ; le coût est qu’une écriture sur une autre place peut provoquer un nouvel essai. Terminer par l’hypothèse d’annulation : le partenaire rembourse les clients qu’il a encaissés.

## Démo en une minute

Ouvre avant le passage la page de réservation, Swagger avec la requête préparée, et le DAT. Sur une instance neuve, choisis A1 au plein tarif et A2 au tarif enfant. Réserve, montre les **18,00 €** et le compte à rebours, puis simule le paiement. Montre A1 et A2 devenus indisponibles. Dans Swagger, envoie à nouveau une demande pour A1. Le serveur répond **409**.

```http
POST /seances/S42/reservations
Content-Type: application/json

{"billets":[{"place":"A1","categorie":"PLEIN"}]}
```

Si la démo prend du retard, montre directement le diagramme de séquence, puis `ScenarioSoireeDuJeudiTest`. Garde `ExpirerBlocagesTest` ouvert pour montrer l’expiration sans attendre dix minutes.

## Questions à préparer

- **Deux acheteurs demandent le même fauteuil.** `Seance` refuse une place déjà bloquée ou vendue. En mémoire, le démonstrateur sérialise les écritures. Avec H2, l’adaptateur vérifie aussi la version de la séance.
- **Que prouvent les 420 places ?** Dans un essai local Web et SortiesFlash, 70 lots de six places ont été attribués et les 70 demandes concurrentes ont reçu un 409. Les chronos sont ceux du prototype, avec le paiement simulé et une seule instance.
- **Que se passe-t-il si PayVia répond après dix minutes ?** Le serveur peut accepter le paiement tardif si toutes les places sont encore disponibles et si la séance est ouverte à la vente. Sinon, il publie une demande de remboursement. Si la notification arrive deux fois, la commande déjà traitée empêche un second effet.
- **Pourquoi deux agrégats ?** `Seance` protège le stock. `Commande` conserve le prix et gère l’état du paiement. Une commande référence la séance par son identifiant.
- **Que montrent les profils H2 et PostgreSQL ?** Ils vérifient la persistance de la séance et les conflits de version. Le profil PostgreSQL conserve les séances entre les redémarrages. La persistance transactionnelle de la commande et des événements reste une évolution de la cible.

Avant le J5, répétez ensemble avec un chronomètre. Poussez le tag `v1.0` avec le DAT PDF dans `docs/`, puis déposez le lien sur Moodle avant le début des passages.
