package com.securaccess.enterprise.repositories;

import com.securaccess.enterprise.entities.HostQrCode;
import com.securaccess.enterprise.entities.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HostQrCodeRepository extends JpaRepository<HostQrCode, Long> {
    
    List<HostQrCode> findByHost(Host host);
    
    List<HostQrCode> findByCode(String code);
    
    Optional<HostQrCode> findByHostAndCode(Host host, String code);
    
    List<HostQrCode> findByHostId(Long hostId);
    
    // @Query("SELECT hqr FROM HostQrCode hqr WHERE hqr.generatedAt BETWEEN :startDate AND :endDate")
    // List<HostQrCode> findByGeneratedAtBetween(@Param("startDate") LocalDateTime startDate,
    //                                          @Param("endDate") LocalDateTime endDate);
    
    // @Query("SELECT hqr FROM HostQrCode hqr WHERE hqr.host.id = :hostId AND hqr.isValid = true")
    // List<HostQrCode> findActiveQrCodesByHostId(@Param("hostId") Long hostId);
    
    // @Query("SELECT COUNT(hqr) FROM HostQrCode hqr WHERE hqr.host.id = :hostId")
    // Long countQrCodesByHostId(@Param("hostId") Long hostId);
}