package com.securaccess.enterprise.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.securaccess.enterprise.entities.QrCode;
import com.securaccess.enterprise.entities.Utilisateur;
import com.securaccess.enterprise.entities.Evenement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.securaccess.enterprise.repositories.QrCodeRepository;
import com.securaccess.enterprise.repositories.UtilisateurRepository;
import com.securaccess.enterprise.repositories.EvenementRepository;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;

@Service
public class QrCodeService {
    
    @Autowired
    private QrCodeRepository qrCodeRepository;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private EvenementRepository evenementRepository;
    
    private static final int QR_CODE_SIZE = 300;
    
    public QrCode genererQrCodePourUtilisateur(Long utilisateurId, Long evenementId, int validiteDureeHeures) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(utilisateurId);
        Optional<Evenement> evenement = evenementRepository.findById(evenementId);
        
        if (!utilisateur.isPresent()) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + utilisateurId);
        }
        
        if (!evenement.isPresent()) {
            throw new IllegalArgumentException("Événement non trouvé avec l'ID: " + evenementId);
        }
        
        String codeUnique = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plusHours(validiteDureeHeures);
        
        QrCode qrCode = new QrCode();
        qrCode.setCode(codeUnique);
        qrCode.setDateExpiration(expiration);
        qrCode.setActif(true);
        qrCode.setDateCreation(LocalDateTime.now());
        
        try {
            String qrImageBase64 = genererImageQrCode(codeUnique);
            // Image data would be stored separately or in a different field
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de l'image QR", e);
        }
        
        return qrCodeRepository.save(qrCode);
    }
    
    public List<QrCode> obtenirTous() {
        return qrCodeRepository.findAll();
    }
    
    public Optional<QrCode> trouverParId(Long id) {
        return qrCodeRepository.findById(id);
    }
    
    public Optional<QrCode> trouverParCode(String code) {
        return qrCodeRepository.findByCode(code);
    }
    
    public List<QrCode> obtenirParUtilisateur(Long utilisateurId) {
        // This would need to be implemented with proper relationships
        return qrCodeRepository.findAll(); // Simplified for now
    }
    
    public List<QrCode> obtenirParEvenement(Long evenementId) {
        // This would need to be implemented with proper relationships
        return qrCodeRepository.findAll(); // Simplified for now
    }
    
    public List<QrCode> obtenirQrCodesActifs() {
        // Temporarily returning all active QR codes until complex queries are fixed
        return qrCodeRepository.findByActif(true);
    }
    
    public List<QrCode> findByActive(Boolean active) {
        return qrCodeRepository.findByActif(active);
    }
    
    public boolean validerQrCode(String code) {
        // Simplified validation - just check if code exists and is active
        Optional<QrCode> qrCode = qrCodeRepository.findByCode(code);
        return qrCode.isPresent() && qrCode.get().getActif();
    }
    
    public String utiliserQrCode(String code) {
        Optional<QrCode> qrCodeOpt = qrCodeRepository.findByCode(code);
        
        if (!qrCodeOpt.isPresent() || !qrCodeOpt.get().getActif()) {
            return "QR Code invalide ou expiré";
        }
        
        QrCode qrCode = qrCodeOpt.get();
        // Here you would implement access logging and business logic
        
        return "Accès autorisé";
    }
    
    public void desactiverQrCode(Long id) {
        Optional<QrCode> qrCodeOpt = qrCodeRepository.findById(id);
        if (qrCodeOpt.isPresent()) {
            QrCode qrCode = qrCodeOpt.get();
            qrCode.setActif(false);
            qrCodeRepository.save(qrCode);
        }
    }
    
    public void reactiverQrCode(Long id) {
        Optional<QrCode> qrCodeOpt = qrCodeRepository.findById(id);
        if (qrCodeOpt.isPresent()) {
            QrCode qrCode = qrCodeOpt.get();
            qrCode.setActif(true);
            qrCodeRepository.save(qrCode);
        }
    }
    
    public void supprimer(Long id) {
        qrCodeRepository.deleteById(id);
    }
    
    public long compterQrCodesParEvenement(Long evenementId) {
        // Simplified implementation
        return qrCodeRepository.count();
    }
    
    public long compterQrCodesUtilises(Long evenementId) {
        // Simplified implementation
        return 0L;
    }
    
    private String genererImageQrCode(String contenu) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(contenu, BarcodeFormat.QR_CODE, 
                                                  QR_CODE_SIZE, QR_CODE_SIZE, hints);
        
        BufferedImage image = new BufferedImage(QR_CODE_SIZE, QR_CODE_SIZE, 
                                                BufferedImage.TYPE_INT_RGB);
        
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, QR_CODE_SIZE, QR_CODE_SIZE);
        graphics.setColor(Color.BLACK);
        
        for (int x = 0; x < QR_CODE_SIZE; x++) {
            for (int y = 0; y < QR_CODE_SIZE; y++) {
                if (bitMatrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }
        
        graphics.dispose();
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}