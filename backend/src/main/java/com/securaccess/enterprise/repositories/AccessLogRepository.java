package com.securaccess.enterprise.repositories;

import com.securaccess.enterprise.entities.AccessLog;
import com.securaccess.enterprise.entities.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    
    List<AccessLog> findByWorker(Worker worker);
    
    List<AccessLog> findByWorkerId(Long workerId);
    
    // @Query("SELECT al FROM AccessLog al WHERE al.accessTime BETWEEN :startTime AND :endTime")
    // List<AccessLog> findByAccessTimeBetween(@Param("startTime") LocalDateTime startTime, 
    //                                       @Param("endTime") LocalDateTime endTime);
    
    // @Query("SELECT al FROM AccessLog al WHERE al.worker.id = :workerId AND al.accessTime BETWEEN :startTime AND :endTime")
    // List<AccessLog> findByWorkerIdAndAccessTimeBetween(@Param("workerId") Long workerId,
    //                                                  @Param("startTime") LocalDateTime startTime,
    //                                                  @Param("endTime") LocalDateTime endTime);
    
    // @Query("SELECT al FROM AccessLog al WHERE al.zone = :zone AND al.accessTime BETWEEN :startTime AND :endTime")
    // List<AccessLog> findByZoneAndAccessTimeBetween(@Param("zone") String zone,
    //                                              @Param("startTime") LocalDateTime startTime,
    //                                              @Param("endTime") LocalDateTime endTime);
    
    // @Query("SELECT COUNT(al) FROM AccessLog al WHERE al.worker.id = :workerId AND al.accessTime BETWEEN :startTime AND :endTime")
    // Long countAccessesByWorkerAndDateRange(@Param("workerId") Long workerId,
    //                                      @Param("startTime") LocalDateTime startTime,
    //                                      @Param("endTime") LocalDateTime endTime);
    
    // @Query("SELECT al FROM AccessLog al WHERE al.worker.department = :department AND al.accessTime BETWEEN :startTime AND :endTime")
    // List<AccessLog> findByDepartmentAndAccessTimeBetween(@Param("department") String department,
    //                                                    @Param("startTime") LocalDateTime startTime,
    //                                                    @Param("endTime") LocalDateTime endTime);
    
    List<AccessLog> findTop10ByWorkerOrderByTimestampDesc(Worker worker);
    
    // @Query("SELECT DISTINCT al.zone FROM AccessLog al WHERE al.worker.id = :workerId")
    // List<String> findDistinctZonesByWorkerId(@Param("workerId") Long workerId);
}