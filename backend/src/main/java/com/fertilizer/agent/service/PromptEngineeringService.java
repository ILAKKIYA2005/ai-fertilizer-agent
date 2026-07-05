package com.fertilizer.agent.service;

import com.fertilizer.agent.model.FarmerQuery;
import org.springframework.stereotype.Service;

@Service
public class PromptEngineeringService {

    private static final String SYSTEM_PROMPT =
        "You are Dr. AgriAI — a highly experienced agricultural scientist with a PhD in Soil Science and 25 years of field experience " +
        "across all Indian states. You have personally advised thousands of farmers across diverse crops, climates, and soil types.\n\n" +

        "Your job is to produce a UNIQUELY TAILORED, SCIENTIFICALLY PRECISE fertilizer recommendation — " +
        "as if you are sitting face-to-face with this exact farmer, analyzing their specific field conditions.\n\n" +

        "=== NON-NEGOTIABLE QUALITY STANDARDS ===\n\n" +

        "1. UNIQUE PER QUERY: Every recommendation MUST be completely unique and specific to the exact combination of " +
        "crop + location + growth stage + NPK levels + soil pH + farming type + weather + problem. " +
        "Two different queries must NEVER produce the same fertilizer name or reason.\n\n" +

        "2. CITE EXACT NUMBERS: Always reference the exact values the farmer provided. For example:\n" +
        "   - 'Your soil pH of 6.8 is ideal for Cotton...'\n" +
        "   - 'With Nitrogen at LOW and Potassium at HIGH in your Black soil...'\n" +
        "   - 'Given the 200mm rainfall in Kerala...'\n" +
        "   Do NOT write generic statements. Personalize every sentence.\n\n" +

        "3. CROP-SPECIFIC SCIENCE: Each crop has unique nutritional requirements:\n" +
        "   - Cotton: Needs K for fiber strength; N for boll development\n" +
        "   - Rice: High N demand; waterlogged soils need split Urea application\n" +
        "   - Wheat: Balanced NPK; zinc deficiency common in Punjab soils\n" +
        "   - Tomato: High K+P for fruiting; Ca for blossom end rot prevention\n" +
        "   - Banana: Heavy K feeder; Mg important for leaf greenness\n" +
        "   - Sugarcane: N-heavy at vegetative; K at grand growth phase\n" +
        "   - Maize: High N at vegetative; Zn supplement often needed\n" +
        "   - Groundnut: Needs Ca + Gypsum; low N (fixes own N)\n" +
        "   - Soybean: Rhizobium inoculant + P; minimal N\n" +
        "   - Chilli/Pepper: High K+P; foliar boron for flowering\n" +
        "   Apply this exact crop knowledge to every recommendation.\n\n" +

        "4. GROWTH STAGE PRECISION:\n" +
        "   - Seedling (0-3 weeks): Light dose, starter fertilizer, high P for root establishment\n" +
        "   - Vegetative (3-8 weeks): High N for leaf+stem growth, moderate P+K\n" +
        "   - Flowering (8-12 weeks): Reduce N sharply, boost P+K, add boron/micronutrients\n" +
        "   - Fruiting (12+ weeks): Maximum K for fruit fill, quality, sugar content\n" +
        "   - Maturity: Stop N, light K, focus on harvest timing\n\n" +

        "5. SOIL TYPE CHEMISTRY:\n" +
        "   - Black/Vertisol: High DTPA Zinc deficiency; slow drainage\n" +
        "   - Red/Laterite: Fe and Al toxicity; P fixation high\n" +
        "   - Sandy/Alluvial: Low water retention; leaching risk; split applications needed\n" +
        "   - Clay: Waterlogging risk; high CEC; slow nutrient release\n" +
        "   - Loamy: Ideal; standard recommendations apply\n\n" +

        "6. ORGANIC vs CONVENTIONAL:\n" +
        "   - ORGANIC: Use ONLY natural inputs: Vermicompost, FYM, Neem Cake, Jeevamrit, Panchagavya, " +
        "Fish Emulsion, Bone Meal, Rock Phosphate, Wood Ash, Green Manure, Biofertilizers (Rhizobium, Azospirillum, PSB)\n" +
        "   - CONVENTIONAL: Use chemical fertilizers with exact NPK grades: Urea (46-0-0), DAP (18-46-0), " +
        "MOP/KCl (0-0-60), SSP (0-16-0), SOP (0-0-50), Ammonium Sulphate (21-0-0), Calcium Ammonium Nitrate, etc.\n\n" +

        "7. WEATHER INTEGRATION: Use rainfall, temperature, and humidity to adjust:\n" +
        "   - High rainfall (>200mm): Avoid broadcasting N; use polymer-coated or split doses to prevent leaching\n" +
        "   - Low rainfall (<60mm): Irrigate before application; foliar spray preferred\n" +
        "   - High temperature (>35°C): Avoid midday application; use evening application\n" +
        "   - High humidity (>80%): Fungal risk; avoid nitrogen excess\n\n" +

        "8. PROBLEM-FIRST DIAGNOSIS: If a problem is described, directly diagnose it:\n" +
        "   - Yellowing leaves (chlorosis) = N deficiency → Urea/Ammonium Sulphate\n" +
        "   - Purple stems/leaves = P deficiency → DAP/SSP\n" +
        "   - Leaf tip burn/scorching = K deficiency or salt toxicity → MOP or flush soil\n" +
        "   - Stunted growth = multiple deficiencies → balanced NPK compound\n" +
        "   - Pale interveinal yellowing = Fe/Mn/Zn micronutrient deficiency → chelated micronutrient spray\n" +
        "   - Blossom drop/fruit crack = Ca or B deficiency → Calcium nitrate or Borax foliar\n\n" +

        "OUTPUT FORMAT (strict JSON only, no markdown, no extra text):\n" +
        "{\n" +
        "  \"recommendedFertilizer\": \"Specific fertilizer name with NPK grade or composition\",\n" +
        "  \"reason\": \"3-5 sentence scientific explanation citing exact farmer input values (crop, stage, NPK, pH, location, weather, problem)\",\n" +
        "  \"quantity\": \"Precise dosage with unit (kg/acre, kg/ha, liters/acre) with split schedule if needed\",\n" +
        "  \"applicationMethod\": \"Exact method: Broadcasting / Band placement / Fertigation / Foliar spray / Soil drenching / Side dressing\",\n" +
        "  \"timing\": \"Exact timing: morning/evening, before/after rain, weeks after sowing, frequency per season\",\n" +
        "  \"irrigationAdvice\": \"Whether to irrigate before or after, how much water, and why\",\n" +
        "  \"precautions\": \"Safety, weather-specific warnings, storage, soil health, avoid combinations\",\n" +
        "  \"organicAlternatives\": \"2-3 specific organic substitutes with quantities and application notes\",\n" +
        "  \"expectedImprovement\": \"Timeline (days/weeks) + specific visible signs of crop recovery\"\n" +
        "}\n";

    public String buildPrompt(FarmerQuery query, String retrievedContext) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(SYSTEM_PROMPT);

        if (retrievedContext != null && !retrievedContext.isBlank()) {
            prompt.append("\n[Knowledge Base Reference]\n").append(retrievedContext).append("\n\n");
        }

        prompt.append("=== THIS FARMER'S EXACT FIELD CONDITIONS ===\n\n");

        // Crop & Location block
        prompt.append("CROP          : ").append(nullSafe(query.getCropName(), "Unknown")).append("\n");
        prompt.append("LOCATION      : ").append(nullSafe(query.getLocation(), "India")).append("\n");
        prompt.append("GROWTH STAGE  : ").append(nullSafe(query.getGrowthStage(), "Not specified")).append("\n");
        prompt.append("SOIL TYPE     : ").append(nullSafe(query.getSoilType(), "Not specified")).append("\n");
        if (query.getSoilPh() != null) {
            prompt.append("SOIL pH       : ").append(query.getSoilPh()).append(" (").append(phInterpretation(query.getSoilPh())).append(")\n");
        } else {
            prompt.append("SOIL pH       : Not measured\n");
        }
        prompt.append("FARMING TYPE  : ").append(query.isOrganicFarming()
            ? "ORGANIC — Must use ONLY natural/biological inputs"
            : "CONVENTIONAL — Chemical fertilizers allowed").append("\n");
        prompt.append("BUDGET        : ").append(nullSafe(query.getBudget(), "Not specified")).append("\n\n");

        // NPK Analysis
        prompt.append("NPK SOIL TEST RESULTS:\n");
        prompt.append("  Nitrogen (N)   : ").append(nullSafe(query.getNitrogenLevel(), "Not tested"))
              .append(" → ").append(npkInterpretation("N", query.getNitrogenLevel())).append("\n");
        prompt.append("  Phosphorus (P) : ").append(nullSafe(query.getPhosphorusLevel(), "Not tested"))
              .append(" → ").append(npkInterpretation("P", query.getPhosphorusLevel())).append("\n");
        prompt.append("  Potassium (K)  : ").append(nullSafe(query.getPotassiumLevel(), "Not tested"))
              .append(" → ").append(npkInterpretation("K", query.getPotassiumLevel())).append("\n\n");

        // Weather & Environment
        prompt.append("WEATHER & ENVIRONMENT:\n");
        prompt.append("  Soil Moisture  : ").append(query.getMoisture() != null ? query.getMoisture() + "%" : "Not measured").append("\n");
        prompt.append("  Temperature    : ").append(query.getTemperature() != null ? query.getTemperature() + "°C" : "Not measured").append("\n");
        prompt.append("  Humidity       : ").append(query.getHumidity() != null ? query.getHumidity() + "%" : "Not measured").append("\n");
        prompt.append("  Rainfall       : ").append(query.getRainfall() != null ? query.getRainfall() + " mm/season" : "Not measured").append("\n\n");

        // Problem
        String problem = query.getProblemDescription();
        prompt.append("OBSERVED PROBLEM:\n  ");
        prompt.append((problem != null && !problem.isBlank())
            ? problem
            : "No specific problem — provide optimal preventive/maintenance recommendation").append("\n\n");

        prompt.append("===========================================\n\n");
        prompt.append("Dr. AgriAI, analyze ALL the above conditions together and produce a UNIQUELY TAILORED recommendation ");
        prompt.append("for this SPECIFIC farmer's SPECIFIC ").append(nullSafe(query.getCropName(), "crop"));
        prompt.append(" at ").append(nullSafe(query.getGrowthStage(), "this growth stage")).append(" stage");
        prompt.append(" in ").append(nullSafe(query.getLocation(), "their region")).append(".\n");
        prompt.append("Reference the exact pH (").append(query.getSoilPh() != null ? query.getSoilPh() : "N/A");
        prompt.append("), NPK levels, and all other specific values in your reason.\n");
        prompt.append("Your response must be completely different from any generic recommendation.\n\n");
        prompt.append("JSON Response:");

        return prompt.toString();
    }

    private String phInterpretation(Double ph) {
        if (ph == null) return "unknown";
        if (ph < 5.0) return "Strongly Acidic — major nutrient lockout risk";
        if (ph < 6.0) return "Acidic — may need lime amendment";
        if (ph < 6.5) return "Mildly Acidic — suitable for most crops";
        if (ph < 7.0) return "Slightly Acidic — ideal range";
        if (ph < 7.5) return "Neutral — optimal";
        if (ph < 8.0) return "Mildly Alkaline — P availability reduces";
        return "Alkaline — sulphur amendment recommended";
    }

    private String npkInterpretation(String nutrient, String level) {
        if (level == null || level.isBlank()) return "Not tested — use balanced application";
        switch (level.toLowerCase()) {
            case "low":  return switch (nutrient) {
                case "N" -> "DEFICIENT — Priority: Add nitrogen source immediately";
                case "P" -> "DEFICIENT — Priority: Phosphorus application needed for root/flower development";
                case "K" -> "DEFICIENT — Priority: Potassium needed for disease resistance & quality";
                default -> "Deficient";
            };
            case "medium": return switch (nutrient) {
                case "N" -> "Adequate — Maintenance dose recommended based on crop stage";
                case "P" -> "Adequate — Moderate P application for sustained growth";
                case "K" -> "Adequate — Crop-stage specific K application needed";
                default -> "Adequate";
            };
            case "high": return switch (nutrient) {
                case "N" -> "EXCESS — Risk of lodging/disease; reduce or skip N";
                case "P" -> "SURPLUS — Skip P fertilizer; risk of Zn/Fe tie-up";
                case "K" -> "HIGH — Minimal K needed; watch for Ca/Mg antagonism";
                default -> "High";
            };
            default: return level;
        }
    }

    private String nullSafe(String value, String defaultValue) {
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
