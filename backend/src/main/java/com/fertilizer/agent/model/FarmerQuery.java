package com.fertilizer.agent.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "farmer_queries")
public class FarmerQuery {
    @Id
    private String id;
    private String userId; // Optional, if user is logged in
    
    // Core details
    private String cropName;
    private String location;
    private String soilType;
    private Double soilPh;
    
    // NPK levels
    private String nitrogenLevel; // Low, Medium, High
    private String phosphorusLevel;
    private String potassiumLevel;
    
    // Weather metrics
    private Double moisture;
    private Double temperature;
    private Double humidity;
    private Double rainfall;
    
    private String growthStage;
    private boolean organicFarming;
    private String budget;
    private String problemDescription;
    
    private LocalDateTime queryDate = LocalDateTime.now();
}
