package com.project.hotel.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateHotelAdminRequest {
    String businessName;
    String businessAddress;
    String taxCode;
    String licenseNumber;
    String bankAccount;
    String idCardOrPassport;
}
