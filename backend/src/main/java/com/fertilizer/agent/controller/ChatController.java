package com.fertilizer.agent.controller;

import com.fertilizer.agent.model.ChatHistory;
import com.fertilizer.agent.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/message")
    public ResponseEntity<ChatHistory> sendMessage(@RequestBody Map<String, String> payload) {
        String sessionId = payload.get("sessionId");
        String message = payload.get("message");
        if (sessionId == null || message == null) {
            return ResponseEntity.badRequest().build();
        }
        
        ChatHistory response = chatService.processMessage(sessionId, message);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatHistory>> getHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(chatService.getChatHistory(sessionId));
    }
}
