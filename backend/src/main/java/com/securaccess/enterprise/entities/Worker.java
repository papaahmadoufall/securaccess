package com.securaccess.enterprise.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workers")
public class Worker {
    
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
    
    @NotBlank(message = "Le département est obligatoire")
    @Size(max = 50, message = "Le département ne peut pas dépasser 50 caractères")
    @Column(nullable = false, length = 50)
    private String department;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_access")
    private LocalDateTime lastAccess;
    
    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccessLog> accessLogs;
    
    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkerQrCode> qrCodes;
    
    // Constructors
    public Worker() {
    }
    
    public Worker(String name, String phone, String pinHash, String department) {
        this.name = sanitizeName(name);
        this.phone = sanitizePhone(phone);
        this.pinHash = pinHash; // Should be pre-hashed
        this.department = sanitizeDepartment(department);
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
        this.department = sanitizeDepartment(this.department);
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        // Re-sanitize inputs on update
        this.name = sanitizeName(this.name);
        this.department = sanitizeDepartment(this.department);
    }
    
    // Sanitization methods
    private String sanitizeName(String name) {
        if (name == null) return null;
        return name.trim()
            .replaceAll("\\s+", " ") // Replace multiple spaces with single space
            .replaceAll("[<>\"'&]", ""); // Remove potentially dangerous characters
    }
    
    private String sanitizePhone(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("[^0-9]", ""); // Keep only digits
    }
    
    private String sanitizeDepartment(String department) {
        if (department == null) return null;
        return department.trim()
            .replaceAll("[<>\"'&]", ""); // Remove potentially dangerous characters
    }
    
    // Business methods
    public boolean isValidPin(String rawPin) {
        // This should use BCrypt or similar for comparison
        // For demo purposes, we'll assume pinHash is already hashed
        return pinHash != null && pinHash.equals(hashPin(rawPin));
    }
    
    public void updateLastAccess() {
        this.lastAccess = LocalDateTime.now();
    }
    
    private String hashPin(String rawPin) {
        // In a real implementation, use BCrypt.hashpw()
        // For demo, we'll use a simple approach
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
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = sanitizeDepartment(department);
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    
    public List<AccessLog> getAccessLogs() {
        return accessLogs;
    }
    
    public void setAccessLogs(List<AccessLog> accessLogs) {
        this.accessLogs = accessLogs;
    }
    
    public List<WorkerQrCode> getQrCodes() {
        return qrCodes;
    }
    
    public void setQrCodes(List<WorkerQrCode> qrCodes) {
        this.qrCodes = qrCodes;
    }
    
    @Override
    public String toString() {
        return "Worker{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", department='" + department + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}