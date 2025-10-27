package com.project.hotel.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPresenceMessage {
    private String username;
    private String status;
}