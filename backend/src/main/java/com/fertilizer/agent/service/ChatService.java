package com.fertilizer.agent.service;

import com.fertilizer.agent.service.GroqService;

import com.fertilizer.agent.model.ChatHistory;
import com.fertilizer.agent.repository.ChatHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final GroqService groqService;

    public ChatService(ChatHistoryRepository chatHistoryRepository, GroqService groqService) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.groqService = groqService;
    }

    public ChatHistory processMessage(String sessionId, String userMessage) {
        // 1. Fetch history
        List<ChatHistory> history = chatHistoryRepository.findBySessionIdOrderByTimestampAsc(sessionId);

        // 2. Save new user message
        ChatHistory userChat = new ChatHistory();
        userChat.setSessionId(sessionId);
        userChat.setRole("user");
        userChat.setContent(userMessage);
        chatHistoryRepository.save(userChat);

        // 3. Get AI Response
        String aiResponse = groqService.generateChatResponse(history, userMessage);

        // 4. Save AI response
        ChatHistory aiChat = new ChatHistory();
        aiChat.setSessionId(sessionId);
        aiChat.setRole("agent");
        aiChat.setContent(aiResponse);
        return chatHistoryRepository.save(aiChat);
    }
    
    public List<ChatHistory> getChatHistory(String sessionId) {
        return chatHistoryRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    }
}
