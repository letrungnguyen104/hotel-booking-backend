package com.project.hotel.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OTPEntry {
    private String code;
    private long expireAt;
}
