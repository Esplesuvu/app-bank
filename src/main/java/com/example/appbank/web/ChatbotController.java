package com.example.appbank.web;

import com.example.appbank.chatbot.dto.ChatRequest;
import com.example.appbank.chatbot.dto.ChatResponse;
import com.example.appbank.chatbot.service.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/chatbot", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping(path = "/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponse query(@Valid @RequestBody ChatRequest request) {
        return chatbotService.chat(request);
    }
}
