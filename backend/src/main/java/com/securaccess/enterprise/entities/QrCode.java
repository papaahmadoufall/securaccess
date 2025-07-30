package com.securaccess.enterprise.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_codes")
public class QrCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;
    
    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;
    
    @Column(name = "actif", nullable = false)
    private Boolean actif = true;
    
    @Column(name = "utilise", nullable = false)
    private Boolean utilise = false;
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_utilisation")
    private LocalDateTime dateUtilisation;
    
    // Constructors
    public QrCode() {}
    
    public QrCode(String code, Utilisateur utilisateur, Evenement evenement, LocalDateTime dateExpiration) {
        this.code = code;
        this.utilisateur = utilisateur;
        this.evenement = evenement;
        this.dateExpiration = dateExpiration;
        this.dateCreation = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    
    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }
    
    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDateTime dateExpiration) { this.dateExpiration = dateExpiration; }
    
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
    
    public Boolean getUtilise() { return utilise; }
    public void setUtilise(Boolean utilise) { this.utilise = utilise; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDateTime getDateUtilisation() { return dateUtilisation; }
    public void setDateUtilisation(LocalDateTime dateUtilisation) { this.dateUtilisation = dateUtilisation; }
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
    
    public boolean isExpire() {
        return dateExpiration != null && LocalDateTime.now().isAfter(dateExpiration);
    }
    
    public boolean isValide() {
        return actif && !utilise && !isExpire();
    }
    
    // Additional methods for compatibility
    public boolean estValide() {
        return isValide();
    }
    
    public boolean estExpire() {
        return isExpire();
    }
    
    public boolean isUtilise() {
        return utilise;
    }
    
    public boolean isActif() {
        return actif;
    }
    
    @Lob
    @Column(name = "image_base64")
    private String imageBase64;
    
    public String getImageBase64() {
        return imageBase64;
    }
    
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}