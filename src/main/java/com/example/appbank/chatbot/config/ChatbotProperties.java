package com.example.appbank.chatbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chatbot")
public class ChatbotProperties {
    /**
     * When disabled, the REST endpoint still answers politely but indicates the chatbot is off.
     */
    private boolean enabled = true;
    private int maxSources = 3;
    private boolean telegramEnabled = false;
    private String telegramBotToken;
    private String telegramWebhookBaseUrl;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxSources() {
        return maxSources;
    }

    public void setMaxSources(int maxSources) {
        this.maxSources = maxSources;
    }

    public boolean isTelegramEnabled() {
        return telegramEnabled;
    }

    public void setTelegramEnabled(boolean telegramEnabled) {
        this.telegramEnabled = telegramEnabled;
    }

    public String getTelegramBotToken() {
        return telegramBotToken;
    }

    public void setTelegramBotToken(String telegramBotToken) {
        this.telegramBotToken = telegramBotToken;
    }

    public String getTelegramWebhookBaseUrl() {
        return telegramWebhookBaseUrl;
    }

    public void setTelegramWebhookBaseUrl(String telegramWebhookBaseUrl) {
        this.telegramWebhookBaseUrl = telegramWebhookBaseUrl;
    }
}
