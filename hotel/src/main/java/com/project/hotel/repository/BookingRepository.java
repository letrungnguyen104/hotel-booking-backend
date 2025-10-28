package com.project.hotel.repository;

import com.project.hotel.entity.Booking;
import com.project.hotel.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
}
