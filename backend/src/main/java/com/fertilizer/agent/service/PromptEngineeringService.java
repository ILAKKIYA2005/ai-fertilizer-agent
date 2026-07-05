package com.fertilizer.agent.service;

import com.fertilizer.agent.model.FarmerQuery;
import org.springframework.stereotype.Service;

@Service
public class PromptEngineeringService {

    private static final String SYSTEM_PROMPT = 
        "You are an expert agricultural scientist with 20 years of experience.\n" +
        "Always recommend fertilizers safely.\n" +
        "Never recommend excessive usage.\n" +
        "Always explain the reason behind every recommendation.\n" +
        "Consider soil nutrients, weather, crop stage, and environmental impact.\n" +
        "Provide your response in structured JSON with the following keys: recommendedFertilizer, reason, quantity, applicationMethod, timing, irrigationAdvice, precautions, organicAlternatives, expectedImprovement.\n\n";

    private static final String FEW_SHOT_EXAMPLES = 
        "Example 1:\n" +
        "Input: Crop: Rice, Nitrogen: Low\n" +
        "Output: { \"recommendedFertilizer\": \"Urea\", \"reason\": \"Urea provides high nitrogen content essential for leaf growth.\", \"applicationMethod\": \"Broadcasting\" }\n\n" +
        "Example 2:\n" +
        "Input: Crop: Tomato, Potassium: Low\n" +
        "Output: { \"recommendedFertilizer\": \"MOP (Muriate of Potash)\", \"reason\": \"Potassium improves fruit quality and disease resistance.\", \"applicationMethod\": \"Soil application\" }\n\n";

    public String buildPrompt(FarmerQuery query, String retrievedContext) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(SYSTEM_PROMPT);
        prompt.append(FEW_SHOT_EXAMPLES);
        
        prompt.append("Retrieved Knowledge Base Context:\n");
        prompt.append(retrievedContext).append("\n\n");
        
        prompt.append("Current Farmer Query:\n");
        prompt.append("Crop: ").append(query.getCropName()).append("\n");
        prompt.append("Location: ").append(query.getLocation()).append("\n");
        prompt.append("Soil pH: ").append(query.getSoilPh()).append("\n");
        prompt.append("Nitrogen: ").append(query.getNitrogenLevel()).append("\n");
        prompt.append("Phosphorus: ").append(query.getPhosphorusLevel()).append("\n");
        prompt.append("Potassium: ").append(query.getPotassiumLevel()).append("\n");
        prompt.append("Moisture: ").append(query.getMoisture()).append("\n");
        prompt.append("Temperature: ").append(query.getTemperature()).append("\n");
        prompt.append("Problem: ").append(query.getProblemDescription()).append("\n\n");
        
        prompt.append("Output:");
        
        return prompt.toString();
    }
}
