package com.project.hotel.repository;

import com.project.hotel.dto.response.AdminDashboardDataResponse;
import com.project.hotel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName")
    List<User> findByRoles_RoleName(@Param("roleName") String roleName);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username <> :username")
    List<User> findByUsernameNot(@Param("username") String username);

    @Query("SELECT new com.project.hotel.dto.response.AdminDashboardDataResponse$RoleDistribution(r.roleName, COUNT(u)) " +
            "FROM User u JOIN u.roles r GROUP BY r.roleName")
    List<AdminDashboardDataResponse.RoleDistribution> countUsersByRole();
}
