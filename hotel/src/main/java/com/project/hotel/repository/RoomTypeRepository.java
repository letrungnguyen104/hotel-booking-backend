package com.project.hotel.repository;

import com.project.hotel.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
    @Query("SELECT DISTINCT r FROM RoomType r LEFT JOIN FETCH r.images")
    List<RoomType> findAllWithImages();

    @Query("SELECT DISTINCT r FROM RoomType r LEFT JOIN FETCH r.amenities")
    List<RoomType> findAllWithAmenities();

    @Query("SELECT DISTINCT r FROM RoomType r LEFT JOIN FETCH r.images WHERE r.hotel.id = :hotelId")
    List<RoomType> findByHotelIdWithImages(int hotelId);
}