package com.project.hotel.repository;

import com.project.hotel.dto.response.AdminDashboardDataResponse;
import com.project.hotel.dto.response.DashboardDataResponse;
import com.project.hotel.entity.Booking;
import com.project.hotel.enums.BookingStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.hotel h " +
            "LEFT JOIN FETCH h.owner " +
            "LEFT JOIN FETCH b.user u " +
            "WHERE b.user.id = :userId " +
            "ORDER BY b.createdAt DESC")
    List<Booking> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.hotel h " +
            "LEFT JOIN FETCH h.owner " +
            "LEFT JOIN FETCH b.user u " +
            "WHERE b.hotel.owner.id = :ownerId " +
            "ORDER BY b.createdAt DESC")
    List<Booking> findByHotelOwnerIdWithDetails(@Param("ownerId") Integer ownerId);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.hotel h " +
            "LEFT JOIN FETCH h.owner " +
            "LEFT JOIN FETCH b.user u " +
            "WHERE b.hotel.owner.id = :ownerId AND b.status = :status " +
            "ORDER BY b.createdAt DESC")
    List<Booking> findByHotelOwnerIdAndStatusWithDetails(@Param("ownerId") Integer ownerId, @Param("status") BookingStatus status);

    @Query("SELECT new com.project.hotel.dto.response.DashboardDataResponse(" +
            "b.checkInDate as date, " +
            "b.totalPrice as amount, " +
            "b.status as status, " +
            "br.room.roomType.name as roomType, " +
            "rt.capacity as guests) " +
            "FROM Booking b " +
            "JOIN b.bookingRooms br " +
            "JOIN br.room r " +
            "JOIN r.roomType rt " +
            "WHERE b.hotel.owner.id = :ownerId " +
            "AND b.createdAt BETWEEN :startDate AND :endDate")
    List<DashboardDataResponse> findDashboardData(
            @Param("ownerId") Integer ownerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT SUM(b.totalPrice) as totalRevenue, COUNT(b) as totalBookings FROM Booking b " +
            "WHERE b.createdAt BETWEEN :startDate AND :endDate " +
            "AND b.status != 'CANCELLED' AND b.status != 'PENDING'")
    Map<String, Object> getRevenueAndCountStats(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new com.project.hotel.dto.response.AdminDashboardDataResponse$DailyRevenue(CAST(b.createdAt AS DATE), SUM(b.totalPrice)) " +
            "FROM Booking b " +
            "WHERE b.createdAt BETWEEN :startDate AND :endDate AND b.status != 'CANCELLED' AND b.status != 'PENDING' " +
            "GROUP BY CAST(b.createdAt AS DATE) " +
            "ORDER BY CAST(b.createdAt AS DATE)")
    List<AdminDashboardDataResponse.DailyRevenue> getRevenueByDay(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new com.project.hotel.dto.response.AdminDashboardDataResponse$HotelRevenue(h.name, SUM(b.totalPrice)) " +
            "FROM Booking b JOIN b.hotel h " +
            "WHERE b.createdAt BETWEEN :startDate AND :endDate AND b.status != 'CANCELLED' AND b.status != 'PENDING' " +
            "GROUP BY h.name " +
            "ORDER BY SUM(b.totalPrice) DESC")
    List<AdminDashboardDataResponse.HotelRevenue> getTopHotelsByRevenue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

}
