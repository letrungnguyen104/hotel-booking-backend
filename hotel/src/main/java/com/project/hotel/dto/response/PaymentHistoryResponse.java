package com.project.hotel.dto.response;

import com.project.hotel.enums.PaymentMethod;
import com.project.hotel.enums.PaymentStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentHistoryResponse {
    Integer paymentId;
    String transactionId;
    Double amount;
    PaymentMethod method;
    PaymentStatus status;
    LocalDateTime paymentDate;

    Integer bookingId;
    String customerName;
    String customerEmail;
    String roomTypeName;
    LocalDateTime checkInDate;
    LocalDateTime checkOutDate;
}