package com.fertilizer.agent.repository;

import com.fertilizer.agent.model.ChatHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends MongoRepository<ChatHistory, String> {
    List<ChatHistory> findBySessionIdOrderByTimestampAsc(String sessionId);
}
