import assert from 'node:assert/strict';
import { performance } from 'node:perf_hooks';

const base = process.argv[2] ?? 'http://127.0.0.1:8080';
const reference = process.argv[3];
assert.match(reference ?? '', /^VIL-\d{8}-2030-S3$/, 'Passer la référence partenaire de S42 en second argument');
const route = '/seances/S42';
const concurrents = 16;
const placesParCommande = 6;
const delaiLimiteMs = 300_000;

async function requete(path, body) {
  const response = await fetch(base + path, {
    ...(body === undefined ? {} : {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    }),
    signal: AbortSignal.timeout(30_000)
  });
  return { statut: response.status, texte: await response.text() };
}

async function plan() {
  const resultat = await requete(route + '/places');
  assert.equal(resultat.statut, 200, resultat.texte);
  return JSON.parse(resultat.texte);
}

const initial = await plan();
assert.equal(initial.length, 420, 'Démarrer avec --vente.demo.grande-salle=true');
assert.ok(initial.every(({ libre }) => libre), 'Démarrer une instance neuve');
const lots = Array.from({ length: initial.length / placesParCommande }, (_, i) =>
  initial.slice(i * placesParCommande, (i + 1) * placesParCommande).map(p => p.place));
const mesures = [];
const debut = performance.now();
let prochainLot = 0;

async function jouerLot(places, index) {
  const t0 = performance.now();
  const [web, partenaire] = await Promise.all([
    requete(route + '/reservations', {
      billets: places.map(place => ({ place, categorie: 'PLEIN' }))
    }),
    requete('/partner/v2/orders', {
      show_ref: reference,
      seats: places,
      ticket_types: places.map(() => 'ADULT'),
      partner_order_id: 'CHARGE-' + index,
      paid_amount_cents: 6000
    })
  ]);
  assert.deepEqual([web.statut, partenaire.statut].sort(), [201, 409],
    JSON.stringify({ places, web, partenaire }));
  if (web.statut === 201) {
    const commande = JSON.parse(web.texte);
    const paiement = await requete('/webhooks/payvia', {
      event: 'payment.succeeded',
      payment_id: 'charge-' + index,
      reference: commande.commande,
      amount: commande.totalCentimes,
      currency: 'EUR',
      occurred_at: new Date().toISOString()
    });
    assert.equal(paiement.statut, 204, paiement.texte);
  }
  mesures.push({ ms: performance.now() - t0, gagnant: web.statut === 201 ? 'web' : 'partenaire' });
}

await Promise.all(Array.from({ length: concurrents }, async () => {
  while (prochainLot < lots.length) {
    const index = prochainLot++;
    await jouerLot(lots[index], index);
  }
}));

const dureeMs = performance.now() - debut;
assert.ok(dureeMs < delaiLimiteMs, 'Les 420 places doivent être attribuées en moins de 5 minutes');
const final = await plan();
assert.equal(final.length, 420);
assert.ok(final.every(({ libre }) => !libre), 'Toutes les places doivent être indisponibles à la fin');
assert.equal(mesures.length, lots.length);
const temps = mesures.map(m => m.ms).sort((a, b) => a - b);
const web = mesures.filter(m => m.gagnant === 'web').length;
console.log(JSON.stringify({
  placesAttribuees: final.length,
  commandesAcceptees: mesures.length,
  demandesRefusees409: mesures.length,
  commandesWebPayees: web,
  ventesPartenaire: mesures.length - web,
  pairesConcurrentesMax: concurrents,
  dureeMs: Math.round(dureeMs),
  p95ParcoursMs: Math.round(temps[Math.ceil(0.95 * temps.length) - 1])
}, null, 2));
