# Oral J5 en solo

Prépare un passage de **7 minutes**, suivi de **5 minutes de questions**. Le plan ci-dessous couvre les cinq parties demandées par le sujet. Il sert de base à ton diaporama.

La fiche officielle associe Mehdi à l’équipe 11 et indique un 7e passage. Elle prévoit un temps de parole partagé entre les membres inscrits. Fais confirmer par l’enseignant le passage seul et le numéro de dépôt à utiliser avant le rendu.

## Déroulé chronométré

1. **0:00 à 1:00 · Le problème.** En mars, 14 fauteuils ont été vendus deux fois. Le site, l’application, les bornes, les guichets et les partenaires partagent donc un seul stock. Cite les trois objectifs du DAT §1 et distingue un résultat d’essai d’un objectif de production.
2. **1:00 à 3:00 · Le domaine.** Montre la carte des contextes. Dans Programmation, une séance est un film à un horaire dans une salle. Dans Vente, elle porte l’état des fauteuils. La Vente est le cœur métier, car une double attribution doit être impossible. Prends la déclaration CNC comme exemple de contexte plus simple, avec un export hebdomadaire.
3. **3:00 à 5:00 · L’architecture et la démo.** Montre le diagramme de composants ou la séquence. Le contrôleur traduit la requête HTTP, le cas d’usage orchestre, puis `Seance` contrôle les fauteuils et `Commande` garde le prix et l’état du paiement. PayVia et SortiesFlash ont chacun un adaptateur qui traduit leur format. Consacre au maximum une minute à la démo. Termine en rappelant qu’un blocage non payé expire après dix minutes.
4. **5:00 à 6:30 · La décision.** Défends l’ADR 0002. Le verrou par séance protège un lot de fauteuils avec une version commune. Un verrou par fauteuil réduirait les conflits entre ventes disjointes, mais rendrait le tout-ou-rien plus difficile. Le coût du choix est qu’une écriture sur une autre place de la même séance peut provoquer un nouvel essai.
5. **6:30 à 7:00 · L’hypothèse.** En cas d’annulation, le partenaire rembourse les clients qu’il a encaissés. Cela évite que le cinéma rembourse sur la base d’un prix de grille qui peut différer du montant réellement payé.

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
- **Que montre le profil H2 ?** Il vérifie la persistance de la séance et les conflits de version. La transaction PostgreSQL qui inclut séance, commande et événements appartient à l’architecture cible.

Avant le J5, répète avec un chronomètre. Vérifie le nom du dépôt avec l’enseignant, puis pousse le tag `v1.0` avec le DAT PDF dans `docs/` et dépose le lien sur Moodle avant le début des passages.
