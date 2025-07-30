package com.securaccess.enterprise.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/qrcode")
@CrossOrigin(origins = "*")
public class QRCodeController {

    // Mock QR codes database
    private static final Map<String, Map<String, Object>> QR_CODES_DATABASE = new HashMap<>();
    
    static {
        // Initialize some mock QR codes
        createMockQRCode("QR001", "worker", "Jean Dupont", "IT Department");
        createMockQRCode("QR002", "host", "Marie Martin", "TechCorp Solutions");
        createMockQRCode("QR003", "worker", "Pierre Durand", "Security");
    }
    
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateQRCode(@RequestBody Map<String, Object> qrData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate required fields
            String personType = (String) qrData.get("personType"); // "worker" or "host"
            String name = (String) qrData.get("name");
            String phone = (String) qrData.get("phone");
            String location = (String) qrData.get("location");
            String zone = (String) qrData.get("zone");
            
            if (personType == null || name == null || phone == null || location == null) {
                response.put("success", false);
                response.put("message", "Missing required fields: personType, name, phone, location");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Generate unique QR code ID
            String qrCodeId = generateQRCodeId(personType);
            
            // Parse validity period
            String validFromStr = (String) qrData.get("validFrom");
            String validToStr = (String) qrData.get("validTo");
            
            LocalDateTime validFrom = validFromStr != null ? 
                LocalDateTime.parse(validFromStr) : LocalDateTime.now();
            LocalDateTime validTo = validToStr != null ? 
                LocalDateTime.parse(validToStr) : LocalDateTime.now().plusDays(1);
            
            // Create QR code data
            Map<String, Object> qrCodeData = new HashMap<>();
            qrCodeData.put("qrCodeId", qrCodeId);
            qrCodeData.put("personType", personType);
            qrCodeData.put("name", name);
            qrCodeData.put("phone", phone);
            qrCodeData.put("email", qrData.get("email"));
            qrCodeData.put("location", location);
            qrCodeData.put("zone", zone);
            qrCodeData.put("department", qrData.get("department"));
            qrCodeData.put("company", qrData.get("company"));
            qrCodeData.put("purpose", qrData.get("purpose"));
            qrCodeData.put("description", qrData.get("description"));
            qrCodeData.put("instructions", qrData.get("instructions"));
            qrCodeData.put("validFrom", validFrom);
            qrCodeData.put("validTo", validTo);
            qrCodeData.put("status", "active");
            qrCodeData.put("createdAt", LocalDateTime.now());
            qrCodeData.put("createdBy", qrData.get("createdBy"));
            qrCodeData.put("lastAccess", null);
            qrCodeData.put("accessCount", 0);
            
            // Generate QR code URL
            String qrCodeUrl = generateQRCodeUrl(qrCodeId);
            qrCodeData.put("qrCodeUrl", qrCodeUrl);
            qrCodeData.put("accessUrl", "http://localhost:3000/qr/" + qrCodeId);
            
            // Store in database
            QR_CODES_DATABASE.put(qrCodeId, qrCodeData);
            
            response.put("success", true);
            response.put("message", "QR code generated successfully");
            response.put("qrCode", qrCodeData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error generating QR code: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{qrCodeId}")
    public ResponseEntity<Map<String, Object>> getQRCode(@PathVariable String qrCodeId) {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, Object> qrCode = QR_CODES_DATABASE.get(qrCodeId);
        
        if (qrCode != null) {
            // Check if QR code is still valid
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime validFrom = (LocalDateTime) qrCode.get("validFrom");
            LocalDateTime validTo = (LocalDateTime) qrCode.get("validTo");
            
            String status = "active";
            if (now.isBefore(validFrom)) {
                status = "not_yet_valid";
            } else if (now.isAfter(validTo)) {
                status = "expired";
            }
            
            qrCode.put("currentStatus", status);
            
            response.put("success", true);
            response.put("data", qrCode);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "QR code not found");
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listQRCodes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String personType,
            @RequestParam(required = false) String status) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> allQRCodes = new ArrayList<>(QR_CODES_DATABASE.values());
            
            // Apply filters
            if (personType != null && !personType.isEmpty()) {
                allQRCodes.removeIf(qr -> !personType.equals(qr.get("personType")));
            }
            
            if (status != null && !status.isEmpty()) {
                allQRCodes.removeIf(qr -> !status.equals(qr.get("status")));
            }
            
            // Sort by creation date (newest first)
            allQRCodes.sort((a, b) -> {
                LocalDateTime dateA = (LocalDateTime) a.get("createdAt");
                LocalDateTime dateB = (LocalDateTime) b.get("createdAt");
                return dateB.compareTo(dateA);
            });
            
            // Pagination
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, allQRCodes.size());
            List<Map<String, Object>> pageData = allQRCodes.subList(startIndex, endIndex);
            
            // Format dates for response
            pageData.forEach(qr -> {
                LocalDateTime createdAt = (LocalDateTime) qr.get("createdAt");
                LocalDateTime validFrom = (LocalDateTime) qr.get("validFrom");
                LocalDateTime validTo = (LocalDateTime) qr.get("validTo");
                
                qr.put("formattedCreatedAt", createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                qr.put("formattedValidFrom", validFrom.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                qr.put("formattedValidTo", validTo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                
                // Update current status
                LocalDateTime now = LocalDateTime.now();
                String currentStatus = "active";
                if (now.isBefore(validFrom)) {
                    currentStatus = "not_yet_valid";
                } else if (now.isAfter(validTo)) {
                    currentStatus = "expired";
                }
                qr.put("currentStatus", currentStatus);
            });
            
            response.put("success", true);
            response.put("data", pageData);
            response.put("pagination", Map.of(
                "page", page,
                "size", size,
                "total", allQRCodes.size(),
                "totalPages", (int) Math.ceil((double) allQRCodes.size() / size)
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving QR codes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{qrCodeId}/status")
    public ResponseEntity<Map<String, Object>> updateQRCodeStatus(
            @PathVariable String qrCodeId, 
            @RequestBody Map<String, String> statusData) {
        
        Map<String, Object> response = new HashMap<>();
        
        Map<String, Object> qrCode = QR_CODES_DATABASE.get(qrCodeId);
        
        if (qrCode != null) {
            String newStatus = statusData.get("status");
            if (Arrays.asList("active", "suspended", "revoked").contains(newStatus)) {
                qrCode.put("status", newStatus);
                qrCode.put("statusUpdatedAt", LocalDateTime.now());
                qrCode.put("statusUpdatedBy", statusData.get("updatedBy"));
                
                response.put("success", true);
                response.put("message", "QR code status updated successfully");
                response.put("qrCode", qrCode);
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid status. Must be: active, suspended, or revoked");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.put("success", false);
            response.put("message", "QR code not found");
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{qrCodeId}")
    public ResponseEntity<Map<String, Object>> deleteQRCode(@PathVariable String qrCodeId) {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, Object> removedQRCode = QR_CODES_DATABASE.remove(qrCodeId);
        
        if (removedQRCode != null) {
            response.put("success", true);
            response.put("message", "QR code deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "QR code not found");
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{qrCodeId}/image")
    public ResponseEntity<byte[]> getQRCodeImage(@PathVariable String qrCodeId) {
        // Mock QR code image generation
        // In a real implementation, you would use a QR code library like ZXing
        byte[] mockImage = generateMockQRCodeImage(qrCodeId);
        
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(mockImage);
    }
    
    // Helper methods
    private String generateQRCodeId(String personType) {
        String prefix = personType.equals("host") ? "HOST" : "WORK";
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999);
        return prefix + String.format("%04d", randomNum);
    }
    
    private String generateQRCodeUrl(String qrCodeId) {
        // In a real implementation, this would generate an actual QR code
        return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
    }
    
    private byte[] generateMockQRCodeImage(String qrCodeId) {
        // Mock image - in real implementation, use ZXing library
        return new byte[]{-119, 80, 78, 71, 13, 10, 26, 10}; // PNG header
    }
    
    private static void createMockQRCode(String id, String type, String name, String org) {
        Map<String, Object> qrCode = new HashMap<>();
        qrCode.put("qrCodeId", id);
        qrCode.put("personType", type);
        qrCode.put("name", name);
        qrCode.put("phone", "+33 6 12 34 56 78");
        qrCode.put("location", "Building A - Main Entrance");
        qrCode.put("zone", "Reception Area");
        qrCode.put("department", org);
        qrCode.put("validFrom", LocalDateTime.now().minusHours(1));
        qrCode.put("validTo", LocalDateTime.now().plusDays(7));
        qrCode.put("status", "active");
        qrCode.put("createdAt", LocalDateTime.now().minusHours(2));
        qrCode.put("accessCount", 0);
        
        QR_CODES_DATABASE.put(id, qrCode);
    }
}