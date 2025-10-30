package com.project.hotel.dto.response;

import com.project.hotel.enums.PromotionStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PromotionResponse {
    private Integer id;
    private String title;
    private String description;
    private String code;
    private Double discountPercent;
    private Double maxDiscountAmount;
    private Double minSpend;
    private LocalDate startDate;
    private LocalDate endDate;
    private String imageUrl;
    private boolean isFeatured;
    private PromotionStatus status;
}