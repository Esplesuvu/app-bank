# App Bank

Application Spring Boot pour gérer des clients et comptes bancaires avec une base H2 en mémoire.

## Démarrage rapide

```bash
mvn spring-boot:run
```

Une fois l'application démarrée :
- API REST disponible sous `/api`
- Documentation Swagger UI : `http://localhost:8080/swagger-ui.html`
- Console H2 : `http://localhost:8080/h2-console`

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

Les messages d'erreur sont retournés au format [RFC 7807](https://datatracker.ietf.org/doc/html/rfc7807) avec un HTTP status explicite (404 si ressource absente, 400 pour une requête invalide).

## Principales fonctionnalités
- Gestion des clients (création, mise à jour, suppression, recherche)
- Création de comptes courants et épargne
- Opérations de débit, crédit et virement avec suivi de l'utilisateur ayant effectué l'opération
- Historique des opérations d'un compte
- Traçabilité automatique : chaque client et compte porte les champs `createdBy` / `updatedBy` ainsi que les dates `createdAt` / `updatedAt` renseignées à partir de l'utilisateur authentifié (ou `system` pour les données d'initialisation)
- Initialisation de données d'exemple au démarrage

## État d'avancement

Le backend est opérationnel (API REST sécurisée par JWT pour clients, comptes, opérations, profil utilisateur et changement de mot de passe). Restent à réaliser selon le plan initial :

- Le client Angular (UI + authentification côté front)
- La partie dashboard (ChartJS/ng-chart) pour les métriques
- L'intégration du chatbot RAG/Telegram

## Problème de build Maven (403)

L'environnement actuel bloque l'accès à Maven Central (réponse HTTP 403) et empêche l'exécution de `mvn package`. Pour construire l'application :

1. S'assurer que la machine dispose d'un accès HTTPS sortant vers Maven Central (https://repo.maven.apache.org/maven2) ou un miroir d'entreprise.
   - Un dépôt de secours `https://repo.spring.io/release` est déjà ajouté dans le `pom.xml`. Si le proxy/filtrage bloque aussi ce dépôt, autorisez-le ou remplacez-le par un miroir accessible.
2. Si un proxy est requis, le déclarer dans `~/.m2/settings.xml` :

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
</settings>
```

3. Relancer `mvn -DskipTests package` (ou `mvn spring-boot:run`) une fois la connectivité rétablie.
