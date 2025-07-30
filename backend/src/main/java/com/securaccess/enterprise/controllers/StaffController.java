package com.securaccess.enterprise.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/staff")
@CrossOrigin(origins = "*")
public class StaffController {

    // Mock staff database
    private static final Map<String, Map<String, Object>> STAFF_DATABASE = new HashMap<>();
    
    // Mock access logs
    private static final List<Map<String, Object>> ACCESS_LOGS = new ArrayList<>();
    
    static {
        // Initialize mock staff
        Map<String, Object> staff1 = new HashMap<>();
        staff1.put("staffId", "STAFF001");
        staff1.put("pin", "1234");
        staff1.put("name", "Security Officer Alpha");
        staff1.put("role", "security_officer");
        staff1.put("department", "Security");
        staff1.put("isActive", true);
        staff1.put("permissions", Arrays.asList("access_control", "override_access", "view_logs"));
        staff1.put("createdAt", LocalDateTime.now().minusMonths(6));
        STAFF_DATABASE.put("STAFF001", staff1);
        
        Map<String, Object> staff2 = new HashMap<>();
        staff2.put("staffId", "STAFF002");
        staff2.put("pin", "5678");
        staff2.put("name", "Security Officer Beta");
        staff2.put("role", "security_officer");
        staff2.put("department", "Security");
        staff2.put("isActive", true);
        staff2.put("permissions", Arrays.asList("access_control", "view_logs"));
        staff2.put("createdAt", LocalDateTime.now().minusMonths(3));
        STAFF_DATABASE.put("STAFF002", staff2);
        
        Map<String, Object> staff3 = new HashMap<>();
        staff3.put("staffId", "ADMIN001");
        staff3.put("pin", "9999");
        staff3.put("name", "Security Administrator");
        staff3.put("role", "security_admin");
        staff3.put("department", "Security");
        staff3.put("isActive", true);
        staff3.put("permissions", Arrays.asList("access_control", "override_access", "view_logs", "manage_staff", "generate_reports"));
        staff3.put("createdAt", LocalDateTime.now().minusYears(1));
        STAFF_DATABASE.put("ADMIN001", staff3);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> staffLogin(@RequestBody Map<String, String> credentials) {
        String staffId = credentials.get("staffId");
        String pin = credentials.get("pin");
        
        Map<String, Object> response = new HashMap<>();
        
        if (staffId == null || pin == null) {
            response.put("success", false);
            response.put("message", "Staff ID and PIN are required");
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> staff = STAFF_DATABASE.get(staffId);
        
        if (staff != null && pin.equals(staff.get("pin")) && (Boolean) staff.get("isActive")) {
            // Generate mock token
            String token = "staff-token-" + System.currentTimeMillis();
            
            // Create staff session info
            Map<String, Object> staffInfo = new HashMap<>();
            staffInfo.put("staffId", staff.get("staffId"));
            staffInfo.put("name", staff.get("name"));
            staffInfo.put("role", staff.get("role"));
            staffInfo.put("department", staff.get("department"));
            staffInfo.put("permissions", staff.get("permissions"));
            staffInfo.put("loginTime", LocalDateTime.now());
            
            response.put("success", true);
            response.put("message", "Authentication successful");
            response.put("staff", staffInfo);
            response.put("token", token);
            
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Invalid staff credentials or account inactive");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> staffLogout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/qr/{qrCodeId}")
    public ResponseEntity<Map<String, Object>> getQRCodeDetails(@PathVariable String qrCodeId) {
        Map<String, Object> response = new HashMap<>();
        
        // Mock QR code data - in real app, fetch from database
        Map<String, Object> qrDetails = generateMockQRDetails(qrCodeId);
        
        if (qrDetails != null) {
            response.put("success", true);
            response.put("data", qrDetails);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "QR code not found or invalid");
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/access/log")
    public ResponseEntity<Map<String, Object>> logAccess(@RequestBody Map<String, Object> accessData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate required fields
            String qrCodeId = (String) accessData.get("qrCodeId");
            String action = (String) accessData.get("action"); // "check-in", "check-out", "override"
            String staffId = (String) accessData.get("staffId");
            String personName = (String) accessData.get("personName");
            String zone = (String) accessData.get("zone");
            
            if (qrCodeId == null || action == null || staffId == null) {
                response.put("success", false);
                response.put("message", "Missing required fields: qrCodeId, action, staffId");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create access log entry
            Map<String, Object> logEntry = new HashMap<>();
            logEntry.put("id", System.currentTimeMillis());
            logEntry.put("qrCodeId", qrCodeId);
            logEntry.put("personName", personName);
            logEntry.put("action", action);
            logEntry.put("zone", zone);
            logEntry.put("staffId", staffId);
            logEntry.put("staffName", getStaffName(staffId));
            logEntry.put("timestamp", LocalDateTime.now());
            logEntry.put("notes", accessData.get("notes"));
            logEntry.put("overrideReason", accessData.get("overrideReason"));
            
            // Add to logs
            ACCESS_LOGS.add(0, logEntry); // Add to beginning for recent first
            
            // Keep only last 1000 entries
            if (ACCESS_LOGS.size() > 1000) {
                ACCESS_LOGS.subList(1000, ACCESS_LOGS.size()).clear();
            }
            
            response.put("success", true);
            response.put("message", "Access logged successfully");
            response.put("logEntry", logEntry);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error logging access: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/access/logs")
    public ResponseEntity<Map<String, Object>> getAccessLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String staffId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String date) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> filteredLogs = new ArrayList<>(ACCESS_LOGS);
            
            // Apply filters
            if (staffId != null && !staffId.isEmpty()) {
                filteredLogs.removeIf(log -> !staffId.equals(log.get("staffId")));
            }
            
            if (action != null && !action.isEmpty()) {
                filteredLogs.removeIf(log -> !action.equals(log.get("action")));
            }
            
            if (date != null && !date.isEmpty()) {
                filteredLogs.removeIf(log -> {
                    LocalDateTime logTime = (LocalDateTime) log.get("timestamp");
                    return !logTime.toLocalDate().toString().equals(date);
                });
            }
            
            // Pagination
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, filteredLogs.size());
            List<Map<String, Object>> pageData = filteredLogs.subList(startIndex, endIndex);
            
            // Format timestamps for response
            pageData.forEach(log -> {
                LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                log.put("formattedTimestamp", timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            });
            
            response.put("success", true);
            response.put("data", pageData);
            response.put("pagination", Map.of(
                "page", page,
                "size", size,
                "total", filteredLogs.size(),
                "totalPages", (int) Math.ceil((double) filteredLogs.size() / size)
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving access logs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime tomorrow = today.plusDays(1);
            
            // Calculate today's stats
            long todayCheckIns = ACCESS_LOGS.stream()
                .filter(log -> {
                    LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                    return timestamp.isAfter(today) && timestamp.isBefore(tomorrow) 
                           && "check-in".equals(log.get("action"));
                })
                .count();
                
            long todayCheckOuts = ACCESS_LOGS.stream()
                .filter(log -> {
                    LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                    return timestamp.isAfter(today) && timestamp.isBefore(tomorrow) 
                           && "check-out".equals(log.get("action"));
                })
                .count();
                
            long todayOverrides = ACCESS_LOGS.stream()
                .filter(log -> {
                    LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                    return timestamp.isAfter(today) && timestamp.isBefore(tomorrow) 
                           && "override".equals(log.get("action"));
                })
                .count();
            
            // Current occupancy (check-ins minus check-outs)
            long currentOccupancy = todayCheckIns - todayCheckOuts;
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("todayCheckIns", todayCheckIns);
            stats.put("todayCheckOuts", todayCheckOuts);
            stats.put("todayOverrides", todayOverrides);
            stats.put("currentOccupancy", Math.max(0, currentOccupancy));
            stats.put("totalLogs", ACCESS_LOGS.size());
            stats.put("activeStaff", STAFF_DATABASE.size());
            
            response.put("success", true);
            response.put("stats", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving dashboard stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Helper methods
    private String getStaffName(String staffId) {
        Map<String, Object> staff = STAFF_DATABASE.get(staffId);
        return staff != null ? (String) staff.get("name") : "Unknown Staff";
    }
    
    private Map<String, Object> generateMockQRDetails(String qrCodeId) {
        // Mock QR code details - in real app, fetch from database
        Map<String, Object> details = new HashMap<>();
        details.put("qrCodeId", qrCodeId);
        details.put("name", "Jean Dupont");
        details.put("role", qrCodeId.contains("HOST") ? "host" : "worker");
        details.put("department", "IT Department");
        details.put("company", "TechCorp Solutions");
        details.put("phone", "+33 6 12 34 56 78");
        details.put("email", "jean.dupont@techcorp.com");
        details.put("location", "Building A - Main Entrance");
        details.put("zone", "Reception Area");
        details.put("purpose", "Client Meeting");
        details.put("description", "Meeting with development team");
        details.put("instructions", "Please escort to meeting room B-205");
        details.put("validFrom", LocalDateTime.now().minusHours(2));
        details.put("validTo", LocalDateTime.now().plusHours(6));
        details.put("status", "valid");
        details.put("createdAt", LocalDateTime.now().minusHours(4));
        details.put("lastAccess", LocalDateTime.now().minusMinutes(30));
        
        return details;
    }
}