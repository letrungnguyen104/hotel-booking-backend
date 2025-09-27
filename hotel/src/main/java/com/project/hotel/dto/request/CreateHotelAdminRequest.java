package com.project.hotel.dto.request;

import com.project.hotel.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateHotelAdminRequest {
    int ownerId;
    String businessName;
    String businessAddress;
    String taxCode;
    String licenseNumber;
    String bankAccount;
    String idCardOrPassport;
}
