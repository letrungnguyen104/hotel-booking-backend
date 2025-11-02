package com.project.hotel.repository;

import com.project.hotel.entity.Report;
import com.project.hotel.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    @Query("SELECT r FROM Report r " +
            "LEFT JOIN FETCH r.reporterUser " +
            "LEFT JOIN FETCH r.reportedUser " +
            "LEFT JOIN FETCH r.reportedHotel " +
            "ORDER BY r.createdAt DESC")
    List<Report> findAllWithDetails();

    @Query("SELECT r FROM Report r " +
            "LEFT JOIN FETCH r.reporterUser " +
            "LEFT JOIN FETCH r.reportedUser " +
            "LEFT JOIN FETCH r.reportedHotel " +
            "WHERE r.status = :status " +
            "ORDER BY r.createdAt DESC")
    List<Report> findByStatusWithDetails(ReportStatus status);
}