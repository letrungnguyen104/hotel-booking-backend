package com.project.hotel.dto.request;

import com.project.hotel.enums.ReportType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateReportRequest {

    Integer reportedUserId;
    Integer reportedHotelId;

    @NotNull(message = "Report type is required")
    ReportType reportType;

    @NotEmpty(message = "Reason is required")
    String reason;

    String details;
}