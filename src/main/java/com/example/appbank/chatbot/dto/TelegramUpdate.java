package com.example.appbank.chatbot.dto;

import lombok.Data;

@Data
public class TelegramUpdate {
    private TelegramMessage message;

    @Data
    public static class TelegramMessage {
        private TelegramChat chat;
        private String text;
    }

    @Data
    public static class TelegramChat {
        private Long id;
        private String type;
    }

    public String chatId() {
        if (message == null || message.chat == null || message.chat.id == null) {
            return null;
        }
        return String.valueOf(message.chat.id);
    }

    public String text() {
        return message != null ? message.text : null;
    }
}
