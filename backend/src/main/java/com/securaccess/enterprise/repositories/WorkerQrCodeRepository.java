package com.securaccess.enterprise.repositories;

import com.securaccess.enterprise.entities.WorkerQrCode;
import com.securaccess.enterprise.entities.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerQrCodeRepository extends JpaRepository<WorkerQrCode, Long> {
    
    List<WorkerQrCode> findByWorker(Worker worker);
    
    List<WorkerQrCode> findByCode(String code);
    
    Optional<WorkerQrCode> findByWorkerAndCode(Worker worker, String code);
    
    List<WorkerQrCode> findByWorkerId(Long workerId);
    
    // @Query("SELECT wqr FROM WorkerQrCode wqr WHERE wqr.generatedAt BETWEEN :startDate AND :endDate")
    // List<WorkerQrCode> findByGeneratedAtBetween(@Param("startDate") LocalDateTime startDate,
    //                                            @Param("endDate") LocalDateTime endDate);
    
    // @Query("SELECT wqr FROM WorkerQrCode wqr WHERE wqr.worker.id = :workerId AND wqr.isValid = true")
    // List<WorkerQrCode> findActiveQrCodesByWorkerId(@Param("workerId") Long workerId);
    
    // @Query("SELECT COUNT(wqr) FROM WorkerQrCode wqr WHERE wqr.worker.id = :workerId")
    // Long countQrCodesByWorkerId(@Param("workerId") Long workerId);
}