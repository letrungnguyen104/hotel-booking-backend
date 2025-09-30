package com.project.hotel.repository;

import com.project.hotel.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    @Query(value = """
    SELECT h.id AS id,
           h.name AS name,
           h.city AS city,
           h.country AS country,
           (
              SELECT STRING_AGG(a.amenity, ', ')
              FROM (
                  SELECT DISTINCT rt2.amenities AS amenity
                  FROM room_type rt2
                  WHERE rt2.hotel_id = h.id
              ) a
           ) AS amenities,
           MAX(rt.price_per_night) AS oldPrice,
           MIN(rt.price_per_night) AS newPrice,
           ISNULL(AVG(r.rating), 0) AS stars,
           COUNT(r.id) AS reviewCount
    FROM hotel h
    JOIN room_type rt ON h.id = rt.hotel_id
    JOIN room rm ON rm.room_type_id = rt.id
    LEFT JOIN review r ON h.id = r.hotel_id
    WHERE h.city LIKE %:city%
      AND rt.capacity >= :guests
      AND NOT EXISTS (
          SELECT 1
          FROM booking b
          JOIN booking_room br ON b.id = br.booking_id
          WHERE br.room_id = rm.id
            AND b.check_in_date < :checkOut
            AND b.check_out_date > :checkIn
      )
    GROUP BY h.id, h.name, h.city, h.country
    """, nativeQuery = true)
    List<Object[]> searchHotels(
            @Param("city") String city,
            @Param("guests") int guests,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );


    @Query(value = """
    SELECT TOP 5 
           h.id AS id,
           h.name AS name,
           h.city AS city,
           MIN(rt.price_per_night) AS price,
           ISNULL(AVG(r.rating), 0) AS rating,
           COUNT(r.id) AS reviewCount,
           (SELECT TOP 1 hi.url 
            FROM hotel_image hi 
            WHERE hi.hotel_id = h.id AND hi.is_main = 1) AS mainImage
    FROM hotel h
    LEFT JOIN room_type rt ON h.id = rt.hotel_id
    LEFT JOIN review r ON h.id = r.hotel_id
    WHERE h.city = :city
      AND h.status = 'ACTIVE'
    GROUP BY h.id, h.name, h.city
    ORDER BY AVG(r.rating) DESC, h.id ASC
    """, nativeQuery = true)
    List<Object[]> findTop5HotelsByCity(@Param("city") String city);

    @Query("SELECT h FROM Hotel h WHERE h.owner.id = :ownerId")
    List<Hotel> findByOwnerId(@Param("ownerId") int ownerId);


}
