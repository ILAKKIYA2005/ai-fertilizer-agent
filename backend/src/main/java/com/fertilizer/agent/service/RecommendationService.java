package com.fertilizer.agent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fertilizer.agent.model.FarmerQuery;
import com.fertilizer.agent.model.Recommendation;
import com.fertilizer.agent.repository.FarmerQueryRepository;
import com.fertilizer.agent.repository.RecommendationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecommendationService {

    private final GroqService groqService;
    private final ChromaDBService chromaDBService;
    private final PromptEngineeringService promptEngineeringService;
    private final FarmerQueryRepository farmerQueryRepository;
    private final RecommendationRepository recommendationRepository;
    private final ObjectMapper objectMapper;

    public RecommendationService(GroqService groqService,
                                 ChromaDBService chromaDBService,
                                 PromptEngineeringService promptEngineeringService,
                                 FarmerQueryRepository farmerQueryRepository,
                                 RecommendationRepository recommendationRepository) {
        this.groqService = groqService;
        this.chromaDBService = chromaDBService;
        this.promptEngineeringService = promptEngineeringService;
        this.farmerQueryRepository = farmerQueryRepository;
        this.recommendationRepository = recommendationRepository;
        this.objectMapper = new ObjectMapper();
    }

    public Recommendation processQuery(FarmerQuery query) {
        // 1. Try to save query to MongoDB (optional — don't fail if DB is down)
        FarmerQuery savedQuery = query;
        try {
            savedQuery = farmerQueryRepository.save(query);
            System.out.println("[DB] Query saved: " + savedQuery.getId());
        } catch (Exception e) {
            System.err.println("[WARN] MongoDB save failed (continuing without DB): " + e.getMessage());
        }

        // 2. Search Vector DB for context (optional – skip if no embedding available)
        String context = "";
        try {
            String queryText = "Crop: " + query.getCropName() + ", Problem: " + query.getProblemDescription();
            List<Double> queryEmbedding = groqService.generateEmbedding(queryText);
            if (queryEmbedding != null && !queryEmbedding.isEmpty()) {
                List<String> retrievedDocs = chromaDBService.searchSimilar("fertilizer_knowledge", queryEmbedding, 3);
                context = String.join("\n", retrievedDocs);
            } else {
                System.out.println("[INFO] No embeddings available – skipping ChromaDB search.");
            }
        } catch (Exception e) {
            System.err.println("[WARN] ChromaDB search failed, continuing without context: " + e.getMessage());
        }

        // 3. Build Prompt
        String prompt = promptEngineeringService.buildPrompt(savedQuery, context);
        System.out.println("[GROQ] Sending prompt for crop: " + query.getCropName()
                + ", stage: " + query.getGrowthStage()
                + ", N=" + query.getNitrogenLevel()
                + ", P=" + query.getPhosphorusLevel()
                + ", K=" + query.getPotassiumLevel());

        // 4. Send to Groq
        String aiResponse = groqService.generateRecommendation(prompt);
        System.out.println("[GROQ] Response received, length=" + aiResponse.length());

        // 5. Parse JSON and build Recommendation
        Recommendation recommendation = new Recommendation();
        recommendation.setQueryId(savedQuery.getId() != null ? savedQuery.getId() : "local-" + System.currentTimeMillis());
        recommendation.setGeneratedAt(LocalDateTime.now());

        try {
            // Strip any markdown code fences just in case
            String cleanJson = aiResponse
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("```", "")
                    .trim();
            // Extract JSON object if there's extra text
            int start = cleanJson.indexOf('{');
            int end = cleanJson.lastIndexOf('}');
            if (start >= 0 && end > start) {
                cleanJson = cleanJson.substring(start, end + 1);
            }
            Recommendation parsed = objectMapper.readValue(cleanJson, Recommendation.class);

            recommendation.setRecommendedFertilizer(parsed.getRecommendedFertilizer());
            recommendation.setReason(parsed.getReason());
            recommendation.setQuantity(parsed.getQuantity());
            recommendation.setApplicationMethod(parsed.getApplicationMethod());
            recommendation.setTiming(parsed.getTiming());
            recommendation.setIrrigationAdvice(parsed.getIrrigationAdvice());
            recommendation.setPrecautions(parsed.getPrecautions());
            recommendation.setOrganicAlternatives(parsed.getOrganicAlternatives());
            recommendation.setExpectedImprovement(parsed.getExpectedImprovement());
            System.out.println("[OK] Recommendation parsed: " + recommendation.getRecommendedFertilizer());

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to parse Groq JSON: " + e.getMessage());
            System.err.println("[RAW] " + aiResponse);
            recommendation.setRecommendedFertilizer("Recommendation Generated");
            recommendation.setReason("Raw Output: " + aiResponse);
        }

        // 6. Try to save recommendation to MongoDB (optional)
        try {
            recommendation = recommendationRepository.save(recommendation);
            System.out.println("[DB] Recommendation saved: " + recommendation.getId());
        } catch (Exception e) {
            System.err.println("[WARN] MongoDB recommendation save failed (continuing): " + e.getMessage());
        }

        return recommendation;
    }
}
