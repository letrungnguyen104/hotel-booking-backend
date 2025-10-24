package com.project.hotel.controller;

import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.ChatMessageResponse;
import com.project.hotel.dto.response.ConversationResponse;
import com.project.hotel.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatService chatService;

    @GetMapping("/history/{recipientId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<ChatMessageResponse>> getChatHistory(@PathVariable int recipientId) {
        return ApiResponse.<List<ChatMessageResponse>>builder()
                .result(chatService.getChatHistory(recipientId))
                .build();
    }

    @GetMapping("/conversations")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<ConversationResponse>> getConversations() {
        return ApiResponse.<List<ConversationResponse>>builder()
                .result(chatService.getConversations())
                .build();
    }
}