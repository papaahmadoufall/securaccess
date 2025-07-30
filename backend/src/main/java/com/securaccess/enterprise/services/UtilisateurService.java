package com.securaccess.enterprise.services;

import com.securaccess.enterprise.entities.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.securaccess.enterprise.repositories.UtilisateurRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        utilisateur.setDateCreation(LocalDateTime.now());
        return utilisateurRepository.save(utilisateur);
    }
    
    public List<Utilisateur> obtenirTous() {
        return utilisateurRepository.findAll();
    }
    
    public Optional<Utilisateur> trouverParId(Long id) {
        return utilisateurRepository.findById(id);
    }
    
    public Optional<Utilisateur> trouverParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }
    
    public Utilisateur modifierUtilisateur(Utilisateur utilisateur) {
        utilisateur.setDateModification(LocalDateTime.now());
        return utilisateurRepository.save(utilisateur);
    }
    
    public void supprimerUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }
    
    public List<Utilisateur> trouverParType(Utilisateur.TypeUtilisateur type) {
        return utilisateurRepository.findByTypeUtilisateur(type);
    }
    
    public List<Utilisateur> trouverActifs() {
        return utilisateurRepository.findByActif(true);
    }
    
    public boolean emailExiste(String email) {
        return utilisateurRepository.findByEmail(email).isPresent();
    }
    
    public long compterUtilisateursParType(Utilisateur.TypeUtilisateur type) {
        // Method temporarily commented out due to repository method being unavailable
        // return utilisateurRepository.countActiveUsersByType(type);
        return utilisateurRepository.findByTypeUtilisateur(type).size();
    }
}