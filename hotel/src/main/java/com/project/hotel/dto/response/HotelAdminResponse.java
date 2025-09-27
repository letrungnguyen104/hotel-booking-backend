package com.project.hotel.dto.response;

import com.project.hotel.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelAdminResponse {
    int id;
    UserResponse owner;
    String businessName;
    String businessAddress;
    String taxCode;
    String licenseNumber;
    String bankAccount;
    String idCardOrPassport;
    int verified;
}
