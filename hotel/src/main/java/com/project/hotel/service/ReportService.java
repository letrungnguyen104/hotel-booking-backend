package com.project.hotel.service;

import com.project.hotel.aspect.LogActivity;
import com.project.hotel.dto.request.CreateNotificationRequest;
import com.project.hotel.dto.request.CreateReportRequest;
import com.project.hotel.dto.response.ReportResponse;
import com.project.hotel.entity.Hotel;
import com.project.hotel.entity.Report;
import com.project.hotel.entity.User;
import com.project.hotel.enums.NotificationType;
import com.project.hotel.enums.ReportStatus;
import com.project.hotel.enums.ReportType;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.repository.HotelRepository;
import com.project.hotel.repository.ReportRepository;
import com.project.hotel.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportService {

    ReportRepository reportRepository;
    UserRepository userRepository;
    HotelRepository hotelRepository;
    NotificationService notificationService;

    @LogActivity("CREATE_REPORT")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void createReport(CreateReportRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User reporter = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        User reportedUser = null;
        Hotel reportedHotel = null;
        String reportedEntityName = "";

        if (request.getReportType() == ReportType.REPORT_USER) {
            if (request.getReportedUserId() == null) {
                throw new AppException(ErrorCode.INVALID_REQUEST);
            }
            reportedUser = userRepository.findById(request.getReportedUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            reportedEntityName = "user: " + reportedUser.getUsername();
        } else if (request.getReportType() == ReportType.REPORT_HOTEL) {
            if (request.getReportedHotelId() == null) {
                throw new AppException(ErrorCode.INVALID_REQUEST);
            }
            reportedHotel = hotelRepository.findById(request.getReportedHotelId())
                    .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
            reportedEntityName = "hotel: " + reportedHotel.getName();
        }

        Report report = Report.builder()
                .reporterUser(reporter)
                .reportedUser(reportedUser)
                .reportedHotel(reportedHotel)
                .reportType(request.getReportType())
                .reason(request.getReason())
                .details(request.getDetails())
                .status(ReportStatus.PENDING)
                .build();

        reportRepository.save(report);
        notifyAdminsOnNewReport(report, reporter, reportedEntityName);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<ReportResponse> getAllReports(String status) {
        List<Report> reports;
        if (status != null && !status.isEmpty()) {
            reports = reportRepository.findByStatusWithDetails(ReportStatus.valueOf(status.toUpperCase()));
        } else {
            reports = reportRepository.findAllWithDetails();
        }

        return reports.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @LogActivity("UPDATE_REPORT")
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ReportResponse updateReportStatus(Integer reportId, String status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND)); // Cần tạo ErrorCode

        report.setStatus(ReportStatus.valueOf(status.toUpperCase()));
        reportRepository.save(report);
        notifyReporterOnStatusUpdate(report);

        return mapToResponse(report);
    }

    private void notifyAdminsOnNewReport(Report report, User reporter, String reportedEntityName) {
        List<User> admins = userRepository.findByRoles_RoleName("ADMIN");
        String title = "New Report Submitted (#" + report.getId() + ")";
        String message = String.format(
                "User '%s' reported %s. Reason: %s",
                reporter.getUsername(),
                reportedEntityName,
                report.getReason()
        );

        for (User admin : admins) {
            CreateNotificationRequest notificationRequest = CreateNotificationRequest.builder()
                    .userId(admin.getId())
                    .title(title)
                    .message(message)
                    .type(NotificationType.SYSTEM.name())
                    .build();
            notificationService.createNotification(notificationRequest);
        }
    }

    private void notifyReporterOnStatusUpdate(Report report) {
        User reporter = report.getReporterUser();
        String title = "";
        String message = "";

        String reportedEntityName = report.getReportType() == ReportType.REPORT_HOTEL
                ? report.getReportedHotel().getName()
                : report.getReportedUser().getUsername();

        if (report.getStatus() == ReportStatus.RESOLVED) {
            title = "Your Report has been Resolved";
            message = String.format(
                    "Your report (#%d) regarding %s has been reviewed and resolved. Thank you for your feedback.",
                    report.getId(),
                    reportedEntityName
            );
        } else if (report.getStatus() == ReportStatus.REJECTED) {
            title = "Your Report was Rejected";
            message = String.format(
                    "Your report (#%d) regarding %s was reviewed, but no violation was found.",
                    report.getId(),
                    reportedEntityName
            );
        } else {
            return;
        }

        CreateNotificationRequest notificationRequest = CreateNotificationRequest.builder()
                .userId(reporter.getId())
                .title(title)
                .message(message)
                .type(NotificationType.SYSTEM.name())
                .build();
        notificationService.createNotification(notificationRequest);
    }

    private ReportResponse mapToResponse(Report report) {
        ReportResponse.UserInfo reporterDto = ReportResponse.UserInfo.builder()
                .id(report.getReporterUser().getId())
                .username(report.getReporterUser().getUsername())
                .fullName(report.getReporterUser().getFullName())
                .build();

        ReportResponse.UserInfo reportedUserDto = null;
        if (report.getReportedUser() != null) {
            reportedUserDto = ReportResponse.UserInfo.builder()
                    .id(report.getReportedUser().getId())
                    .username(report.getReportedUser().getUsername())
                    .fullName(report.getReportedUser().getFullName())
                    .build();
        }

        ReportResponse.HotelInfo reportedHotelDto = null;
        if (report.getReportedHotel() != null) {
            reportedHotelDto = ReportResponse.HotelInfo.builder()
                    .id(report.getReportedHotel().getId())
                    .name(report.getReportedHotel().getName())
                    .build();
        }

        return ReportResponse.builder()
                .id(report.getId())
                .reporterUser(reporterDto)
                .reportedUser(reportedUserDto)
                .reportedHotel(reportedHotelDto)
                .reportType(report.getReportType())
                .reason(report.getReason())
                .details(report.getDetails())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .build();
    }
}