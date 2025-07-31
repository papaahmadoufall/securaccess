package com.securaccess.enterprise.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @PostMapping("/login/worker")
    public ResponseEntity<Map<String, Object>> loginWorker(@RequestBody Map<String, String> credentials) {
        try {
            String phone = credentials.get("phone");
            String pin = credentials.get("pin");
            
            // Mock authentication - check against hardcoded credentials (Senegal format)
            if ("771234567".equals(phone) && "1234".equals(pin)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("token", "mock-worker-token-" + System.currentTimeMillis());
                response.put("user", createMockWorker("1", "Jean Dupont", phone));
                response.put("message", "Connexion réussie");
                
                return ResponseEntity.ok(response);
            } else if ("789876543".equals(phone) && "1234".equals(pin)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("token", "mock-worker-token-" + System.currentTimeMillis());
                response.put("user", createMockWorker("2", "Pierre Durand", phone));
                response.put("message", "Connexion réussie");
                
                return ResponseEntity.ok(response);
            } else {
                return createErrorResponse("Numéro de téléphone ou code PIN incorrect");
            }
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la connexion");
        }
    }
    
    @PostMapping("/login/host")
    public ResponseEntity<Map<String, Object>> loginHost(@RequestBody Map<String, String> credentials) {
        try {
            String phone = credentials.get("phone");
            String pin = credentials.get("pin");
            
            // Mock authentication for hosts (Senegal format)
            if ("789876543".equals(phone) && "5678".equals(pin)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("token", "mock-host-token-" + System.currentTimeMillis());
                response.put("user", createMockHost("1", "Marie Martin", phone));
                response.put("message", "Connexion réussie");
                
                return ResponseEntity.ok(response);
            } else if ("771234567".equals(phone) && "5678".equals(pin)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("token", "mock-host-token-" + System.currentTimeMillis());
                response.put("user", createMockHost("2", "Ahmed Fall", phone));
                response.put("message", "Connexion réussie");
                
                return ResponseEntity.ok(response);
            } else {
                return createErrorResponse("Numéro de téléphone ou code PIN incorrect");
            }
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la connexion");
        }
    }
    
    @PostMapping("/login/manager")
    public ResponseEntity<Map<String, Object>> loginManager(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");
            
            // Mock authentication for managers
            if ("manager@securaccess.com".equals(email) && "admin123".equals(password)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("token", "mock-manager-token-" + System.currentTimeMillis());
                response.put("user", createMockManager("1", "Admin Manager", email));
                response.put("message", "Connexion réussie");
                
                return ResponseEntity.ok(response);
            } else {
                return createErrorResponse("Email ou mot de passe incorrect");
            }
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la connexion");
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Déconnexion réussie");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            if (authorization != null && authorization.startsWith("Bearer ")) {
                String token = authorization.substring(7);
                
                // Mock token validation
                if (token.startsWith("mock-")) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("token", token);
                    return ResponseEntity.ok(response);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Token invalide");
            return ResponseEntity.status(401).body(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la validation du token");
        }
    }
    
    // Helper methods
    private Map<String, Object> createMockWorker(String id, String name, String phone) {
        Map<String, Object> worker = new HashMap<>();
        worker.put("id", id);
        worker.put("name", name);
        worker.put("phone", phone);
        worker.put("role", "worker");
        worker.put("department", id.equals("1") ? "IT" : "Marketing");
        worker.put("isActive", true);
        worker.put("createdAt", LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        worker.put("lastAccess", LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return worker;
    }
    
    private Map<String, Object> createMockHost(String id, String name, String phone) {
        Map<String, Object> host = new HashMap<>();
        host.put("id", id);
        host.put("name", name);
        host.put("phone", phone);
        host.put("role", "host");
        host.put("accessLocation", "Salle de Conférence A");
        host.put("accessStartDate", LocalDateTime.now().minusDays(1).toLocalDate().toString());
        host.put("accessEndDate", LocalDateTime.now().plusDays(7).toLocalDate().toString());
        host.put("isActive", true);
        host.put("createdAt", LocalDateTime.now().minusDays(10).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return host;
    }
    
    private Map<String, Object> createMockManager(String id, String name, String email) {
        Map<String, Object> manager = new HashMap<>();
        manager.put("id", id);
        manager.put("name", name);
        manager.put("email", email);
        manager.put("role", "manager");
        manager.put("isActive", true);
        manager.put("createdAt", LocalDateTime.now().minusDays(60).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return manager;
    }
    
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.badRequest().body(error);
    }
}