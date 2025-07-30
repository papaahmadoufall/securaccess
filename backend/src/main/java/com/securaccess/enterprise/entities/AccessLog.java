package com.securaccess.enterprise.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_logs")
public class AccessLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private Worker worker;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host host;
    
    @Column(nullable = false, length = 20)
    private String accessType; // "entry" or "exit"
    
    @Column(nullable = false, length = 100)
    private String location;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    private Boolean success;
    
    @Column(length = 50)
    private String qrCode;
    
    @Column(length = 500)
    private String details;
    
    // Constructors
    public AccessLog() {
    }
    
    public AccessLog(Worker worker, String accessType, String location, Boolean success, String qrCode) {
        this.worker = worker;
        this.accessType = accessType;
        this.location = location;
        this.success = success;
        this.qrCode = qrCode;
        this.timestamp = LocalDateTime.now();
    }
    
    public AccessLog(Host host, String accessType, String location, Boolean success, String qrCode) {
        this.host = host;
        this.accessType = accessType;
        this.location = location;
        this.success = success;
        this.qrCode = qrCode;
        this.timestamp = LocalDateTime.now();
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
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
    
    public Host getHost() {
        return host;
    }
    
    public void setHost(Host host) {
        this.host = host;
    }
    
    public String getAccessType() {
        return accessType;
    }
    
    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    public String getQrCode() {
        return qrCode;
    }
    
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
}