# App Bank

Application Spring Boot pour gérer des clients et comptes bancaires avec une base H2 en mémoire.

> **Statut actuel :** backend fonctionnel et sécurisé (JWT) avec données d'exemple **et** client Angular prêt (login, dashboard, clients, comptes, chatbot). Le seul blocage restant est l'accès Maven Central (HTTP 403) dans cet environnement, qui empêche l'exécution des builds/tests.

## Démarrage rapide

```bash
mvn spring-boot:run
```

Une fois l'application démarrée :
- API REST disponible sous `/api`
- Documentation Swagger UI : `http://localhost:8080/swagger-ui.html`
- Console H2 : `http://localhost:8080/h2-console`

## Frontend Angular

Un client Angular minimal est fourni dans le dossier `frontend` pour consommer l'API sécurisée (JWT) et afficher le tableau de bord, les clients, les comptes et le chatbot.

```bash
cd frontend
npm install
npm run start
```

Le serveur de dev tourne sur http://localhost:4200. Authentifiez-vous avec l'admin seedé (`admin` / `admin123` par défaut) puis naviguez entre Dashboard, Clients, Comptes et Chatbot. Si l'API tourne sur une autre URL, modifiez `frontend/src/environments/environment.ts`.

### Authentification JWT

- Un compte par défaut est créé : `admin` / `admin`.
- Récupérer un jeton :

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin"}'
```

- Ajouter le jeton `Bearer` dans l'en-tête `Authorization` pour appeler les autres endpoints.

Profil utilisateur et changement de mot de passe :

```bash
# Récupérer le profil (nom d'utilisateur + rôles)
curl -H 'Authorization: Bearer <JETON>' http://localhost:8080/api/users/me

# Mettre à jour le mot de passe
curl -X PUT http://localhost:8080/api/users/me/password \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <JETON>' \
  -d '{"currentPassword":"admin","newPassword":"monNouveauMotDePasse"}'
```

### Exemples d'appels

Création d'un compte courant :

```bash
curl -X POST http://localhost:8080/api/accounts/current \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <JETON>' \
  -d '{"initialBalance":5000,"overdraft":1000,"customerId":1}'
```

Débit/crédit d'un compte (avec validation) :

```bash
curl -X POST http://localhost:8080/api/accounts/{accountId}/debit \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <JETON>' \
  -d '{"amount":250,"description":"Retrait"}'
```

Synthèse pour le tableau de bord (totaux clients/comptes/ops, soldes, débits/crédits) :

```bash
curl -H 'Authorization: Bearer <JETON>' http://localhost:8080/api/dashboard/summary
```

Les messages d'erreur sont retournés au format [RFC 7807](https://datatracker.ietf.org/doc/html/rfc7807) avec un HTTP status explicite (404 si ressource absente, 400 pour une requête invalide).

## Principales fonctionnalités
- Gestion des clients (création, mise à jour, suppression, recherche)
- Création de comptes courants et épargne
- Opérations de débit, crédit et virement avec suivi de l'utilisateur ayant effectué l'opération
- Historique des opérations d'un compte
- Traçabilité automatique : chaque client et compte porte les champs `createdBy` / `updatedBy` ainsi que les dates `createdAt` / `updatedAt` renseignées à partir de l'utilisateur authentifié (ou `system` pour les données d'initialisation)
- Initialisation de données d'exemple au démarrage

## État d'avancement

- Backend : API REST sécurisée par JWT (clients, comptes courants/épargne, opérations, dashboard, profil utilisateur, changement de mot de passe) avec données d'exemple et traçabilité.
- Chatbot : endpoint REST RAG simplifié + webhook Telegram optionnel configurables via propriétés.
- Frontend : client Angular 17 prêt avec login, dashboard (agrégats), gestion clients/comptes/opérations et chatbot.

### Prochaines étapes proposées

- **Débloquer le build Maven** : configurer le proxy/miroir ou utiliser un cache Maven local (voir section « Problème de build Maven (403) » et `docs/BUILD_FAQ.md`).
- Lancer les builds/tests une fois la connectivité Maven rétablie (`mvn -DskipTests package`, `npm run test`, etc.).
- Durcir la sécurité et la config prod (base de données persistante, secrets externes, TLS, CI/CD).

## Chatbot RAG + Telegram

Un chatbot léger est exposé via l'API pour répondre aux questions courantes sur l'application à partir d'une base de connaissances intégrée.

- **Endpoint REST** : `POST /api/chatbot/query`

```bash
curl -X POST http://localhost:8080/api/chatbot/query \
  -H 'Content-Type: application/json' \
  -d '{"question":"Comment créer un compte courant ?"}'
```

La réponse inclut un résumé et les sources utilisées.

- **Telegram (optionnel)** : activez le bot en définissant les propriétés `chatbot.telegram-enabled=true` et `chatbot.telegram-bot-token=<votre_token>`, puis déclarez le webhook `https://<host>/api/chatbot/telegram/webhook` via l'API Telegram. Le détail des étapes est disponible dans [docs/CHATBOT.md](docs/CHATBOT.md).

## Problème de build Maven (403)

👉 Besoin d'un pas-à-pas détaillé ? Consultez [docs/BUILD_FAQ.md](docs/BUILD_FAQ.md) et utilisez le script `./scripts/apply-settings-example.sh` pour copier le modèle `settings.xml` dans `~/.m2` avant de le personnaliser.

L'environnement actuel bloque l'accès à Maven Central (réponse HTTP 403) et empêche l'exécution de `mvn package`. Pour construire l'application :

1. S'assurer que la machine dispose d'un accès HTTPS sortant vers Maven Central (https://repo.maven.apache.org/maven2) ou un miroir d'entreprise.
2. Si un proxy est requis, le déclarer dans `~/.m2/settings.xml` (un exemple prêt à l'emploi est fourni dans `settings-example.xml`) :

```xml
<settings>
  <proxies>
    <proxy>
      <id>corp-proxy</id>
      <active>true</active>
      <protocol>https</protocol>
      <host>proxy.example.com</host>
      <port>3128</port>
    </proxy>
  </proxies>
  <mirrors>
    <mirror>
      <id>corp-mirror</id>
      <mirrorOf>central</mirrorOf>
      <url>https://votre-miroir-maven-exemple</url>
    </mirror>
  </mirrors>
</settings>
```

3. Copiez/ajustez `settings-example.xml` vers `~/.m2/settings.xml`, puis relancez `mvn -s ~/.m2/settings.xml -DskipTests package` (ou `mvn spring-boot:run`) une fois la connectivité rétablie.
   - Astuce : si vous disposez déjà d'un `.m2/repository` pré-rempli (cache CI/CD ou machine connectée), vous pouvez le copier sur le poste de build, puis exécuter `mvn -o -DskipTests package` pour travailler hors ligne.
