package com.securaccess.enterprise.repositories;

import com.securaccess.enterprise.entities.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    
    Optional<Manager> findByEmail(String email);
    
    List<Manager> findByIsActive(Boolean isActive);
    
    // @Query("SELECT m FROM Manager m WHERE m.name LIKE %:name%")
    // List<Manager> findByNameContaining(@Param("name") String name);
    
    // @Query("SELECT m FROM Manager m WHERE m.email = :email AND m.isActive = true")
    // Optional<Manager> findActiveByEmail(@Param("email") String email);
    
    // @Query("SELECT m FROM Manager m WHERE m.department = :department AND m.isActive = true")
    // List<Manager> findActiveByDepartment(@Param("department") String department);
    
    // @Query("SELECT m FROM Manager m WHERE m.createdAt BETWEEN :startDate AND :endDate")
    // List<Manager> findManagersCreatedBetween(@Param("startDate") LocalDateTime startDate,
    //                                        @Param("endDate") LocalDateTime endDate);
    
    // @Query("SELECT COUNT(m) FROM Manager m WHERE m.department = :department AND m.isActive = true")
    // Long countActiveManagersByDepartment(@Param("department") String department);
    
    // @Query("SELECT DISTINCT m.department FROM Manager m WHERE m.isActive = true")
    // List<String> findAllActiveDepartments();
}