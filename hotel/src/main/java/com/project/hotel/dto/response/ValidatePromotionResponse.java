package com.project.hotel.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidatePromotionResponse {
    boolean valid;
    String message;
    Double discountAmount;
    Double finalPrice;
}