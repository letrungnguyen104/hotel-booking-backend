package com.project.hotel.repository;

import com.project.hotel.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByBookingId(Integer bookingId);

    @Query("SELECT p FROM Payment p " +
            "JOIN p.booking b " +
            "JOIN b.hotel h " +
            "WHERE h.owner.id = :ownerId " +
            "ORDER BY p.paidAt DESC")
    List<Payment> findPaymentsByHotelOwner(@Param("ownerId") Integer ownerId);
}
