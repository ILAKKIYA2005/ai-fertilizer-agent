package com.fertilizer.agent.service;

import com.fertilizer.agent.model.FarmerQuery;
import org.springframework.stereotype.Service;

@Service
public class PromptEngineeringService {

    private static final String SYSTEM_PROMPT = 
        "You are an expert agricultural scientist with 20 years of experience in soil health and crop nutrition.\n" +
        "Your task is to analyze the farmer's query and provide a tailored, highly specific fertilizer recommendation.\n\n" +
        "STRICT RECOMMENDATION RULES:\n" +
        "1. RESPECT FARMING PREFERENCE (CRITICAL):\n" +
        "   - If 'Organic Farming' is requested (true), you MUST recommend a natural organic fertilizer (e.g. Vermicompost, Well-rotted Farmyard Manure, Neem Cake, Fish Emulsion, Bone Meal, Rock Phosphate) as the primary recommendation. DO NOT recommend synthetic chemicals like Urea, DAP, MOP, or Ammonium Sulfate.\n" +
        "   - If 'Conventional Farming' is requested (false), you may recommend standard synthetic chemical fertilizers.\n" +
        "2. ANALYZE NPK NUTRIENT LEVELS:\n" +
        "   - If Nitrogen (N) is Low: Recommend a nitrogen-rich option (e.g., Urea/Ammonium Sulfate for conventional, or blood meal/manure for organic).\n" +
        "   - If Phosphorus (P) is Low: Recommend a phosphorus-rich option (e.g., Single Superphosphate (SSP)/DAP for conventional, or bone meal/rock phosphate for organic).\n" +
        "   - If Potassium (K) is Low: Recommend a potassium-rich option (e.g., Muriate of Potash (MOP)/Potassium Sulfate for conventional, or wood ash/kelp meal for organic).\n" +
        "   - If NPK are balanced or Medium/High: Recommend a balanced maintenance blend or organic compost.\n" +
        "3. ADJUST FOR ENVIRONMENT & WEATHER:\n" +
        "   - Growth Stage: Customize quantity/timing for the specific stage (e.g., Seedling needs less than Fruiting).\n" +
        "   - Soil pH: Explain if the pH affects nutrient uptake (e.g., highly acidic or alkaline soils require adjustment).\n" +
        "   - Rainfall/Moisture: Adjust precautions (e.g., do not apply conventional nitrogen before heavy rain to prevent leaching).\n" +
        "   - Problem Description: Directly address the farmer's observed issues (e.g., yellowing leaves, stunted growth, pests).\n\n" +
        "You must return a valid JSON object ONLY. Do not include any markdown formatting, backticks (like ```json), or conversational intro/outro text. The response must exactly follow this JSON structure:\n" +
        "{\n" +
        "  \"recommendedFertilizer\": \"Name of fertilizer\",\n" +
        "  \"reason\": \"Detailed scientific explanation explaining why this fits their specific NPK levels, soil pH, crop stage, and symptoms\",\n" +
        "  \"quantity\": \"Specific application rate per acre or per plant\",\n" +
        "  \"applicationMethod\": \"How to apply (e.g. Broadcasting, Band Placement, Fertigation, Foliar Spray)\",\n" +
        "  \"timing\": \"Best time and frequency of application\",\n" +
        "  \"irrigationAdvice\": \"Watering instructions before/after application\",\n" +
        "  \"precautions\": \"Handling safety, soil health warnings, and toxicity risks\",\n" +
        "  \"organicAlternatives\": \"If conventional: list organic options. If organic: list additional natural soil builders\",\n" +
        "  \"expectedImprovement\": \"Timeline and visual signs of crop recovery\"\n" +
        "}\n\n";

    public String buildPrompt(FarmerQuery query, String retrievedContext) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(SYSTEM_PROMPT);
        
        if (retrievedContext != null && !retrievedContext.isBlank()) {
            prompt.append("Retrieved Knowledge Base Context:\n");
            prompt.append(retrievedContext).append("\n\n");
        }
        
        prompt.append("Current Farmer Query:\n");
        prompt.append("Crop Name: ").append(query.getCropName()).append("\n");
        prompt.append("Location: ").append(query.getLocation()).append("\n");
        prompt.append("Growth Stage: ").append(query.getGrowthStage()).append("\n");
        prompt.append("Soil Type: ").append(query.getSoilType()).append("\n");
        prompt.append("Soil pH: ").append(query.getSoilPh()).append("\n");
        prompt.append("Farming Preference: ").append(query.isOrganicFarming() ? "Organic Farming" : "Conventional Farming").append("\n");
        prompt.append("Soil Nutrient Levels:\n");
        prompt.append("  - Nitrogen (N): ").append(query.getNitrogenLevel()).append("\n");
        prompt.append("  - Phosphorus (P): ").append(query.getPhosphorusLevel()).append("\n");
        prompt.append("  - Potassium (K): ").append(query.getPotassiumLevel()).append("\n");
        prompt.append("Weather & Environmental Metrics:\n");
        prompt.append("  - Soil Moisture: ").append(query.getMoisture() != null ? query.getMoisture() + "%" : "Not measured").append("\n");
        prompt.append("  - Temperature: ").append(query.getTemperature() != null ? query.getTemperature() + "°C" : "Not measured").append("\n");
        prompt.append("  - Humidity: ").append(query.getHumidity() != null ? query.getHumidity() + "%" : "Not measured").append("\n");
        prompt.append("  - Rainfall: ").append(query.getRainfall() != null ? query.getRainfall() + "mm" : "Not measured").append("\n");
        prompt.append("Problem Description: ").append(query.getProblemDescription() != null && !query.getProblemDescription().isBlank() ? query.getProblemDescription() : "None reported").append("\n\n");
        
        prompt.append("Output JSON:");
        
        return prompt.toString();
    }
}
