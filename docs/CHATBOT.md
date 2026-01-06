# Chatbot RAG + Telegram

Cette implémentation fournit un chatbot léger adossé à une base de connaissances intégrée (RAG simplifié) et un webhook facultatif pour Telegram.

## Endpoint REST

- **URL** : `POST /api/chatbot/query`
- **Body** :

```json
{
  "question": "Comment créditer un compte ?",
  "conversationId": "optional-conversation-id"
}
```

- **Réponse** :

```json
{
  "answer": "...",
  "conversationId": "default",
  "sources": [
    {"id":"accounts","title":"Comptes courants et épargne","source":"README.md","score":0.66}
  ]
}
```

## Webhook Telegram (facultatif)

1. Créez un bot via `@BotFather` et récupérez le token.
2. Configurez l'application :

```properties
chatbot.telegram-enabled=true
chatbot.telegram-bot-token=123456:ABCDEF...
chatbot.telegram-webhook-base-url=https://votre-domaine.exemple
```

3. Déployez l'application puis déclarez le webhook auprès de Telegram :

```bash
curl -X POST "https://api.telegram.org/bot$BOT_TOKEN/setWebhook" \
  -H "Content-Type: application/json" \
  -d '{"url": "https://votre-domaine.exemple/api/chatbot/telegram/webhook"}'
```

Les messages texte envoyés au bot seront relayés au service RAG qui répondra avec la meilleure source trouvée.

## Personnalisation de la base de connaissances

La base intégrée est définie dans `ChatbotConfig.defaultKnowledgeBase()`. Ajoutez, supprimez ou modifiez les entrées `ChatDocument` pour refléter votre application (titre, contenu, source). Les réponses utilisent un score de recouvrement de mots-clés pour sélectionner les passages les plus pertinents.
