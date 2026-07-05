package com.fertilizer.agent.service;

import com.fertilizer.agent.model.FarmerQuery;
import org.springframework.stereotype.Service;

@Service
public class PromptEngineeringService {

    private static final String SYSTEM_PROMPT =
        "You are an expert agricultural scientist and soil nutritionist with 20 years of hands-on field experience across India.\n" +
        "Your task is to generate a UNIQUE, HIGHLY SPECIFIC fertilizer recommendation tailored to EXACTLY the input provided.\n\n" +

        "CRITICAL RULES — YOU MUST FOLLOW ALL OF THESE:\n\n" +

        "RULE 1 — BE CROP AND LOCATION SPECIFIC:\n" +
        "  - Different crops have completely different nutrient requirements. Never give the same recommendation for different crops.\n" +
        "  - For example: Cotton needs high Potassium; Rice needs high Nitrogen; Tomato needs a balanced NPK with emphasis on K during fruiting.\n" +
        "  - Factor in the specific Indian region/state for regional soil characteristics.\n\n" +

        "RULE 2 — RESPECT FARMING PREFERENCE (CRITICAL):\n" +
        "  - If 'Organic Farming': ONLY recommend natural fertilizers (Vermicompost, Farmyard Manure, Neem Cake, Fish Emulsion, Bone Meal, Jeevamrit).\n" +
        "  - If 'Conventional Farming': Recommend chemical fertilizers (Urea, DAP, MOP, SSP, Ammonium Sulphate, etc.).\n\n" +

        "RULE 3 — ANALYZE NPK LEVELS PRECISELY:\n" +
        "  - Nitrogen LOW → Primary need: N-rich fertilizer (Urea for conventional; Vermicompost/Blood Meal for organic).\n" +
        "  - Phosphorus LOW → Primary need: P-rich fertilizer (DAP/SSP for conventional; Bone Meal/Rock Phosphate for organic).\n" +
        "  - Potassium LOW → Primary need: K-rich fertilizer (MOP/SOP for conventional; Wood Ash/Kelp for organic).\n" +
        "  - Multiple nutrients LOW → Recommend compound fertilizer addressing each deficiency.\n" +
        "  - ALL Medium → Still give a CROP-SPECIFIC recommendation (e.g., for Cotton at Maturity use Potassium Sulphate for boll development; for Rice at Vegetative use Urea split dose).\n" +
        "  - ALL High → Recommend soil health maintenance (gypsum, biostimulants, or reduced dosage fertilizer).\n\n" +

        "RULE 4 — GROWTH STAGE MATTERS:\n" +
        "  - Seedling: Low dose, gentle formulas. Avoid high-concentration fertilizers.\n" +
        "  - Vegetative: Nitrogen-heavy for leaf/stem growth.\n" +
        "  - Flowering: Reduce N, increase P and K.\n" +
        "  - Fruiting/Maturity: High K for fruit/grain quality and development.\n\n" +

        "RULE 5 — USE ALL PARAMETERS:\n" +
        "  - The recommendation MUST change meaningfully if: crop changes, location changes, NPK changes, growth stage changes, or problem description changes.\n" +
        "  - Never give a generic recommendation. Always cite specific numbers from the input in your reason.\n\n" +

        "RULE 6 — JSON OUTPUT ONLY:\n" +
        "  Return ONLY a valid JSON object. No markdown, no backticks, no intro text. Strictly follow this structure:\n" +
        "{\n" +
        "  \"recommendedFertilizer\": \"Exact fertilizer name with grade/NPK ratio e.g. Urea (46-0-0) or DAP (18-46-0)\",\n" +
        "  \"reason\": \"Detailed explanation referencing the exact crop, NPK levels, growth stage, soil pH, and problem reported\",\n" +
        "  \"quantity\": \"Exact dosage per acre or per hectare with units\",\n" +
        "  \"applicationMethod\": \"Specific method: Broadcasting / Band Placement / Fertigation / Foliar Spray / Drip\",\n" +
        "  \"timing\": \"When and how often to apply, referencing the growth stage\",\n" +
        "  \"irrigationAdvice\": \"Specific watering guidance before/after application\",\n" +
        "  \"precautions\": \"Handling safety, weather-related warnings, soil toxicity risks\",\n" +
        "  \"organicAlternatives\": \"Specific organic substitutes with quantities\",\n" +
        "  \"expectedImprovement\": \"Timeline in days/weeks with specific visual indicators\"\n" +
        "}\n\n";

    public String buildPrompt(FarmerQuery query, String retrievedContext) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(SYSTEM_PROMPT);

        if (retrievedContext != null && !retrievedContext.isBlank()) {
            prompt.append("Knowledge Base Context:\n");
            prompt.append(retrievedContext).append("\n\n");
        }

        // Build a rich, explicit context block
        prompt.append("=== FARMER'S FIELD DATA ===\n");
        prompt.append("Crop Name        : ").append(nullSafe(query.getCropName(), "Not specified")).append("\n");
        prompt.append("Location/Region  : ").append(nullSafe(query.getLocation(), "Not specified")).append("\n");
        prompt.append("Growth Stage     : ").append(nullSafe(query.getGrowthStage(), "Not specified")).append("\n");
        prompt.append("Soil Type        : ").append(nullSafe(query.getSoilType(), "Not specified")).append("\n");
        prompt.append("Soil pH          : ").append(query.getSoilPh() != null ? query.getSoilPh() : "Not measured").append("\n");
        prompt.append("Farming Type     : ").append(query.isOrganicFarming() ? "ORGANIC FARMING (use only natural fertilizers)" : "CONVENTIONAL FARMING (chemical fertilizers allowed)").append("\n");
        prompt.append("Budget           : ").append(nullSafe(query.getBudget(), "Not specified")).append("\n");
        prompt.append("\n--- Soil Nutrient Levels ---\n");
        prompt.append("Nitrogen (N)     : ").append(nullSafe(query.getNitrogenLevel(), "Not measured")).append("\n");
        prompt.append("Phosphorus (P)   : ").append(nullSafe(query.getPhosphorusLevel(), "Not measured")).append("\n");
        prompt.append("Potassium (K)    : ").append(nullSafe(query.getPotassiumLevel(), "Not measured")).append("\n");
        prompt.append("\n--- Weather & Environment ---\n");
        prompt.append("Soil Moisture    : ").append(query.getMoisture() != null ? query.getMoisture() + "%" : "Not measured").append("\n");
        prompt.append("Temperature      : ").append(query.getTemperature() != null ? query.getTemperature() + "°C" : "Not measured").append("\n");
        prompt.append("Humidity         : ").append(query.getHumidity() != null ? query.getHumidity() + "%" : "Not measured").append("\n");
        prompt.append("Rainfall         : ").append(query.getRainfall() != null ? query.getRainfall() + " mm" : "Not measured").append("\n");
        prompt.append("\n--- Problem Reported ---\n");
        prompt.append(query.getProblemDescription() != null && !query.getProblemDescription().isBlank()
            ? query.getProblemDescription()
            : "No specific problem reported — provide preventive/maintenance recommendation").append("\n");
        prompt.append("===========================\n\n");

        prompt.append("Based on ALL the above field data, generate a SPECIFIC recommendation for ")
              .append(nullSafe(query.getCropName(), "this crop"))
              .append(" at ")
              .append(nullSafe(query.getGrowthStage(), "this growth stage"))
              .append(" stage. Your response must be uniquely tailored to these exact conditions.\n\n");
        prompt.append("Output JSON:");

        return prompt.toString();
    }

    private String nullSafe(String value, String defaultValue) {
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
