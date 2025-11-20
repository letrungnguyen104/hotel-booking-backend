package com.project.hotel.repository;

import com.project.hotel.entity.WithdrawalRequest;
import com.project.hotel.enums.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Integer> {
    List<WithdrawalRequest> findByWallet_User_IdOrderByCreatedAtDesc(Integer userId);
    List<WithdrawalRequest> findByStatusOrderByCreatedAtDesc(WithdrawalStatus status);
}
