package com.project.hotel.repository;

import com.project.hotel.entity.HotelAdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelAdminProfileRepository extends JpaRepository<HotelAdminProfile, Integer> {

}
