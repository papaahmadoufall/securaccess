package com.securaccess.enterprise.repositories;

import com.securaccess.enterprise.entities.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    
    Optional<Worker> findByPhone(String phone);
    
    List<Worker> findByDepartment(String department);
    
    List<Worker> findByIsActive(Boolean isActive);
    
    List<Worker> findByDepartmentAndIsActive(String department, Boolean isActive);
    
    // @Query("SELECT w FROM Worker w WHERE w.name LIKE %:name%")
    // List<Worker> findByNameContaining(@Param("name") String name);
    
    // @Query("SELECT w FROM Worker w WHERE w.lastAccess < :date")
    // List<Worker> findWorkersWithLastAccessBefore(@Param("date") LocalDateTime date);
    
    // @Query("SELECT w FROM Worker w WHERE w.createdAt BETWEEN :startDate AND :endDate")
    // List<Worker> findWorkersCreatedBetween(@Param("startDate") LocalDateTime startDate, 
    //                                      @Param("endDate") LocalDateTime endDate);
    
    // @Query("SELECT COUNT(w) FROM Worker w WHERE w.department = :department AND w.isActive = true")
    // Long countActiveWorkersByDepartment(@Param("department") String department);
    
    // @Query("SELECT DISTINCT w.department FROM Worker w WHERE w.isActive = true")
    // List<String> findAllActiveDepartments();
}