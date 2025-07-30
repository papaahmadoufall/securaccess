package com.securaccess.enterprise.resources;

import com.securaccess.enterprise.entities.QrCode;
import com.securaccess.enterprise.services.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/qrcodes")
@CrossOrigin(origins = "*")
public class QrCodeResource {
    
    @Autowired
    private QrCodeService qrCodeService;
    
    @GetMapping
    public ResponseEntity<?> obtenirTous() {
        try {
            List<QrCode> qrCodes = qrCodeService.obtenirTous();
            return ResponseEntity.ok(qrCodes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des QR codes");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenirParId(@PathVariable Long id) {
        try {
            Optional<QrCode> qrCode = qrCodeService.trouverParId(id);
            if (qrCode.isPresent()) {
                return ResponseEntity.ok(qrCode.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "QR Code non trouvé");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération du QR Code");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @PostMapping("/generer")
    public ResponseEntity<?> genererQrCode(
            @RequestParam Long utilisateurId,
            @RequestParam Long evenementId,
            @RequestParam(defaultValue = "24") int validiteDureeHeures) {
        try {
            if (utilisateurId == null || evenementId == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Les paramètres utilisateurId et evenementId sont requis");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (validiteDureeHeures <= 0 || validiteDureeHeures > 8760) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "La durée de validité doit être entre 1 et 8760 heures");
                return ResponseEntity.badRequest().body(error);
            }
            
            QrCode qrCode = qrCodeService.genererQrCodePourUtilisateur(utilisateurId, evenementId, validiteDureeHeures);
            return ResponseEntity.ok(qrCode);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la génération du QR Code");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<?> obtenirParCode(@PathVariable String code) {
        try {
            Optional<QrCode> qrCode = qrCodeService.trouverParCode(code);
            if (qrCode.isPresent()) {
                return ResponseEntity.ok(qrCode.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "QR Code non trouvé");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération du QR Code");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @PostMapping("/valider/{code}")
    public ResponseEntity<?> validerQrCode(@PathVariable String code) {
        try {
            boolean valide = qrCodeService.validerQrCode(code);
            Map<String, Object> response = new HashMap<>();
            response.put("valide", valide);
            response.put("code", code);
            
            if (valide) {
                response.put("message", "QR Code valide");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "QR Code invalide");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la validation du QR Code");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @PostMapping("/utiliser/{code}")
    public ResponseEntity<?> utiliserQrCode(@PathVariable String code) {
        try {
            String resultat = qrCodeService.utiliserQrCode(code);
            Map<String, Object> response = new HashMap<>();
            response.put("code", code);
            response.put("message", resultat);
            
            if ("Accès autorisé".equals(resultat)) {
                response.put("acces", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("acces", false);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de l'utilisation du QR Code");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<?> obtenirParUtilisateur(@PathVariable Long utilisateurId) {
        try {
            List<QrCode> qrCodes = qrCodeService.obtenirParUtilisateur(utilisateurId);
            return ResponseEntity.ok(qrCodes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des QR Codes de l'utilisateur");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @GetMapping("/evenement/{evenementId}")
    public ResponseEntity<?> obtenirParEvenement(@PathVariable Long evenementId) {
        try {
            List<QrCode> qrCodes = qrCodeService.obtenirParEvenement(evenementId);
            return ResponseEntity.ok(qrCodes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des QR Codes de l'événement");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @GetMapping("/actifs")
    public ResponseEntity<?> obtenirQrCodesActifs() {
        try {
            List<QrCode> qrCodes = qrCodeService.obtenirQrCodesActifs();
            return ResponseEntity.ok(qrCodes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des QR Codes actifs");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @PutMapping("/{id}/desactiver")
    public ResponseEntity<?> desactiverQrCode(@PathVariable Long id) {
        try {
            Optional<QrCode> qrCode = qrCodeService.trouverParId(id);
            if (!qrCode.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "QR Code non trouvé");
                return ResponseEntity.notFound().build();
            }
            
            qrCodeService.desactiverQrCode(id);
            Map<String, String> success = new HashMap<>();
            success.put("message", "QR Code désactivé avec succès");
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la désactivation du QR Code");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @PutMapping("/{id}/reactiver")
    public ResponseEntity<?> reactiverQrCode(@PathVariable Long id) {
        try {
            Optional<QrCode> qrCode = qrCodeService.trouverParId(id);
            if (!qrCode.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "QR Code non trouvé");
                return ResponseEntity.notFound().build();
            }
            
            qrCodeService.reactiverQrCode(id);
            Map<String, String> success = new HashMap<>();
            success.put("message", "QR Code réactivé avec succès");
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la réactivation du QR Code");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@PathVariable Long id) {
        try {
            Optional<QrCode> qrCode = qrCodeService.trouverParId(id);
            if (!qrCode.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "QR Code non trouvé");
                return ResponseEntity.notFound().build();
            }
            
            qrCodeService.supprimer(id);
            Map<String, String> success = new HashMap<>();
            success.put("message", "QR Code supprimé avec succès");
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la suppression du QR Code");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @GetMapping("/stats/evenement/{evenementId}")
    public ResponseEntity<?> obtenirStatistiquesEvenement(@PathVariable Long evenementId) {
        try {
            long totalQrCodes = qrCodeService.compterQrCodesParEvenement(evenementId);
            long qrCodesUtilises = qrCodeService.compterQrCodesUtilises(evenementId);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("evenementId", evenementId);
            stats.put("totalQrCodes", totalQrCodes);
            stats.put("qrCodesUtilises", qrCodesUtilises);
            stats.put("qrCodesNonUtilises", totalQrCodes - qrCodesUtilises);
            stats.put("tauxUtilisation", totalQrCodes > 0 ? (double) qrCodesUtilises / totalQrCodes * 100 : 0);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des statistiques");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}