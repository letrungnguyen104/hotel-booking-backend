package com.project.hotel.repository;

import com.project.hotel.entity.BookingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingServiceRepository extends JpaRepository<BookingService, Integer> {
    List<BookingService> findByBookingId(Integer bookingId);
}
