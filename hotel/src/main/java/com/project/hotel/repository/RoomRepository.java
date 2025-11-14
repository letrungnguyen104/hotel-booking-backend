package com.project.hotel.repository;

import com.project.hotel.entity.Room;
import com.project.hotel.entity.RoomType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    boolean existsByRoomNumberAndRoomType(String roomNumber, RoomType roomType);
//    @Query("SELECT r FROM Room r WHERE r.roomType.id = :roomTypeId " +
//            "AND r.status = 'AVAILABLE' AND r.id NOT IN (" +
//            "  SELECT br.room.id FROM BookingRoom br JOIN br.booking b " +
//            "  WHERE b.status != 'CANCELLED' AND b.status != 'FAILED' " +
//            "  AND b.checkInDate < :checkOut AND b.checkOutDate > :checkIn" +
//            ")")
//    List<Room> findAvailableRoomsByRoomTypeAndDate(
//            @Param("roomTypeId") int roomTypeId,
//            @Param("checkIn") LocalDate checkIn,
//            @Param("checkOut") LocalDate checkOut,
//            Pageable pageable
//    );
//
//    @Query(value = """
//    SELECT r FROM Room r
//    WHERE r.roomType.id = :roomTypeId
//    AND r.status = 'AVAILABLE'
//    AND r.id NOT IN (
//        SELECT br.room.id FROM BookingRoom br
//        WHERE br.booking.id != :bookingIdToExclude
//        AND br.booking.status != 'CANCELLED'
//        AND br.booking.status != 'FAILED'
//        AND br.booking.checkInDate < :checkOut
//        AND br.booking.checkOutDate > :checkIn
//    )
//    """)
//    List<Room> findAvailableRoomsForRetry(
//            @Param("roomTypeId") Integer roomTypeId,
//            @Param("checkIn") LocalDate checkIn,
//            @Param("checkOut") LocalDate checkOut,
//            @Param("bookingIdToExclude") Integer bookingIdToExclude,
//            Pageable pageable
//    );

    @Query(value = """
    SELECT r FROM Room r
    WHERE r.roomType.id = :roomTypeId
    AND r.status = 'AVAILABLE'
    AND r.id NOT IN (
        SELECT br.room.id FROM BookingRoom br
        WHERE (:bookingIdToExclude IS NULL OR br.booking.id != :bookingIdToExclude)
        AND br.booking.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')
        AND br.booking.checkInDate < :checkOut 
        AND br.booking.checkOutDate > :checkIn
    )
    """)
    List<Room> findAvailableRooms(
            @Param("roomTypeId") Integer roomTypeId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("bookingIdToExclude") Integer bookingIdToExclude,
            Pageable pageable
    );
}