package com.project.hotel.controller;

import com.project.hotel.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import com.project.hotel.configuration.WebSocketEventListener;

@RestController
@RequestMapping("/presence")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PresenceController {

    WebSocketEventListener webSocketEventListener;

    @GetMapping("/online-users")
    public ApiResponse<Set<String>> getOnlineUsers() {
        return ApiResponse.<Set<String>>builder()
                .result(webSocketEventListener.getConnectedUsers())
                .build();
    }
}
