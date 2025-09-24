package com.project.hotel.repository;

import com.project.hotel.entity.Hotel;
import com.project.hotel.entity.HotelImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelImageRepository extends JpaRepository<HotelImage, Integer> {
    void deleteByHotel(Hotel hotel);
    List<HotelImage> findByHotel(Hotel hotel);
}
