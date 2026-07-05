package com.fertilizer.agent.controller;

import com.fertilizer.agent.model.User;
import com.fertilizer.agent.repository.UserRepository;
import com.fertilizer.agent.repository.FarmerQueryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final FarmerQueryRepository farmerQueryRepository;
    private final com.fertilizer.agent.service.GroqService groqService;
    private final com.fertilizer.agent.service.ChromaDBService chromaDBService;

    public AdminController(UserRepository userRepository, 
                           FarmerQueryRepository farmerQueryRepository,
                           com.fertilizer.agent.service.GroqService groqService,
                           com.fertilizer.agent.service.ChromaDBService chromaDBService) {
        this.userRepository = userRepository;
        this.farmerQueryRepository = farmerQueryRepository;
        this.groqService = groqService;
        this.chromaDBService = chromaDBService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalUsers", userRepository.count());
        analytics.put("totalQueriesProcessed", farmerQueryRepository.count());
        // For now returning some mocked/default aggregations for the rest since we need complex Mongo queries
        analytics.put("mostSearchedCrop", "Tomato");
        analytics.put("topLocation", "California");
        analytics.put("avgSoilPh", "6.4");
        analytics.put("organicQueryPercent", "34%");
        return ResponseEntity.ok(analytics);
    }

    @PostMapping("/rebuild-chroma")
    public ResponseEntity<String> rebuildChroma() {
        try {
            chromaDBService.createCollection("fertilizer_knowledge");
            
            // Dummy agricultural dataset for rebuilding
            List<String> knowledgeBase = List.of(
                "Urea is recommended for low nitrogen in Rice crops.",
                "MOP (Muriate of Potash) is recommended for low potassium in Tomato crops.",
                "DAP (Diammonium Phosphate) is recommended for root development in Wheat.",
                "Organic compost can improve soil structure and moisture retention in dry conditions.",
                "For high soil pH, elemental sulfur can be applied to lower the pH for acidic-loving crops."
            );

            for (int i = 0; i < knowledgeBase.size(); i++) {
                String doc = knowledgeBase.get(i);
                List<Double> embedding = groqService.generateEmbedding(doc);
                if (embedding != null && !embedding.isEmpty()) {
                    chromaDBService.addDocument("fertilizer_knowledge", "doc_" + i, doc, embedding);
                }
            }
            return ResponseEntity.ok("ChromaDB rebuilt successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error rebuilding ChromaDB: " + e.getMessage());
        }
    }
}
