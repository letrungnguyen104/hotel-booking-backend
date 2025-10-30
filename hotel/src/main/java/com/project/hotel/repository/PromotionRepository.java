package com.project.hotel.repository;

import com.project.hotel.entity.Promotion;
import com.project.hotel.enums.PromotionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    List<Promotion> findByStatus(PromotionStatus status);
    List<Promotion> findByIsFeaturedTrueAndStatus(PromotionStatus status);
    Optional<Promotion> findByCodeAndStatus(String code, PromotionStatus status);
    boolean existsByCode(String code);
}