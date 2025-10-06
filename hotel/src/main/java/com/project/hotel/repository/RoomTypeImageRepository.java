package com.project.hotel.repository;

import com.project.hotel.entity.RoomType;
import com.project.hotel.entity.RoomTypeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeImageRepository extends JpaRepository<RoomTypeImage, Integer> {
    void deleteByRoomType(RoomType roomType);
    List<RoomTypeImage> findByRoomType(RoomType roomType);
}
