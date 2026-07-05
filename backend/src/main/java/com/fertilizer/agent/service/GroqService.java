package com.fertilizer.agent.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    @Value("${groq.api.key:}")
    private String apiKey;

    private static final String GROQ_BASE_URL = "https://api.groq.com/openai/v1";
    private static final String MODEL = "llama-3.3-70b-versatile";

    private final WebClient.Builder webClientBuilder;

    public GroqService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public void validateApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("[CONFIG] GROQ_API_KEY is missing! Set the GROQ_API_KEY environment variable.");
        } else {
            System.out.println("[CONFIG] GROQ_API_KEY loaded: " + apiKey.substring(0, Math.min(10, apiKey.length())) + "***");
        }
    }

    private WebClient client() {
        return webClientBuilder
                .baseUrl(GROQ_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String generateRecommendation(String prompt) {
        Map<String, Object> request = Map.of(
                "model", MODEL,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.7
        );
        try {
            Map<?, ?> response = client().post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return extractContent(response);
        } catch (WebClientResponseException e) {
            System.err.println("[Groq] HTTP " + e.getStatusCode() + " — " + e.getResponseBodyAsString());
            return "Error generating recommendation. HTTP " + e.getStatusCode().value();
        } catch (Exception e) {
            System.err.println("[Groq] Error: " + e.getMessage());
            return "Error generating recommendation.";
        }
    }

    public String generateChatResponse(List<com.fertilizer.agent.model.ChatHistory> history, String newMessage) {
        List<Map<String, Object>> messages = new ArrayList<>();
        for (var msg : history) {
            String role = "user".equals(msg.getRole()) ? "user" : "assistant";
            messages.add(Map.of("role", role, "content", msg.getContent()));
        }
        messages.add(Map.of("role", "user", "content", newMessage));

        Map<String, Object> request = Map.of(
                "model", MODEL,
                "messages", messages,
                "temperature", 0.7
        );
        try {
            Map<?, ?> response = client().post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return extractContent(response);
        } catch (WebClientResponseException e) {
            System.err.println("[Groq] Chat HTTP " + e.getStatusCode() + " — " + e.getResponseBodyAsString());
            return "Error generating chat response. HTTP " + e.getStatusCode().value();
        } catch (Exception e) {
            System.err.println("[Groq] Chat Error: " + e.getMessage());
            return "Error generating chat response.";
        }
    }

    /** Groq has no embedding API — returns empty list */
    public List<Double> generateEmbedding(String text) {
        return List.of();
    }

    private String extractContent(Map<?, ?> response) {
        if (response != null && response.containsKey("choices")) {
            @SuppressWarnings("unchecked")
            List<Map<?, ?>> choices = (List<Map<?, ?>>) response.get("choices");
            if (!choices.isEmpty()) {
                Map<?, ?> message = (Map<?, ?>) choices.get(0).get("message");
                if (message != null) {
                    return (String) message.get("content");
                }
            }
        }
        return "No response generated.";
    }
}
