package com.example.appbank.chatbot.service;

import com.example.appbank.chatbot.dto.ChatRequest;
import com.example.appbank.chatbot.dto.ChatResponse;

public interface ChatbotService {
    ChatResponse chat(ChatRequest request);
}
