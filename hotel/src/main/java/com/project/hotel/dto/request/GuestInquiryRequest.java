package com.project.hotel.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GuestInquiryRequest {
    @NotEmpty(message = "Name is required")
    String fullName;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    String email;

    @NotEmpty(message = "Message is required")
    String message;
}