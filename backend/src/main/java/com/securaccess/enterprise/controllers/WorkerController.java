package com.securaccess.enterprise.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/workers")
@CrossOrigin(origins = "*")
public class WorkerController {
    
    // Mock data storage
    private static Map<String, Map<String, Object>> workersDatabase = new HashMap<>();
    private static Long nextId = 3L;
    
    static {
        // Initialize with mock data
        Map<String, Object> worker1 = new HashMap<>();
        worker1.put("id", "1");
        worker1.put("name", "Jean Dupont");
        worker1.put("phone", "771234567");
        worker1.put("pinHash", "1234"); // In real app, this would be hashed
        worker1.put("department", "IT");
        worker1.put("isActive", true);
        worker1.put("createdAt", "2024-01-15T10:30:00");
        worker1.put("lastAccess", LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        workersDatabase.put("1", worker1);
        
        Map<String, Object> worker2 = new HashMap<>();
        worker2.put("id", "2");
        worker2.put("name", "Pierre Durand");
        worker2.put("phone", "789876543");
        worker2.put("pinHash", "1234");
        worker2.put("department", "Marketing");
        worker2.put("isActive", true);
        worker2.put("createdAt", "2024-01-20T14:15:00");
        worker2.put("lastAccess", LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        workersDatabase.put("2", worker2);
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllWorkers() {
        try {
            List<Map<String, Object>> workers = new ArrayList<>(workersDatabase.values());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("workers", workers);
            response.put("total", workers.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la récupération des employés");
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createWorker(@RequestBody Map<String, Object> workerData) {
        try {
            // Validate required fields
            if (!workerData.containsKey("name") || !workerData.containsKey("phone") || 
                !workerData.containsKey("pin") || !workerData.containsKey("department")) {
                return createErrorResponse("Les champs nom, téléphone, code PIN et département sont requis");
            }
            
            // Check if phone already exists
            for (Map<String, Object> worker : workersDatabase.values()) {
                if (workerData.get("phone").equals(worker.get("phone"))) {
                    return createErrorResponse("Ce numéro de téléphone est déjà utilisé");
                }
            }
            
            String newId = String.valueOf(nextId++);
            Map<String, Object> newWorker = new HashMap<>();
            newWorker.put("id", newId);
            newWorker.put("name", workerData.get("name"));
            newWorker.put("phone", workerData.get("phone"));
            newWorker.put("pinHash", workerData.get("pin")); // In real app, hash this
            newWorker.put("department", workerData.get("department"));
            newWorker.put("isActive", true);
            newWorker.put("createdAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            newWorker.put("lastAccess", null);
            
            workersDatabase.put(newId, newWorker);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("worker", newWorker);
            response.put("message", "Employé créé avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la création de l'employé");
        }
    }
    
    @PutMapping("/{workerId}")
    public ResponseEntity<Map<String, Object>> updateWorker(@PathVariable String workerId, @RequestBody Map<String, Object> workerData) {
        try {
            if (!workersDatabase.containsKey(workerId)) {
                return createErrorResponse("Employé non trouvé");
            }
            
            Map<String, Object> worker = workersDatabase.get(workerId);
            
            // Update fields if provided
            if (workerData.containsKey("name")) {
                worker.put("name", workerData.get("name"));
            }
            if (workerData.containsKey("phone")) {
                // Check if new phone is already used by another worker
                for (Map.Entry<String, Map<String, Object>> entry : workersDatabase.entrySet()) {
                    if (!entry.getKey().equals(workerId) && 
                        workerData.get("phone").equals(entry.getValue().get("phone"))) {
                        return createErrorResponse("Ce numéro de téléphone est déjà utilisé");
                    }
                }
                worker.put("phone", workerData.get("phone"));
            }
            if (workerData.containsKey("pin")) {
                worker.put("pinHash", workerData.get("pin")); // In real app, hash this
            }
            if (workerData.containsKey("department")) {
                worker.put("department", workerData.get("department"));
            }
            
            worker.put("updatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("worker", worker);
            response.put("message", "Employé mis à jour avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la mise à jour de l'employé");
        }
    }
    
    @DeleteMapping("/{workerId}")
    public ResponseEntity<Map<String, Object>> deleteWorker(@PathVariable String workerId) {
        try {
            if (!workersDatabase.containsKey(workerId)) {
                return createErrorResponse("Employé non trouvé");
            }
            
            workersDatabase.remove(workerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Employé supprimé avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la suppression de l'employé");
        }
    }
    
    @PutMapping("/{workerId}/status")
    public ResponseEntity<Map<String, Object>> toggleWorkerStatus(@PathVariable String workerId, @RequestBody Map<String, Object> statusData) {
        try {
            if (!workersDatabase.containsKey(workerId)) {
                return createErrorResponse("Employé non trouvé");
            }
            
            Map<String, Object> worker = workersDatabase.get(workerId);
            Boolean isActive = (Boolean) statusData.get("isActive");
            worker.put("isActive", isActive);
            worker.put("updatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("worker", worker);
            response.put("message", isActive ? "Employé activé" : "Employé désactivé");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la mise à jour du statut");
        }
    }
    
    @GetMapping("/{workerId}/qr-code/generate")
    public ResponseEntity<Map<String, Object>> generateQRCode(@PathVariable String workerId) {
        try {
            // Mock QR code generation
            Map<String, Object> qrCode = new HashMap<>();
            qrCode.put("id", UUID.randomUUID().toString());
            qrCode.put("code", "WKR-" + System.currentTimeMillis());
            qrCode.put("workerId", workerId);
            qrCode.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            qrCode.put("expiresAt", LocalDateTime.now().plusHours(8).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
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
    
    @GetMapping("/{workerId}/access-history")
    public ResponseEntity<Map<String, Object>> getAccessHistory(@PathVariable String workerId,
                                   @RequestParam(value = "limit", defaultValue = "50") int limit,
                                   @RequestParam(value = "type", required = false) String type) {
        try {
            List<Map<String, Object>> history = getMockAccessHistory(workerId, limit, type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("history", history);
            response.put("total", history.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la récupération de l'historique");
        }
    }
    
    @PostMapping("/{workerId}/access-log")
    public ResponseEntity<Map<String, Object>> logAccess(@PathVariable String workerId, @RequestBody Map<String, Object> accessData) {
        try {
            // Mock access logging
            Map<String, Object> accessLog = new HashMap<>();
            accessLog.put("id", UUID.randomUUID().toString());
            accessLog.put("workerId", workerId);
            accessLog.put("type", accessData.get("type")); // entry or exit
            accessLog.put("location", accessData.get("location"));
            accessLog.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            accessLog.put("qrCode", accessData.get("qrCode"));
            accessLog.put("success", true);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accessLog", accessLog);
            response.put("message", "Accès enregistré avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de l'enregistrement de l'accès");
        }
    }
    
    @GetMapping("/{workerId}/profile")
    public ResponseEntity<Map<String, Object>> getWorkerProfile(@PathVariable String workerId) {
        try {
            Map<String, Object> worker = getMockWorkerProfile(workerId);
            
            if (worker != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("worker", worker);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la récupération du profil");
        }
    }
    
    @PutMapping("/{workerId}/last-access")
    public ResponseEntity<Map<String, Object>> updateLastAccess(@PathVariable String workerId) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Dernière connexion mise à jour");
            response.put("lastAccess", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la mise à jour");
        }
    }
    
    // Helper methods
    private List<Map<String, Object>> getMockAccessHistory(String workerId, int limit, String type) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        // Generate mock access history
        for (int i = 0; i < Math.min(limit, 10); i++) {
            Map<String, Object> access = new HashMap<>();
            access.put("id", UUID.randomUUID().toString());
            access.put("type", i % 2 == 0 ? "entry" : "exit");
            access.put("location", i % 3 == 0 ? "Entrée Principale" : (i % 3 == 1 ? "Salle de Réunion A" : "Cafétéria"));
            access.put("timestamp", LocalDateTime.now().minusHours(i * 2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            access.put("success", true);
            access.put("qrCode", "WKR-" + (System.currentTimeMillis() - i * 1000));
            
            // Filter by type if specified
            if (type == null || type.equals(access.get("type"))) {
                history.add(access);
            }
        }
        
        return history;
    }
    
    private Map<String, Object> getMockWorkerProfile(String workerId) {
        // Mock worker profiles
        Map<String, Map<String, Object>> workers = new HashMap<>();
        
        Map<String, Object> worker1 = new HashMap<>();
        worker1.put("id", "1");
        worker1.put("name", "Jean Dupont");
        worker1.put("phone", "0612345678");
        worker1.put("department", "IT");
        worker1.put("isActive", true);
        worker1.put("createdAt", "2024-01-15T10:30:00");
        worker1.put("lastAccess", LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        workers.put("1", worker1);
        
        Map<String, Object> worker2 = new HashMap<>();
        worker2.put("id", "2");
        worker2.put("name", "Pierre Durand");
        worker2.put("phone", "0698765432");
        worker2.put("department", "Marketing");
        worker2.put("isActive", true);
        worker2.put("createdAt", "2024-01-20T14:15:00");
        worker2.put("lastAccess", LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        workers.put("2", worker2);
        
        return workers.get(workerId);
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