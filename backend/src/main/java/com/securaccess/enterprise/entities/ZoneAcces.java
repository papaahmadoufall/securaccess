package com.securaccess.enterprise.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "zones_acces")
public class ZoneAcces {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nom;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "actif", nullable = false)
    private Boolean actif = true;
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    // Constructors
    public ZoneAcces() {}
    
    public ZoneAcces(String nom, String description) {
        this.nom = nom;
        this.description = description;
        this.dateCreation = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}