package com.project.hotel.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import com.project.hotel.configuration.WebSocketEventListener;

@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PresenceController {

    WebSocketEventListener eventListener;

    @GetMapping("/online-users")
    public Set<String> getOnlineUsers() {
        return eventListener.getConnectedUsers();
    }
}
