package com.securaccess.enterprise.repositories;

import com.securaccess.enterprise.entities.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, Long> {
    
    Optional<QrCode> findByCode(String code);
    
    List<QrCode> findByActif(Boolean actif);
    
    // Temporarily commenting out complex queries until entity mapping is verified
    // @Query("SELECT qr FROM QrCode qr WHERE qr.dateExpiration < :currentDate")
    // List<QrCode> findExpiredQrCodes(@Param("currentDate") LocalDateTime currentDate);
    
    // @Query("SELECT qr FROM QrCode qr WHERE qr.dateExpiration > :currentDate AND qr.actif = true")
    // List<QrCode> findActiveAndNotExpiredQrCodes(@Param("currentDate") LocalDateTime currentDate);
    
    // @Query("SELECT qr FROM QrCode qr WHERE qr.dateCreation BETWEEN :startDate AND :endDate")
    // List<QrCode> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
    //                                   @Param("endDate") LocalDateTime endDate);
    
    // @Query("SELECT COUNT(qr) FROM QrCode qr WHERE qr.actif = true")
    // Long countActiveQrCodes();
    
    // @Query("SELECT COUNT(qr) FROM QrCode qr WHERE qr.dateExpiration < :currentDate")
    // Long countExpiredQrCodes(@Param("currentDate") LocalDateTime currentDate);
    
    // @Query("SELECT qr FROM QrCode qr WHERE qr.actif = true ORDER BY qr.dateCreation DESC")
    // List<QrCode> findActiveQrCodesOrderByCreatedAtDesc();
    
    // @Query("SELECT qr FROM QrCode qr WHERE qr.code = :code AND qr.actif = true AND qr.dateExpiration > :currentDate")
    // Optional<QrCode> findValidQrCode(@Param("code") String code, @Param("currentDate") LocalDateTime currentDate);
}