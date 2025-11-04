package com.project.hotel.repository;

import com.project.hotel.dto.response.AdminDashboardDataResponse;
import com.project.hotel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRoles_RoleName(String roleName);
    List<User> findByUsernameNot(String username);

    @Query("SELECT new com.project.hotel.dto.response.AdminDashboardDataResponse$RoleDistribution(r.roleName, COUNT(u)) " +
            "FROM User u JOIN u.roles r GROUP BY r.roleName")
    List<AdminDashboardDataResponse.RoleDistribution> countUsersByRole();
}
