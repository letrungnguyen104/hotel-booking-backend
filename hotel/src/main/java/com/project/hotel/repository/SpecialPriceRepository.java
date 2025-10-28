package com.project.hotel.repository;

import com.project.hotel.entity.SpecialPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpecialPriceRepository extends JpaRepository<SpecialPrice, Integer> {
    List<SpecialPrice> findByRoomTypeId(Integer roomTypeId);

    @Query("SELECT sp FROM SpecialPrice sp " +
            "WHERE sp.roomType.id = :roomTypeId " +
            "AND :checkInDate BETWEEN sp.startDate AND sp.endDate " +
            "ORDER BY sp.price ASC")
    List<SpecialPrice> findActiveSpecialPrice(
            @Param("roomTypeId") Integer roomTypeId,
            @Param("checkInDate") LocalDate checkInDate
    );
}
