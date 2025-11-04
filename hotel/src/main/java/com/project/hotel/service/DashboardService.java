package com.project.hotel.service;

import com.project.hotel.dto.response.AdminDashboardDataResponse;
import com.project.hotel.repository.BookingRepository;
import com.project.hotel.repository.HotelRepository;
import com.project.hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public AdminDashboardDataResponse getAdminDashboardData(LocalDate startDate, LocalDate endDate) {

        long totalUsers = userRepository.count();
        long totalHotels = hotelRepository.count();
        List<AdminDashboardDataResponse.RoleDistribution> userRoleDistribution = userRepository.countUsersByRole();
        List<AdminDashboardDataResponse.SimpleHotelInfo> hotelOverview = hotelRepository.getSimpleHotelInfo();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        Map<String, Object> stats = bookingRepository.getRevenueAndCountStats(startDateTime, endDateTime);
        double totalRevenue = (stats.get("totalRevenue") != null) ? (Double) stats.get("totalRevenue") : 0.0;
        long totalBookings = (stats.get("totalBookings") != null) ? (Long) stats.get("totalBookings") : 0L;

        List<AdminDashboardDataResponse.DailyRevenue> revenueOverTime = bookingRepository.getRevenueByDay(startDateTime, endDateTime);

        List<AdminDashboardDataResponse.HotelRevenue> topHotelsByRevenue =
                bookingRepository.getTopHotelsByRevenue(startDateTime, endDateTime, PageRequest.of(0, 5));

        return AdminDashboardDataResponse.builder()
                .totalRevenue(totalRevenue)
                .totalBookings(totalBookings)
                .totalHotels(totalHotels)
                .totalUsers(totalUsers)
                .revenueOverTime(revenueOverTime)
                .userRoleDistribution(userRoleDistribution)
                .topHotelsByRevenue(topHotelsByRevenue)
                .hotelOverview(hotelOverview)
                .build();
    }
}