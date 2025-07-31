package com.securaccess.enterprise.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/hosts")
@CrossOrigin(origins = "*")
public class HostController {
    
    // Mock data storage
    private static Map<String, Map<String, Object>> hostsDatabase = new HashMap<>();
    private static Long nextId = 2L;
    
    static {
        // Initialize with mock data
        Map<String, Object> host1 = new HashMap<>();
        host1.put("id", "1");
        host1.put("name", "Marie Martin");
        host1.put("phone", "789876543");
        host1.put("pinHash", "5678"); // In real app, this would be hashed
        host1.put("accessLocation", "Salle de Conférence A");
        host1.put("accessStartDate", LocalDate.now().minusDays(1).toString());
        host1.put("accessEndDate", LocalDate.now().plusDays(7).toString());
        host1.put("accessHours", "09:00 - 17:00");
        host1.put("instructions", "Accès autorisé pour la présentation client");
        host1.put("isActive", true);
        host1.put("createdAt", LocalDateTime.now().minusDays(10).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        host1.put("lastAccess", LocalDateTime.now().minusHours(3).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        hostsDatabase.put("1", host1);
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllHosts() {
        try {
            List<Map<String, Object>> hosts = new ArrayList<>(hostsDatabase.values());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hosts", hosts);
            response.put("total", hosts.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la récupération des hôtes");
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createHost(@RequestBody Map<String, Object> hostData) {
        try {
            // Validate required fields
            if (!hostData.containsKey("name") || !hostData.containsKey("phone") || 
                !hostData.containsKey("pin") || !hostData.containsKey("accessLocation") ||
                !hostData.containsKey("accessStartDate") || !hostData.containsKey("accessEndDate")) {
                return createErrorResponse("Les champs nom, téléphone, code PIN, lieu d'accès et dates sont requis");
            }
            
            // Check if phone already exists
            for (Map<String, Object> host : hostsDatabase.values()) {
                if (hostData.get("phone").equals(host.get("phone"))) {
                    return createErrorResponse("Ce numéro de téléphone est déjà utilisé");
                }
            }
            
            String newId = String.valueOf(nextId++);
            Map<String, Object> newHost = new HashMap<>();
            newHost.put("id", newId);
            newHost.put("name", hostData.get("name"));
            newHost.put("phone", hostData.get("phone"));
            newHost.put("pinHash", hostData.get("pin")); // In real app, hash this
            newHost.put("accessLocation", hostData.get("accessLocation"));
            newHost.put("accessStartDate", hostData.get("accessStartDate"));
            newHost.put("accessEndDate", hostData.get("accessEndDate"));
            newHost.put("accessHours", hostData.getOrDefault("accessHours", "08:00 - 18:00"));
            newHost.put("instructions", hostData.getOrDefault("instructions", ""));
            newHost.put("isActive", true);
            newHost.put("createdAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            newHost.put("lastAccess", null);
            
            hostsDatabase.put(newId, newHost);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("host", newHost);
            response.put("message", "Hôte créé avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la création de l'hôte");
        }
    }
    
    @PutMapping("/{hostId}")
    public ResponseEntity<Map<String, Object>> updateHost(@PathVariable String hostId, @RequestBody Map<String, Object> hostData) {
        try {
            if (!hostsDatabase.containsKey(hostId)) {
                return createErrorResponse("Hôte non trouvé");
            }
            
            Map<String, Object> host = hostsDatabase.get(hostId);
            
            // Update fields if provided
            if (hostData.containsKey("name")) {
                host.put("name", hostData.get("name"));
            }
            if (hostData.containsKey("phone")) {
                // Check if new phone is already used by another host
                for (Map.Entry<String, Map<String, Object>> entry : hostsDatabase.entrySet()) {
                    if (!entry.getKey().equals(hostId) && 
                        hostData.get("phone").equals(entry.getValue().get("phone"))) {
                        return createErrorResponse("Ce numéro de téléphone est déjà utilisé");
                    }
                }
                host.put("phone", hostData.get("phone"));
            }
            if (hostData.containsKey("pin")) {
                host.put("pinHash", hostData.get("pin")); // In real app, hash this
            }
            if (hostData.containsKey("accessLocation")) {
                host.put("accessLocation", hostData.get("accessLocation"));
            }
            if (hostData.containsKey("accessStartDate")) {
                host.put("accessStartDate", hostData.get("accessStartDate"));
            }
            if (hostData.containsKey("accessEndDate")) {
                host.put("accessEndDate", hostData.get("accessEndDate"));
            }
            if (hostData.containsKey("accessHours")) {
                host.put("accessHours", hostData.get("accessHours"));
            }
            if (hostData.containsKey("instructions")) {
                host.put("instructions", hostData.get("instructions"));
            }
            
            host.put("updatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("host", host);
            response.put("message", "Hôte mis à jour avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la mise à jour de l'hôte");
        }
    }
    
    @DeleteMapping("/{hostId}")
    public ResponseEntity<Map<String, Object>> deleteHost(@PathVariable String hostId) {
        try {
            if (!hostsDatabase.containsKey(hostId)) {
                return createErrorResponse("Hôte non trouvé");
            }
            
            hostsDatabase.remove(hostId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hôte supprimé avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la suppression de l'hôte");
        }
    }
    
    @PutMapping("/{hostId}/status")
    public ResponseEntity<Map<String, Object>> toggleHostStatus(@PathVariable String hostId, @RequestBody Map<String, Object> statusData) {
        try {
            if (!hostsDatabase.containsKey(hostId)) {
                return createErrorResponse("Hôte non trouvé");
            }
            
            Map<String, Object> host = hostsDatabase.get(hostId);
            Boolean isActive = (Boolean) statusData.get("isActive");
            host.put("isActive", isActive);
            host.put("updatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("host", host);
            response.put("message", isActive ? "Hôte activé" : "Hôte désactivé");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la mise à jour du statut");
        }
    }
    
    @GetMapping("/{hostId}/qr-code/generate")
    public ResponseEntity<Map<String, Object>> generateQRCode(@PathVariable String hostId) {
        try {
            // Mock QR code generation for host
            Map<String, Object> qrCode = new HashMap<>();
            qrCode.put("id", UUID.randomUUID().toString());
            qrCode.put("code", "HST-" + System.currentTimeMillis());
            qrCode.put("hostId", hostId);
            qrCode.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            qrCode.put("expiresAt", LocalDateTime.now().plusHours(24).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            qrCode.put("isValid", true);
            qrCode.put("imageBase64", generateMockQRImage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrCode", qrCode);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la génération du QR code");
        }
    }
    
    @GetMapping("/{hostId}/access-history")
    public ResponseEntity<Map<String, Object>> getAccessHistory(@PathVariable String hostId,
                                   @RequestParam(value = "limit", defaultValue = "50") int limit,
                                   @RequestParam(value = "type", required = false) String type) {
        try {
            List<Map<String, Object>> history = getMockAccessHistory(hostId, limit, type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("history", history);
            response.put("total", history.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la récupération de l'historique");
        }
    }
    
    @GetMapping("/{hostId}/profile")
    public ResponseEntity<Map<String, Object>> getHostProfile(@PathVariable String hostId) {
        try {
            Map<String, Object> host = hostsDatabase.get(hostId);
            
            if (host != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("host", host);
                
                return ResponseEntity.ok(response);
            } else {
                return createErrorResponse("Hôte non trouvé");
            }
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la récupération du profil");
        }
    }
    
    // Helper methods
    private List<Map<String, Object>> getMockAccessHistory(String hostId, int limit, String type) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        // Generate mock access history for host
        for (int i = 0; i < Math.min(limit, 8); i++) {
            Map<String, Object> access = new HashMap<>();
            access.put("id", UUID.randomUUID().toString());
            access.put("type", i % 2 == 0 ? "entry" : "exit");
            access.put("location", "Salle de Conférence A");
            access.put("timestamp", LocalDateTime.now().minusHours(i * 3).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            access.put("success", true);
            access.put("qrCode", "HST-" + (System.currentTimeMillis() - i * 1000));
            
            // Filter by type if specified
            if (type == null || type.equals(access.get("type"))) {
                history.add(access);
            }
        }
        
        return history;
    }
    
    private String generateMockQRImage() {
        // Mock base64 QR code image (small black square)
        return "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";
    }
    
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.internalServerError().body(error);
    }
}