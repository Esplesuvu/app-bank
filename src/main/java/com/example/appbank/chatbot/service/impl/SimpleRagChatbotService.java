package com.example.appbank.chatbot.service.impl;

import com.example.appbank.chatbot.config.ChatDocument;
import com.example.appbank.chatbot.config.ChatbotProperties;
import com.example.appbank.chatbot.dto.ChatRequest;
import com.example.appbank.chatbot.dto.ChatResponse;
import com.example.appbank.chatbot.dto.ChatSource;
import com.example.appbank.chatbot.service.ChatbotService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SimpleRagChatbotService implements ChatbotService {

    private static final Pattern TOKEN_SPLIT = Pattern.compile("[^a-z0-9]+");

    private final List<ChatDocument> knowledgeBase;
    private final ChatbotProperties properties;

    public SimpleRagChatbotService(List<ChatDocument> knowledgeBase, ChatbotProperties properties) {
        this.knowledgeBase = knowledgeBase;
        this.properties = properties;
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        String question = request.getQuestion();
        String conversationId = StringUtils.hasText(request.getConversationId()) ? request.getConversationId() : "default";

        if (!properties.isEnabled()) {
            return new ChatResponse(
                    "Le chatbot est désactivé. Activez-le via la propriété chatbot.enabled=true.",
                    conversationId,
                    List.of()
            );
        }

        Set<String> questionTokens = tokenize(question);
        List<ScoredDoc> scored = knowledgeBase.stream()
                .map(doc -> new ScoredDoc(doc, score(questionTokens, tokenize(doc.title() + " " + doc.content()))))
                .filter(it -> it.score > 0)
                .sorted(Comparator.comparingDouble((ScoredDoc it) -> it.score).reversed())
                .limit(Math.max(1, properties.getMaxSources()))
                .toList();

        if (CollectionUtils.isEmpty(scored)) {
            String defaultAnswer = "Je n'ai pas trouvé d'information correspondante dans la base intégrée. " +
                    "Consultez la documentation ou précisez votre question (auth, comptes, opérations, dashboard, chatbot).";
            return new ChatResponse(defaultAnswer, conversationId, List.of());
        }

        String answer = buildAnswer(scored);
        List<ChatSource> sources = scored.stream()
                .map(s -> new ChatSource(s.doc.id(), s.doc.title(), s.doc.source(), s.score))
                .collect(Collectors.toList());
        return new ChatResponse(answer, conversationId, sources);
    }

    private String buildAnswer(List<ScoredDoc> scored) {
        ChatDocument top = scored.get(0).doc;
        StringBuilder builder = new StringBuilder();
        builder.append(top.title()).append(" : ").append(top.content());

        if (scored.size() > 1) {
            builder.append("\n\nAutres sources utiles :");
            scored.stream().skip(1).forEach(item -> builder
                    .append("\n- ")
                    .append(item.doc.title())
                    .append(" (source: ")
                    .append(item.doc.source())
                    .append(")"));
        }
        return builder.toString();
    }

    private Set<String> tokenize(String text) {
        if (!StringUtils.hasText(text)) {
            return Set.of();
        }
        String normalized = text.toLowerCase(Locale.ROOT);
        String[] tokens = TOKEN_SPLIT.split(normalized);
        Set<String> result = new HashSet<>();
        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }
            result.add(token);
        }
        return result;
    }

    private double score(Set<String> questionTokens, Set<String> docTokens) {
        if (questionTokens.isEmpty() || docTokens.isEmpty()) {
            return 0;
        }
        List<String> intersection = new ArrayList<>();
        for (String token : questionTokens) {
            if (docTokens.contains(token)) {
                intersection.add(token);
            }
        }
        return (double) intersection.size() / (double) questionTokens.size();
    }

    private record ScoredDoc(ChatDocument doc, double score) {
    }
}
