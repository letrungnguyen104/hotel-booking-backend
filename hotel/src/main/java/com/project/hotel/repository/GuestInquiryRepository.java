package com.project.hotel.repository;

import com.project.hotel.entity.GuestInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestInquiryRepository extends JpaRepository<GuestInquiry, Integer> {
}