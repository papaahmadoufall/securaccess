package com.securaccess.enterprise.repositories;

import com.securaccess.enterprise.entities.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    
    List<Evenement> findByNom(String nom);
    
    // @Query("SELECT e FROM Evenement e WHERE e.nom LIKE %:nom%")
    // List<Evenement> findByNomContaining(@Param("nom") String nom);
    
    List<Evenement> findByLieu(String lieu);
    
    // @Query("SELECT e FROM Evenement e WHERE e.dateCreation BETWEEN :startDate AND :endDate")
    // List<Evenement> findByDateCreationBetween(@Param("startDate") LocalDateTime startDate,
    //                                         @Param("endDate") LocalDateTime endDate);
    
    // @Query("SELECT e FROM Evenement e WHERE e.nom = :nom AND e.dateCreation BETWEEN :startDate AND :endDate")
    // List<Evenement> findByNomAndDateCreationBetween(@Param("nom") String nom,
    //                                                @Param("startDate") LocalDateTime startDate,
    //                                                @Param("endDate") LocalDateTime endDate);
    
    // @Query("SELECT e FROM Evenement e WHERE e.lieu = :lieu AND e.dateCreation BETWEEN :startDate AND :endDate")
    // List<Evenement> findByLieuAndDateCreationBetween(@Param("lieu") String lieu,
    //                                                 @Param("startDate") LocalDateTime startDate,
    //                                                 @Param("endDate") LocalDateTime endDate);
    
    // @Query("SELECT e FROM Evenement e WHERE e.description LIKE %:keyword%")
    // List<Evenement> findByDescriptionContaining(@Param("keyword") String keyword);
    
    // @Query("SELECT COUNT(e) FROM Evenement e WHERE e.nom = :nom")
    // Long countByNom(@Param("nom") String nom);
    
    // @Query("SELECT COUNT(e) FROM Evenement e WHERE e.lieu = :lieu")
    // Long countByLieu(@Param("lieu") String lieu);
    
    // @Query("SELECT e FROM Evenement e ORDER BY e.dateCreation DESC")
    // List<Evenement> findAllOrderByDateCreationDesc();
    
    // @Query("SELECT e FROM Evenement e WHERE e.dateCreation >= :date ORDER BY e.dateCreation DESC")
    // List<Evenement> findRecentEvents(@Param("date") LocalDateTime date);
}