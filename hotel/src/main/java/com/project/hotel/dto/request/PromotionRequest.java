package com.project.hotel.dto.request;

import com.project.hotel.enums.PromotionStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PromotionRequest {
    @NotEmpty(message = "Title cannot be empty")
    private String title;

    private String description;

    private String code;

    @NotNull(message = "Discount percent cannot be null")
    private Double discountPercent;

    private Double maxDiscountAmount;
    private Double minSpend;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;

    private String imageUrl;
    private Boolean isFeatured;

    @NotNull(message = "Status cannot be null")
    private PromotionStatus status;
}