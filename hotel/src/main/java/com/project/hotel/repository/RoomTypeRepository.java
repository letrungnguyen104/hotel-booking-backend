package com.project.hotel.repository;

import com.project.hotel.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
    @Query("SELECT DISTINCT r FROM RoomType r LEFT JOIN FETCH r.images")
    List<RoomType> findAllWithImages();

    @Query("SELECT DISTINCT r FROM RoomType r LEFT JOIN FETCH r.amenities")
    List<RoomType> findAllWithAmenities();

    @Query("SELECT DISTINCT r FROM RoomType r LEFT JOIN FETCH r.images WHERE r.hotel.id = :hotelId")
    List<RoomType> findByHotelIdWithImages(int hotelId);

    @Query(value = """
    SELECT rt.id, rt.name, rt.description, rt.capacity, rt.price_per_night, rt.status,
           (SELECT COUNT(r.id)
            FROM room r
            WHERE r.room_type_id = rt.id
              AND r.status = 'AVAILABLE'
              AND r.id NOT IN (
                SELECT br.room_id FROM booking b
                JOIN booking_room br ON b.id = br.booking_id
                WHERE b.check_in_date < :checkOut AND b.check_out_date > :checkIn
              )
           ) AS available_rooms_count
    FROM room_type rt
    WHERE rt.hotel_id = :hotelId AND rt.status = 'ACTIVE'
    """, nativeQuery = true)
    List<Object[]> findAvailableRoomTypesByHotelAndDate(
            @Param("hotelId") int hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}