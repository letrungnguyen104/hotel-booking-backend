package com.project.hotel.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WithdrawRequestDto {
    @Min(100000)
    Double amount;
    @NotEmpty
    String bankName;
    @NotEmpty
    String bankAccountNumber;
    @NotEmpty
    String accountHolderName;
}