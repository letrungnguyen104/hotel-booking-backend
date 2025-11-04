package com.project.hotel.repository;

import com.project.hotel.dto.response.AdminDashboardDataResponse;
import com.project.hotel.entity.Hotel;
import com.project.hotel.enums.HotelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

//    @Query(value = """
//    SELECT
//        h.id AS id,
//        h.address AS address,
//        h.name AS name,
//        h.city AS city,
//        h.country AS country,
//        (
//            SELECT STRING_AGG(T.name, ', ')
//            FROM (
//                SELECT DISTINCT am.name
//                FROM room_type rt2
//                JOIN room_type_amenity rta ON rt2.id = rta.room_type_id
//                JOIN amenity am ON rta.amenity_id = am.id
//                WHERE rt2.hotel_id = h.id
//            ) AS T
//        ) AS amenities,
//        MAX(rt.price_per_night) AS oldPrice,
//        MIN(rt.price_per_night) AS newPrice,
//        ISNULL(AVG(CAST(r.rating AS float)), 0) AS stars,
//        COUNT(DISTINCT r.id) AS reviewCount,
//        (
//            SELECT TOP 1 hi.url
//            FROM hotel_image hi
//            WHERE hi.hotel_id = h.id AND hi.is_main = 1
//        ) AS mainImage
//    FROM
//        hotel h
//    JOIN room_type rt ON h.id = rt.hotel_id
//    JOIN room rm ON rm.room_type_id = rt.id
//    LEFT JOIN review r ON h.id = r.hotel_id
//    WHERE
//        h.address LIKE %:address%
//        AND rt.capacity >= :guests
//        AND rm.id NOT IN (
//            SELECT br.room_id
//            FROM booking b
//            JOIN booking_room br ON b.id = br.booking_id
//            WHERE b.check_in_date < :checkOut AND b.check_out_date > :checkIn
//        )
//    GROUP BY
//        h.id, h.address, h.name, h.city, h.country
//    """, nativeQuery = true)
//    List<Object[]> searchHotels(
//            @Param("address") String address,
//            @Param("guests") int guests,
//            @Param("checkIn") LocalDate checkIn,
//            @Param("checkOut") LocalDate checkOut
//    );

    @Query(value = """
    SELECT
        h.id AS id,
        h.address AS address,
        h.name AS name,
        h.city AS city,
        h.country AS country,
        (
            SELECT STRING_AGG(T.name, ', ')
            FROM (
                SELECT DISTINCT am.name
                FROM room_type rt_sub
                JOIN room_type_amenity rta ON rt_sub.id = rta.room_type_id
                JOIN amenity am ON rta.amenity_id = am.id
                WHERE rt_sub.hotel_id = h.id
            ) AS T
        ) AS amenities,
        (SELECT MAX(rt_price.price_per_night) FROM room_type rt_price WHERE rt_price.hotel_id = h.id) AS oldPrice,
        (SELECT MIN(rt_price.price_per_night) FROM room_type rt_price WHERE rt_price.hotel_id = h.id) AS newPrice,
        ISNULL(AVG(CAST(r.rating AS float)), 0) AS stars,
        COUNT(DISTINCT r.id) AS reviewCount,
        (SELECT TOP 1 hi.url FROM hotel_image hi WHERE hi.hotel_id = h.id AND hi.is_main = 1) AS mainImage
    FROM
        hotel h
    LEFT JOIN review r ON h.id = r.hotel_id
    WHERE
        h.address LIKE '%' + ISNULL(:address, '') + '%'
        AND EXISTS (
            SELECT 1
            FROM room_type rt
            JOIN room rm ON rt.id = rm.room_type_id
            WHERE rt.hotel_id = h.id
              AND rt.capacity >= :guests
              AND rm.id NOT IN (
                  SELECT br.room_id
                  FROM booking b
                  JOIN booking_room br ON b.id = br.booking_id
                  WHERE b.check_in_date < :checkOut AND b.check_out_date > :checkIn
              )
        )
    GROUP BY
        h.id, h.address, h.name, h.city, h.country
    """, nativeQuery = true)
    List<Object[]> searchHotels(
            @Param("address") String address,
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

    @Query("""
    SELECT h, COALESCE(AVG(r.rating), 0), COUNT(r.id)
    FROM Hotel h
    LEFT JOIN Review r ON r.hotel.id = h.id
    WHERE h.owner.id = :ownerId
    GROUP BY h
    """)
    List<Object[]> findHotelsByOwnerWithRating(@Param("ownerId") int ownerId);

    @Query("""
    SELECT h, COALESCE(AVG(r.rating), 0), COUNT(r.id)
    FROM Hotel h
    LEFT JOIN Review r ON r.hotel.id = h.id
    WHERE h.owner.id = :ownerId AND h.status = :status
    GROUP BY h
    """)
    List<Object[]> findByOwnerIdAndStatus(@Param("ownerId") int ownerId, @Param("status") HotelStatus status);

    @Query("SELECT new com.project.hotel.dto.response.AdminDashboardDataResponse$SimpleHotelInfo(h.id, h.name, h.owner.username, h.status) " +
            "FROM Hotel h ORDER BY h.createdAt DESC")
    List<AdminDashboardDataResponse.SimpleHotelInfo> getSimpleHotelInfo();

}
