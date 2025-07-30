package com.securaccess.enterprise.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestResource {
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "SecurAccess Enterprise Backend is working!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("database", "Connected");
        health.put("service", "SecurAccess Enterprise");
        
        return ResponseEntity.ok(health);
    }
}