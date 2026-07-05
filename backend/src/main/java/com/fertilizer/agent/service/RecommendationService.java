package com.fertilizer.agent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fertilizer.agent.model.FarmerQuery;
import com.fertilizer.agent.model.Recommendation;
import com.fertilizer.agent.repository.FarmerQueryRepository;
import com.fertilizer.agent.repository.RecommendationRepository;
import org.springframework.stereotype.Service;

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
        // 1. Save query to MongoDB
        FarmerQuery savedQuery = farmerQueryRepository.save(query);

        // 2. Search Vector DB for context
        String queryText = "Crop: " + query.getCropName() + ", Problem: " + query.getProblemDescription();
        List<Double> queryEmbedding = groqService.generateEmbedding(queryText);
        List<String> retrievedDocs = chromaDBService.searchSimilar("fertilizer_knowledge", queryEmbedding, 3);
        String context = String.join("\n", retrievedDocs);

        // 3. Build Prompt
        String prompt = promptEngineeringService.buildPrompt(savedQuery, context);

        // 4. Send to Groq
        String aiResponse = groqService.generateRecommendation(prompt);

        // 5. Parse JSON and Save Recommendation
        Recommendation recommendation = new Recommendation();
        recommendation.setQueryId(savedQuery.getId());
        
        try {
            // Attempt to parse structured JSON from Gemini response
            // For simplicity, we are mapping it dynamically or using a fallback if formatting fails
            // Assuming aiResponse is a valid JSON string without markdown codeblocks
            String cleanJson = aiResponse.replaceAll("```json", "").replaceAll("```", "").trim();
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
            
        } catch (Exception e) {
            System.err.println("Failed to parse Gemini JSON: " + e.getMessage());
            recommendation.setReason("Raw Output: " + aiResponse); // Fallback
        }

        return recommendationRepository.save(recommendation);
    }
}
