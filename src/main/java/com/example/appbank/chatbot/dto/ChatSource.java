package com.example.appbank.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSource {
    private String id;
    private String title;
    private String source;
    private double score;
}
