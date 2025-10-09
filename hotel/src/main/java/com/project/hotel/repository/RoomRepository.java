package com.project.hotel.repository;

import com.project.hotel.entity.Room;
import com.project.hotel.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    boolean existsByRoomNumberAndRoomType(String roomNumber, RoomType roomType);
}