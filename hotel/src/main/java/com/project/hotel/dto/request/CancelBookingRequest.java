package com.project.hotel.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CancelBookingRequest {
    @NotEmpty(message = "Cancellation reason cannot be empty")
    String reason;
}