package com.securaccess.enterprise.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/managers")
public class ManagerController {

    private final Map<String, Map<String, Object>> managersDatabase = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(3);

    public ManagerController() {
        initializeMockData();
    }

    private void initializeMockData() {
        // Add some mock managers for demonstration
        Map<String, Object> manager1 = new HashMap<>();
        manager1.put("id", "1");
        manager1.put("name", "Admin Manager");
        manager1.put("email", "manager@securaccess.com");
        manager1.put("passwordHash", "admin123"); // In real app, hash this
        manager1.put("department", "Administration");
        manager1.put("isActive", true);
        manager1.put("createdAt", LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        manager1.put("lastLogin", LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Map<String, Object> manager2 = new HashMap<>();
        manager2.put("id", "2");
        manager2.put("name", "Marie Dupont");
        manager2.put("email", "marie.dupont@company.com");
        manager2.put("passwordHash", "manager123"); // In real app, hash this
        manager2.put("department", "Ressources Humaines");
        manager2.put("isActive", true);
        manager2.put("createdAt", LocalDateTime.now().minusDays(15).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        manager2.put("lastLogin", LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        managersDatabase.put("1", manager1);
        managersDatabase.put("2", manager2);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllManagers() {
        try {
            List<Map<String, Object>> managers = new ArrayList<>(managersDatabase.values());
            
            // Remove password hash from response
            managers.forEach(manager -> manager.remove("passwordHash"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("managers", managers);
            response.put("total", managers.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la récupération des managers");
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createManager(@RequestBody Map<String, Object> managerData) {
        try {
            // Validate required fields
            if (!managerData.containsKey("name") || !managerData.containsKey("email") || 
                !managerData.containsKey("password") || !managerData.containsKey("department")) {
                return createErrorResponse("Les champs nom, email, mot de passe et département sont requis");
            }
            
            // Check if email already exists
            for (Map<String, Object> manager : managersDatabase.values()) {
                if (managerData.get("email").equals(manager.get("email"))) {
                    return createErrorResponse("Cet email est déjà utilisé");
                }
            }
            
            String newId = String.valueOf(nextId.getAndIncrement());
            Map<String, Object> newManager = new HashMap<>();
            newManager.put("id", newId);
            newManager.put("name", managerData.get("name"));
            newManager.put("email", managerData.get("email"));
            newManager.put("passwordHash", managerData.get("password")); // In real app, hash this
            newManager.put("department", managerData.get("department"));
            newManager.put("isActive", true);
            newManager.put("createdAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            newManager.put("lastLogin", null);
            
            managersDatabase.put(newId, newManager);
            
            // Remove password hash from response
            Map<String, Object> responseManager = new HashMap<>(newManager);
            responseManager.remove("passwordHash");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("manager", responseManager);
            response.put("message", "Manager créé avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la création du manager");
        }
    }

    @GetMapping("/{managerId}")
    public ResponseEntity<Map<String, Object>> getManager(@PathVariable String managerId) {
        try {
            Map<String, Object> manager = managersDatabase.get(managerId);
            if (manager == null) {
                return createErrorResponse("Manager non trouvé");
            }
            
            // Remove password hash from response
            Map<String, Object> responseManager = new HashMap<>(manager);
            responseManager.remove("passwordHash");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("manager", responseManager);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la récupération du manager");
        }
    }

    @PutMapping("/{managerId}")
    public ResponseEntity<Map<String, Object>> updateManager(@PathVariable String managerId, @RequestBody Map<String, Object> managerData) {
        try {
            Map<String, Object> existingManager = managersDatabase.get(managerId);
            if (existingManager == null) {
                return createErrorResponse("Manager non trouvé");
            }
            
            // Check if email is being changed and if it already exists
            if (managerData.containsKey("email") && !managerData.get("email").equals(existingManager.get("email"))) {
                for (Map<String, Object> manager : managersDatabase.values()) {
                    if (!manager.get("id").equals(managerId) && managerData.get("email").equals(manager.get("email"))) {
                        return createErrorResponse("Cet email est déjà utilisé");
                    }
                }
            }
            
            // Update fields
            if (managerData.containsKey("name")) existingManager.put("name", managerData.get("name"));
            if (managerData.containsKey("email")) existingManager.put("email", managerData.get("email"));
            if (managerData.containsKey("password")) existingManager.put("passwordHash", managerData.get("password"));
            if (managerData.containsKey("department")) existingManager.put("department", managerData.get("department"));
            if (managerData.containsKey("isActive")) existingManager.put("isActive", managerData.get("isActive"));
            
            // Remove password hash from response
            Map<String, Object> responseManager = new HashMap<>(existingManager);
            responseManager.remove("passwordHash");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("manager", responseManager);
            response.put("message", "Manager mis à jour avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la mise à jour du manager");
        }
    }

    @DeleteMapping("/{managerId}")
    public ResponseEntity<Map<String, Object>> deleteManager(@PathVariable String managerId) {
        try {
            Map<String, Object> manager = managersDatabase.get(managerId);
            if (manager == null) {
                return createErrorResponse("Manager non trouvé");
            }
            
            managersDatabase.remove(managerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Manager supprimé avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la suppression du manager");
        }
    }

    @GetMapping("/{managerId}/profile")
    public ResponseEntity<Map<String, Object>> getManagerProfile(@PathVariable String managerId) {
        try {
            Map<String, Object> manager = managersDatabase.get(managerId);
            if (manager == null) {
                return createErrorResponse("Manager non trouvé");
            }
            
            // Remove password hash from response
            Map<String, Object> profile = new HashMap<>(manager);
            profile.remove("passwordHash");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("profile", profile);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la récupération du profil");
        }
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return ResponseEntity.badRequest().body(response);
    }
}