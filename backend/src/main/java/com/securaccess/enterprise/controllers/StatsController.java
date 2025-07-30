package com.securaccess.enterprise.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/stats")
@CrossOrigin(origins = "*")
public class StatsController {
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Mock dashboard statistics
            stats.put("totalWorkers", 15);
            stats.put("activeWorkers", 12);
            stats.put("totalHosts", 8);
            stats.put("activeHosts", 6);
            stats.put("totalQrCodes", 156);
            stats.put("activeQrCodes", 23);
            stats.put("todayAccesses", 67);
            stats.put("weeklyAccesses", 432);
            stats.put("monthlyAccesses", 1856);
            
            // Recent activities
            List<Map<String, Object>> recentActivities = new ArrayList<>();
            
            Map<String, Object> activity1 = new HashMap<>();
            activity1.put("id", "1");
            activity1.put("type", "worker_access");
            activity1.put("message", "Jean Dupont a accédé à l'IT Office");
            activity1.put("timestamp", LocalDateTime.now().minusMinutes(15).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            activity1.put("status", "success");
            recentActivities.add(activity1);
            
            Map<String, Object> activity2 = new HashMap<>();
            activity2.put("id", "2");
            activity2.put("type", "host_qr_generated");
            activity2.put("message", "QR Code généré pour Marie Martin");
            activity2.put("timestamp", LocalDateTime.now().minusMinutes(32).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            activity2.put("status", "info");
            recentActivities.add(activity2);
            
            Map<String, Object> activity3 = new HashMap<>();
            activity3.put("id", "3");
            activity3.put("type", "worker_created");
            activity3.put("message", "Nouvel employé créé: Pierre Durand");
            activity3.put("timestamp", LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            activity3.put("status", "success");
            recentActivities.add(activity3);
            
            Map<String, Object> activity4 = new HashMap<>();
            activity4.put("id", "4");
            activity4.put("type", "access_denied");
            activity4.put("message", "Accès refusé - QR Code expiré");
            activity4.put("timestamp", LocalDateTime.now().minusHours(3).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            activity4.put("status", "warning");
            recentActivities.add(activity4);
            
            stats.put("recentActivities", recentActivities);
            
            // Department statistics
            List<Map<String, Object>> departmentStats = new ArrayList<>();
            
            Map<String, Object> dept1 = new HashMap<>();
            dept1.put("name", "IT");
            dept1.put("workers", 8);
            dept1.put("accesses", 156);
            dept1.put("percentage", 35.2);
            departmentStats.add(dept1);
            
            Map<String, Object> dept2 = new HashMap<>();
            dept2.put("name", "Marketing");
            dept2.put("workers", 4);
            dept2.put("accesses", 89);
            dept2.put("percentage", 20.1);
            departmentStats.add(dept2);
            
            Map<String, Object> dept3 = new HashMap<>();
            dept3.put("name", "Sales");
            dept3.put("workers", 3);
            dept3.put("accesses", 123);
            dept3.put("percentage", 27.8);
            departmentStats.add(dept3);
            
            stats.put("departmentStats", departmentStats);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la récupération des statistiques");
        }
    }
    
    @GetMapping("/access")
    public ResponseEntity<Map<String, Object>> getAccessStats(@RequestParam(value = "period", defaultValue = "week") String period) {
        try {
            Map<String, Object> accessStats = new HashMap<>();
            
            // Mock access statistics based on period
            List<Map<String, Object>> chartData = new ArrayList<>();
            
            if ("week".equals(period)) {
                String[] days = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
                int[] accesses = {45, 62, 58, 71, 69, 23, 15};
                
                for (int i = 0; i < days.length; i++) {
                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("label", days[i]);
                    dataPoint.put("value", accesses[i]);
                    dataPoint.put("date", LocalDateTime.now().minusDays(6-i).toLocalDate().toString());
                    chartData.add(dataPoint);
                }
                
                accessStats.put("total", 343);
                accessStats.put("average", 49);
                accessStats.put("peak", 71);
                accessStats.put("peakDay", "Jeudi");
                
            } else if ("month".equals(period)) {
                for (int i = 1; i <= 30; i++) {
                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("label", String.valueOf(i));
                    dataPoint.put("value", 30 + (int)(Math.random() * 50));
                    dataPoint.put("date", LocalDateTime.now().minusDays(30-i).toLocalDate().toString());
                    chartData.add(dataPoint);
                }
                
                accessStats.put("total", 1247);
                accessStats.put("average", 41);
                accessStats.put("peak", 78);
                accessStats.put("peakDay", "15");
            }
            
            accessStats.put("chartData", chartData);
            accessStats.put("period", period);
            
            // Access by type
            Map<String, Object> accessTypes = new HashMap<>();
            accessTypes.put("entry", 178);
            accessTypes.put("exit", 165);
            accessStats.put("accessTypes", accessTypes);
            
            // Most active hours
            List<Map<String, Object>> hourlyStats = new ArrayList<>();
            int[] hours = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
            int[] counts = {12, 45, 38, 42, 67, 23, 55, 41, 38, 52, 8};
            
            for (int i = 0; i < hours.length; i++) {
                Map<String, Object> hourStat = new HashMap<>();
                hourStat.put("hour", String.format("%02d:00", hours[i]));
                hourStat.put("count", counts[i]);
                hourlyStats.add(hourStat);
            }
            
            accessStats.put("hourlyStats", hourlyStats);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accessStats", accessStats);
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Erreur lors de la récupération des statistiques d'accès");
        }
    }
    
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.internalServerError().body(error);
    }
}