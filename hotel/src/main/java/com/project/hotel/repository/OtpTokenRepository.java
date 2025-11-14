package com.project.hotel.repository;

import com.project.hotel.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    
    Optional<OtpToken> findByEmailAndOtpCodeAndVerifiedTrueAndUsedFalseAndExpireAtAfter(
            String email, String otpCode, LocalDateTime currentTime);
    
    Optional<OtpToken> findTopByEmailOrderByCreatedAtDesc(String email);
    
    @Modifying
    @Query("DELETE FROM OtpToken o WHERE o.expireAt < :currentTime")
    void deleteExpiredTokens(LocalDateTime currentTime);
    
    @Modifying
    @Query("UPDATE OtpToken o SET o.used = true WHERE o.email = :email AND o.verified = true")
    void markAllAsUsedByEmail(String email);
}
