package com.fertilizer.agent.repository;

import com.fertilizer.agent.model.FarmerQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FarmerQueryRepository extends MongoRepository<FarmerQuery, String> {
}
