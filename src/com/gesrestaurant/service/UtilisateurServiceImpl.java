/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.service;

import com.gesrestaurant.dao.UtilisateurDAO;
import com.gesrestaurant.model.Utilisateur;
import com.gesrestaurant.util.DatabaseConnection;
import java.sql.Connection;
import java.util.List;

public class UtilisateurServiceImpl implements IUtilisateurService {
    
    private UtilisateurDAO utilisateurDao;
    
    public UtilisateurServiceImpl() {
        Connection conn = DatabaseConnection.getConnection();
        this.utilisateurDao = new UtilisateurDAO(conn);
    }
    
    @Override
    public Utilisateur authentifier(String login, String motDePasse) {
        // Règle 1 : Login non vide
        if (login == null || login.trim().isEmpty()) {
            System.err.println("Erreur : Login vide !");
            return null;
        }
        
        // Règle 2 : Mot de passe non vide
        if (motDePasse == null || motDePasse.trim().isEmpty()) {
            System.err.println("Erreur : Mot de passe vide !");
            return null;
        }
        
        // Règle 3 : Vérifier dans la BD
        Utilisateur utilisateur = utilisateurDao.authenticate(login, motDePasse);
        
        if (utilisateur == null) {
            System.err.println("Erreur : Login ou mot de passe incorrect !");
        }
        
        return utilisateur;
    }
    
    @Override
    public boolean creerUtilisateur(Utilisateur utilisateur) {
        // Règle 1 : Login non vide
        if (utilisateur.getLogin() == null || utilisateur.getLogin().trim().isEmpty()) {
            System.err.println("Erreur : Login vide !");
            return false;
        }
        
        // Règle 2 : Mot de passe non vide
        if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().trim().isEmpty()) {
            System.err.println("Erreur : Mot de passe vide !");
            return false;
        }
        
        // Règle 3 : Rôle obligatoire
        if (utilisateur.getRole() == null || utilisateur.getRole().trim().isEmpty()) {
            System.err.println("Erreur : Rôle vide !");
            return false;
        }
        
        // Règle 4 : Login unique
        if (!loginDisponible(utilisateur.getLogin())) {
            System.err.println("Erreur : Login '" + utilisateur.getLogin() + "' déjà utilisé !");
            return false;
        }
        
        // Règle 5 : Valider le mot de passe
        if (!validerMotDePasse(utilisateur.getMotDePasse())) {
            System.err.println("Erreur : Mot de passe trop court (min 4 caractères) !");
            return false;
        }
        
        return utilisateurDao.create(utilisateur);
    }
    
    @Override
    public boolean modifierUtilisateur(Utilisateur utilisateur) {
        // Règle 1 : Vérifier que l'utilisateur existe
        Utilisateur existant = utilisateurDao.read(utilisateur.getId());
        if (existant == null) {
            System.err.println("Erreur : Utilisateur #" + utilisateur.getId() + " n'existe pas !");
            return false;
        }
        
        // Règle 2 : Si login change, vérifier qu'il est disponible
        if (!existant.getLogin().equals(utilisateur.getLogin())) {
            if (!loginDisponible(utilisateur.getLogin())) {
                System.err.println("Erreur : Login '" + utilisateur.getLogin() + "' déjà utilisé !");
                return false;
            }
        }
        
        // Règle 3 : Rôle obligatoire
        if (utilisateur.getRole() == null || utilisateur.getRole().trim().isEmpty()) {
            System.err.println("Erreur : Rôle vide !");
            return false;
        }
        
        // Règle 4 : Valider mot de passe
        if (!validerMotDePasse(utilisateur.getMotDePasse())) {
            System.err.println("Erreur : Mot de passe trop court (min 4 caractères) !");
            return false;
        }
        
        return utilisateurDao.update(utilisateur);
    }
    
    @Override
    public boolean supprimerUtilisateur(int id) {
        // Règle : Ne pas supprimer le dernier administrateur
        List<Utilisateur> admins = utilisateurDao.findByRole("ADMIN");
        if (admins.size() <= 1) {
            Utilisateur aSupprimer = utilisateurDao.read(id);
            if (aSupprimer != null && aSupprimer.isAdmin()) {
                System.err.println("Erreur : Impossible de supprimer le dernier administrateur !");
                return false;
            }
        }
        
        return utilisateurDao.delete(id);
    }
    
    @Override
    public Utilisateur getUtilisateurById(int id) {
        return utilisateurDao.read(id);
    }
    
    @Override
    public boolean loginDisponible(String login) {
        return !utilisateurDao.loginExists(login);
    }
    
    @Override
    public boolean validerMotDePasse(String motDePasse) {
        // Règle simple : au moins 4 caractères
        return motDePasse != null && motDePasse.length() >= 4;
    }
    
    // ✅ NOUVELLE MÉTHODE : Récupérer tous les utilisateurs
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurDao.findAll();
    }
    
    // ✅ NOUVELLE MÉTHODE : Récupérer les utilisateurs par rôle
    public List<Utilisateur> getUtilisateursByRole(String role) {
        return utilisateurDao.findByRole(role);
    }
}