package com.fertilizer.agent.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Service
public class ChromaDBService {

    @Value("${chroma.api.url}")
    private String chromaUrl;

    private final WebClient webClient;

    public ChromaDBService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public void addDocument(String collectionName, String id, String document, List<Double> embedding) {
        String url = chromaUrl + "/api/v1/collections/" + collectionName + "/add";
        Map<String, Object> body = Map.of(
            "ids", List.of(id),
            "embeddings", List.of(embedding),
            "documents", List.of(document)
        );
        try {
            webClient.post().uri(url).bodyValue(body).retrieve().bodyToMono(Void.class).block();
            System.out.println("Added document to ChromaDB: " + id);
        } catch (Exception e) {
            System.err.println("Error adding to ChromaDB: " + e.getMessage());
        }
    }

    public List<String> searchSimilar(String collectionName, List<Double> queryEmbedding, int nResults) {
        String url = chromaUrl + "/api/v1/collections/" + collectionName + "/query";
        Map<String, Object> body = Map.of(
            "query_embeddings", List.of(queryEmbedding),
            "n_results", nResults
        );
        try {
            Map response = webClient.post().uri(url).bodyValue(body).retrieve().bodyToMono(Map.class).block();
            if (response != null && response.containsKey("documents")) {
                List<List<String>> docs = (List<List<String>>) response.get("documents");
                if (docs != null && !docs.isEmpty()) {
                    return docs.get(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Error querying ChromaDB: " + e.getMessage());
        }
        return List.of("Urea is recommended for low nitrogen in Rice crops.");
    }
    
    public void createCollection(String collectionName) {
        String url = chromaUrl + "/api/v1/collections";
        Map<String, Object> body = Map.of("name", collectionName);
        try {
            webClient.post().uri(url).bodyValue(body).retrieve().bodyToMono(Void.class).block();
        } catch (Exception e) {
            System.err.println("Collection might already exist or error: " + e.getMessage());
        }
    }
}
