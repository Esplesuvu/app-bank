package com.example.appbank.chatbot.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(ChatbotProperties.class)
public class ChatbotConfig {

    @Bean
    public List<ChatDocument> defaultKnowledgeBase() {
        return List.of(
                new ChatDocument(
                        "auth",
                        "Authentification JWT et utilisateurs",
                        "Utilise Spring Security + JWT stateless. Endpoints publics: /api/auth/** (login/register) et /api/chatbot/telegram/webhook. " +
                                "Les autres routes sont protégées via le header Authorization: Bearer <token>. Un compte admin par défaut est seedé (admin/admin).",
                        "README.md"
                ),
                new ChatDocument(
                        "customers",
                        "Gestion des clients",
                        "Endpoints REST pour CRUD client: GET/POST/PUT/DELETE /api/customers. Validation email + nom. Les champs de traçabilité createdBy/updatedBy sont remplis via l'auditeur de sécurité.",
                        "README.md"
                ),
                new ChatDocument(
                        "accounts",
                        "Comptes courants et épargne",
                        "Création: POST /api/accounts/current ou /api/accounts/saving. Les comptes supportent débit, crédit et virement via /api/accounts/{id}/debit|credit et /api/accounts/transfer. " +
                                "Chaque opération enregistre l'utilisateur opérateur et s'affiche dans l'historique.",
                        "README.md"
                ),
                new ChatDocument(
                        "dashboard",
                        "Tableau de bord",
                        "Agrégats disponibles via GET /api/dashboard/summary : totaux clients/comptes/ops, soldes cumulés, somme des débits/crédits. L'UI Angular reste à implémenter pour afficher ces statistiques.",
                        "README.md"
                ),
                new ChatDocument(
                        "chatbot",
                        "Chatbot RAG + Telegram",
                        "Endpoint POST /api/chatbot/query pour poser des questions. Un webhook Telegram facultatif est exposé sous /api/chatbot/telegram/webhook lorsque chatbot.telegram-enabled=true et un token est fourni.",
                        "docs/CHATBOT.md"
                )
        );
    }
}
