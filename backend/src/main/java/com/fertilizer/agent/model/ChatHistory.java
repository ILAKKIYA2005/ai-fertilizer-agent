package com.fertilizer.agent.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "chat_history")
public class ChatHistory {
    @Id
    private String id;
    private String sessionId; // Connects back to a user or specific interaction
    
    private String role; // e.g., "user" or "agent"
    private String content; // the text of the message
    
    private LocalDateTime timestamp = LocalDateTime.now();
}
