package com.fertilizer.agent.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "recommendations")
public class Recommendation {
    @Id
    private String id;
    private String queryId;
    
    private String recommendedFertilizer;
    private String reason;
    private String quantity;
    private String applicationMethod;
    private String timing;
    private String irrigationAdvice;
    private String precautions;
    private String organicAlternatives;
    private String expectedImprovement;
    
    private LocalDateTime generatedAt = LocalDateTime.now();
}
