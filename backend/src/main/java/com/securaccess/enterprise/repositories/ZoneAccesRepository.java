package com.securaccess.enterprise.repositories;

import com.securaccess.enterprise.entities.ZoneAcces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneAccesRepository extends JpaRepository<ZoneAcces, Long> {
    
    Optional<ZoneAcces> findByNom(String nom);
    
    List<ZoneAcces> findByActif(Boolean actif);
    
    // @Query("SELECT z FROM ZoneAcces z WHERE z.nom LIKE %:nom%")
    // List<ZoneAcces> findByNomContaining(@Param("nom") String nom);
    
    // @Query("SELECT z FROM ZoneAcces z WHERE z.niveauSecurite >= :minLevel")
    // List<ZoneAcces> findByMinimumSecurityLevel(@Param("minLevel") Integer minLevel);
    
    // @Query("SELECT z FROM ZoneAcces z WHERE z.niveauSecurite <= :maxLevel")
    // List<ZoneAcces> findByMaximumSecurityLevel(@Param("maxLevel") Integer maxLevel);
    
    // @Query("SELECT z FROM ZoneAcces z WHERE z.niveauSecurite BETWEEN :minLevel AND :maxLevel")
    // List<ZoneAcces> findBySecurityLevelBetween(@Param("minLevel") Integer minLevel, 
    //                                          @Param("maxLevel") Integer maxLevel);
    
    // @Query("SELECT z FROM ZoneAcces z WHERE z.actif = true ORDER BY z.niveauSecurite DESC")
    // List<ZoneAcces> findActiveZonesOrderBySecurityLevelDesc();
    
    // @Query("SELECT COUNT(z) FROM ZoneAcces z WHERE z.niveauSecurite = :level AND z.actif = true")
    // Long countActiveZonesBySecurityLevel(@Param("level") Integer level);
    
    // @Query("SELECT DISTINCT z.niveauSecurite FROM ZoneAcces z WHERE z.actif = true ORDER BY z.niveauSecurite")
    // List<Integer> findAllActiveSecurityLevels();
}