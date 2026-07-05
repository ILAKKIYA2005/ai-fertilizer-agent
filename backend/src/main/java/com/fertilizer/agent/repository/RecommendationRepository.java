package com.fertilizer.agent.repository;

import com.fertilizer.agent.model.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RecommendationRepository extends MongoRepository<Recommendation, String> {
    Optional<Recommendation> findByQueryId(String queryId);
}
