package com.securaccess.enterprise.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hosts")
public class Host {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le nom ne peut contenir que des lettres, espaces, apostrophes et tirets")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^0[1-9][0-9]{8}$", message = "Format de téléphone invalide (ex: 0612345678)")
    @Column(nullable = false, unique = true, length = 10)
    private String phone;
    
    @NotBlank(message = "Le code PIN est obligatoire")
    @Pattern(regexp = "^[0-9]{4}$", message = "Le code PIN doit contenir exactement 4 chiffres")
    @Column(nullable = false, length = 60) // Allows for bcrypt hash
    private String pinHash;
    
    @NotBlank(message = "Le lieu d'accès est obligatoire")
    @Size(max = 100, message = "Le lieu d'accès ne peut pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String accessLocation;
    
    @NotNull(message = "La date de début d'accès est obligatoire")
    @FutureOrPresent(message = "La date de début ne peut pas être dans le passé")
    @Column(name = "access_start_date", nullable = false)
    private LocalDate accessStartDate;
    
    @NotNull(message = "La date de fin d'accès est obligatoire")
    @Future(message = "La date de fin doit être dans le futur")
    @Column(name = "access_end_date", nullable = false)
    private LocalDate accessEndDate;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Size(max = 500, message = "Les instructions ne peuvent pas dépasser 500 caractères")
    @Column(length = 500)
    private String instructions;
    
    @Size(max = 50, message = "Les horaires d'accès ne peuvent pas dépasser 50 caractères")
    @Column(name = "access_hours", length = 50)
    private String accessHours = "08:00 - 18:00";
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_access")
    private LocalDateTime lastAccess;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_manager_id")
    private Manager createdByManager;
    
    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccessLog> accessLogs;
    
    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HostQrCode> qrCodes;
    
    // Constructors
    public Host() {
    }
    
    public Host(String name, String phone, String pinHash, String accessLocation, 
                LocalDate accessStartDate, LocalDate accessEndDate) {
        this.name = sanitizeName(name);
        this.phone = sanitizePhone(phone);
        this.pinHash = pinHash;
        this.accessLocation = sanitizeLocation(accessLocation);
        this.accessStartDate = accessStartDate;
        this.accessEndDate = accessEndDate;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Sanitize inputs
        this.name = sanitizeName(this.name);
        this.phone = sanitizePhone(this.phone);
        this.accessLocation = sanitizeLocation(this.accessLocation);
        this.instructions = sanitizeInstructions(this.instructions);
        this.accessHours = sanitizeAccessHours(this.accessHours);
        // Validate date range
        validateDateRange();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        // Re-sanitize inputs on update
        this.name = sanitizeName(this.name);
        this.accessLocation = sanitizeLocation(this.accessLocation);
        this.instructions = sanitizeInstructions(this.instructions);
        this.accessHours = sanitizeAccessHours(this.accessHours);
        // Validate date range
        validateDateRange();
    }
    
    // Sanitization methods
    private String sanitizeName(String name) {
        if (name == null) return null;
        return name.trim()
            .replaceAll("\\s+", " ")
            .replaceAll("[<>\"'&]", "");
    }
    
    private String sanitizePhone(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("[^0-9]", "");
    }
    
    private String sanitizeLocation(String location) {
        if (location == null) return null;
        return location.trim()
            .replaceAll("[<>\"'&]", "");
    }
    
    private String sanitizeInstructions(String instructions) {
        if (instructions == null) return null;
        return instructions.trim()
            .replaceAll("[<>\"'&]", "");
    }
    
    private String sanitizeAccessHours(String accessHours) {
        if (accessHours == null) return "08:00 - 18:00";
        return accessHours.trim()
            .replaceAll("[<>\"'&]", "");
    }
    
    // Validation methods
    private void validateDateRange() {
        if (accessStartDate != null && accessEndDate != null) {
            if (accessEndDate.isBefore(accessStartDate)) {
                throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début");
            }
        }
    }
    
    // Business methods
    public boolean isValidPin(String rawPin) {
        return pinHash != null && pinHash.equals(hashPin(rawPin));
    }
    
    public boolean isAccessValid() {
        LocalDate today = LocalDate.now();
        return isActive && 
               accessStartDate != null && !today.isBefore(accessStartDate) &&
               accessEndDate != null && !today.isAfter(accessEndDate);
    }
    
    public boolean isAccessExpired() {
        return accessEndDate != null && LocalDate.now().isAfter(accessEndDate);
    }
    
    public boolean isAccessNotStarted() {
        return accessStartDate != null && LocalDate.now().isBefore(accessStartDate);
    }
    
    public void updateLastAccess() {
        this.lastAccess = LocalDateTime.now();
    }
    
    private String hashPin(String rawPin) {
        // In a real implementation, use BCrypt.hashpw()
        return rawPin; // This should be BCrypt.hashpw(rawPin, BCrypt.gensalt())
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = sanitizeName(name);
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = sanitizePhone(phone);
    }
    
    public String getPinHash() {
        return pinHash;
    }
    
    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }
    
    public String getAccessLocation() {
        return accessLocation;
    }
    
    public void setAccessLocation(String accessLocation) {
        this.accessLocation = sanitizeLocation(accessLocation);
    }
    
    public LocalDate getAccessStartDate() {
        return accessStartDate;
    }
    
    public void setAccessStartDate(LocalDate accessStartDate) {
        this.accessStartDate = accessStartDate;
        validateDateRange();
    }
    
    public LocalDate getAccessEndDate() {
        return accessEndDate;
    }
    
    public void setAccessEndDate(LocalDate accessEndDate) {
        this.accessEndDate = accessEndDate;
        validateDateRange();
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = sanitizeInstructions(instructions);
    }
    
    public String getAccessHours() {
        return accessHours;
    }
    
    public void setAccessHours(String accessHours) {
        this.accessHours = sanitizeAccessHours(accessHours);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getLastAccess() {
        return lastAccess;
    }
    
    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }
    
    public Manager getCreatedByManager() {
        return createdByManager;
    }
    
    public void setCreatedByManager(Manager createdByManager) {
        this.createdByManager = createdByManager;
    }
    
    public List<AccessLog> getAccessLogs() {
        return accessLogs;
    }
    
    public void setAccessLogs(List<AccessLog> accessLogs) {
        this.accessLogs = accessLogs;
    }
    
    public List<HostQrCode> getQrCodes() {
        return qrCodes;
    }
    
    public void setQrCodes(List<HostQrCode> qrCodes) {
        this.qrCodes = qrCodes;
    }
    
    @Override
    public String toString() {
        return "Host{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", accessLocation='" + accessLocation + '\'' +
                ", accessStartDate=" + accessStartDate +
                ", accessEndDate=" + accessEndDate +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}