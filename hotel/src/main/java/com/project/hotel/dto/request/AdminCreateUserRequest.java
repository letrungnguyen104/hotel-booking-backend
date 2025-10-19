package com.project.hotel.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCreateUserRequest {
    String username;
    String email;
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
    String fullName;
    String phoneNumber;
    String address;
    int status = 1;
    Set<String> roles;
}
