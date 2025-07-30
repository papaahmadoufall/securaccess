package com.securaccess.enterprise.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "worker_qr_codes")
public class WorkerQrCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;
    
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private Boolean isValid = true;
    
    @Column(nullable = false)
    private Boolean isUsed = false;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    @Lob
    @Column(name = "image_base64")
    private String imageBase64;
    
    // Constructors
    public WorkerQrCode() {
    }
    
    public WorkerQrCode(Worker worker, String code, LocalDateTime expiresAt) {
        this.worker = worker;
        this.code = code;
        this.generatedAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.isValid = true;
        this.isUsed = false;
    }
    
    // Business methods
    public boolean isActive() {
        return isValid && !isUsed && !isExpired();
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }
    
    public void invalidate() {
        this.isValid = false;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if (this.generatedAt == null) {
            this.generatedAt = LocalDateTime.now();
        }
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Worker getWorker() {
        return worker;
    }
    
    public void setWorker(Worker worker) {
        this.worker = worker;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public Boolean getIsValid() {
        return isValid;
    }
    
    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }
    
    public Boolean getIsUsed() {
        return isUsed;
    }
    
    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }
    
    public LocalDateTime getUsedAt() {
        return usedAt;
    }
    
    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
    
    public String getImageBase64() {
        return imageBase64;
    }
    
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}