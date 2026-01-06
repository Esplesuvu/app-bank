package com.example.appbank.web;

import com.example.appbank.chatbot.dto.TelegramUpdate;
import com.example.appbank.chatbot.service.TelegramBotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot/telegram")
public class TelegramWebhookController {

    private final TelegramBotService telegramBotService;

    public TelegramWebhookController(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveWebhook(@RequestBody TelegramUpdate update) {
        telegramBotService.handleUpdate(update);
        return ResponseEntity.ok().build();
    }
}
