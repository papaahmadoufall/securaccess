package com.securaccess.enterprise.repositories;

import com.securaccess.enterprise.entities.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HostRepository extends JpaRepository<Host, Long> {
    
    Optional<Host> findByPhone(String phone);
    
    List<Host> findByIsActive(Boolean isActive);
    
    // @Query("SELECT h FROM Host h WHERE h.name LIKE %:name%")
    // List<Host> findByNameContaining(@Param("name") String name);
    
    // @Query("SELECT h FROM Host h WHERE h.company = :company AND h.isActive = true")
    // List<Host> findActiveByCompany(@Param("company") String company);
    
    // @Query("SELECT h FROM Host h WHERE h.createdAt BETWEEN :startDate AND :endDate")
    // List<Host> findHostsCreatedBetween(@Param("startDate") LocalDateTime startDate,
    //                                  @Param("endDate") LocalDateTime endDate);
    
    // @Query("SELECT COUNT(h) FROM Host h WHERE h.company = :company AND h.isActive = true")
    // Long countActiveHostsByCompany(@Param("company") String company);
    
    // @Query("SELECT DISTINCT h.company FROM Host h WHERE h.isActive = true")
    // List<String> findAllActiveCompanies();
    
    // @Query("SELECT h FROM Host h WHERE h.lastVisit < :date")
    // List<Host> findHostsWithLastVisitBefore(@Param("date") LocalDateTime date);
}