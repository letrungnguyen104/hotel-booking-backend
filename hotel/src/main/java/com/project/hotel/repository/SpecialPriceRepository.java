package com.project.hotel.repository;

import com.project.hotel.entity.SpecialPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialPriceRepository extends JpaRepository<SpecialPrice, Integer> {
    List<SpecialPrice> findByRoomTypeId(Integer roomTypeId);
}
