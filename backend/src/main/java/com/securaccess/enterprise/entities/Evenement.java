package com.securaccess.enterprise.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "evenements")
public class Evenement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;
    
    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;
    
    @Column(name = "lieu")
    private String lieu;
    
    @Column(name = "actif", nullable = false)
    private Boolean actif = true;
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id", nullable = false)
    private Utilisateur createur;
    
    @ManyToMany
    @JoinTable(
        name = "evenement_zones",
        joinColumns = @JoinColumn(name = "evenement_id"),
        inverseJoinColumns = @JoinColumn(name = "zone_id")
    )
    private Set<ZoneAcces> zonesAcces = new HashSet<>();
    
    // Constructors
    public Evenement() {}
    
    public Evenement(String nom, String description, LocalDateTime dateDebut, LocalDateTime dateFin, Utilisateur createur) {
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.createur = createur;
        this.dateCreation = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public Utilisateur getCreateur() { return createur; }
    public void setCreateur(Utilisateur createur) { this.createur = createur; }
    
    public Set<ZoneAcces> getZonesAcces() { return zonesAcces; }
    public void setZonesAcces(Set<ZoneAcces> zonesAcces) { this.zonesAcces = zonesAcces; }
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}