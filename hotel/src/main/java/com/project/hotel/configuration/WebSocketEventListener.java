// src/main/java/com/project/hotel/configuration/WebSocketEventListener.java
package com.project.hotel.configuration;

import com.project.hotel.dto.response.UserPresenceMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketEventListener {

    SimpMessageSendingOperations messagingTemplate;
    Set<String> connectedUsers = Collections.synchronizedSet(new HashSet<>());

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = accessor.getUser();

        if (userPrincipal != null) {
            String username = userPrincipal.getName();
            if (username != null) {
                connectedUsers.add(username);
                log.info("User connected: {}", username);

                UserPresenceMessage presenceMessage = UserPresenceMessage.builder()
                        .username(username)
                        .status("ONLINE")
                        .build();

                messagingTemplate.convertAndSend("/topic/presence", presenceMessage);
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = accessor.getUser();

        if (userPrincipal != null) {
            String username = userPrincipal.getName();
            if (username != null) {
                connectedUsers.remove(username);
                log.info("User disconnected: {}", username);
                UserPresenceMessage presenceMessage = UserPresenceMessage.builder()
                        .username(username)
                        .status("OFFLINE")
                        .build();
                messagingTemplate.convertAndSend("/topic/presence", presenceMessage);
            }
        }
    }

    public Set<String> getConnectedUsers() {
        return connectedUsers;
    }
}