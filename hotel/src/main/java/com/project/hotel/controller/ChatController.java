// src/main/java/com/project/hotel/controller/ChatController.java
package com.project.hotel.controller;

import com.project.hotel.dto.request.ChatMessageRequest;
import com.project.hotel.dto.response.ChatMessageResponse;
import com.project.hotel.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {
    SimpMessagingTemplate messagingTemplate;
    ChatService chatService;

    // Client sẽ gửi tin nhắn đến "/app/chat.private"
    @MessageMapping("/chat.private")
    public void handlePrivateMessage(@Payload ChatMessageRequest request, Principal principal) {
        String senderUsername = principal.getName();
        ChatMessageResponse response = chatService.processAndSaveMessage(request, senderUsername);
        // 3. Gửi tin nhắn đến người nhận
        // Gửi đến kênh riêng của người nhận: /user/{username}/queue/messages
        messagingTemplate.convertAndSendToUser(
                response.getReceiverUsername(),
                "/queue/messages",
                response
        );
        messagingTemplate.convertAndSendToUser(
                senderUsername,
                "/queue/messages",
                response
        );
    }
}