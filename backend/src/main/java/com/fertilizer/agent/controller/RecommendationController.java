package com.fertilizer.agent.controller;

import com.fertilizer.agent.model.FarmerQuery;
import com.fertilizer.agent.model.Recommendation;
import com.fertilizer.agent.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommend")
@CrossOrigin(origins = "*") // Allows React frontend to call the API
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public ResponseEntity<Recommendation> getRecommendation(@RequestBody FarmerQuery query) {
        try {
            Recommendation recommendation = recommendationService.processQuery(query);
            return ResponseEntity.ok(recommendation);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[ERROR] /api/recommend failed: " + e.getClass().getName() + " - " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
