package com.securaccess.enterprise.repositories;

import com.securaccess.enterprise.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    Optional<Utilisateur> findByEmail(String email);
    
    List<Utilisateur> findByTypeUtilisateur(Utilisateur.TypeUtilisateur typeUtilisateur);
    
    List<Utilisateur> findByActif(Boolean actif);
    
    // @Query("SELECT u FROM Utilisateur u WHERE u.email = :email AND u.actif = true")
    // Optional<Utilisateur> findActiveByEmail(@Param("email") String email);
    
    // @Query("SELECT u FROM Utilisateur u WHERE u.typeUtilisateur = :type AND u.actif = true")
    // List<Utilisateur> findActiveByType(@Param("type") Utilisateur.TypeUtilisateur type);
    
    // @Query("SELECT u FROM Utilisateur u WHERE u.nom LIKE %:nom% OR u.prenom LIKE %:prenom%")
    // List<Utilisateur> findByNomOrPrenomContaining(@Param("nom") String nom, @Param("prenom") String prenom);
    
    // @Query("SELECT u FROM Utilisateur u WHERE u.dateCreation BETWEEN :startDate AND :endDate")
    // List<Utilisateur> findUsersCreatedBetween(@Param("startDate") LocalDateTime startDate,
    //                                         @Param("endDate") LocalDateTime endDate);
    
    // @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.typeUtilisateur = :type AND u.actif = true")
    // Long countActiveUsersByType(@Param("type") Utilisateur.TypeUtilisateur type);
    
    // @Query("SELECT DISTINCT u.typeUtilisateur FROM Utilisateur u WHERE u.actif = true")
    // List<Utilisateur.TypeUtilisateur> findAllActiveTypes();
    
    // @Query("SELECT u FROM Utilisateur u WHERE u.nom LIKE %:keyword% OR u.prenom LIKE %:keyword% OR u.email LIKE %:keyword%")
    // List<Utilisateur> findByKeywordContaining(@Param("keyword") String keyword);
}