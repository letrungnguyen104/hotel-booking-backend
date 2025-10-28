package com.project.hotel.repository;

import com.project.hotel.entity.BookingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRoomRepository extends JpaRepository<BookingRoom, Integer> {
    List<BookingRoom> findByBookingId(Integer bookingId);
}
