package com.example.appbank.chatbot.service;

import com.example.appbank.chatbot.config.ChatbotProperties;
import com.example.appbank.chatbot.dto.ChatRequest;
import com.example.appbank.chatbot.dto.ChatResponse;
import com.example.appbank.chatbot.dto.TelegramUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

@Service
public class TelegramBotService {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ChatbotService chatbotService;
    private final ChatbotProperties properties;

    public TelegramBotService(ChatbotService chatbotService, ChatbotProperties properties) {
        this.chatbotService = chatbotService;
        this.properties = properties;
    }

    public void handleUpdate(TelegramUpdate update) {
        if (!properties.isTelegramEnabled()) {
            return;
        }
        String botToken = properties.getTelegramBotToken();
        if (!StringUtils.hasText(botToken)) {
            log.warn("Telegram bot token not configured; skipping update processing");
            return;
        }
        String chatId = update.chatId();
        String text = update.text();
        if (!StringUtils.hasText(chatId) || !StringUtils.hasText(text)) {
            log.debug("Ignoring Telegram update without chat or text");
            return;
        }

        ChatResponse response = chatbotService.chat(new ChatRequest(text, chatId));
        sendMessage(botToken, chatId, renderMessage(response));
    }

    private String renderMessage(ChatResponse response) {
        StringBuilder builder = new StringBuilder(response.getAnswer());
        if (response.getSources() != null && !response.getSources().isEmpty()) {
            builder.append("\n\nSources:\n");
            String sourcesText = response.getSources().stream()
                    .map(s -> "- " + s.getTitle() + " (" + s.getSource() + ")")
                    .collect(Collectors.joining("\n"));
            builder.append(sourcesText);
        }
        return builder.toString();
    }

    private void sendMessage(String botToken, String chatId, String text) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        var payload = new SendMessageRequest(chatId, text);
        try {
            restTemplate.postForEntity(url, payload, String.class);
        } catch (Exception ex) {
            log.warn("Unable to send message to Telegram chat {}: {}", chatId, ex.getMessage());
        }
    }

    private record SendMessageRequest(String chat_id, String text) {
    }
}
