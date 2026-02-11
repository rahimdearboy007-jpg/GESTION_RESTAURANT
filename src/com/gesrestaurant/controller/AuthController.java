/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Contrôleur d'authentification pour l'application Gestion Restaurant
 * Gère la logique de connexion des utilisateurs
 */
package com.gesrestaurant.controller;

import com.gesrestaurant.model.Utilisateur;
import com.gesrestaurant.service.IUtilisateurService;

/**
 * Contrôleur responsable de l'authentification des utilisateurs
 * Conforme aux spécifications 2.7 du sujet (Gestion des utilisateurs simplifiée)
 * 
 * @author rahim
 * @version 1.0
 */
public class AuthController {
    
    /** Service de gestion des utilisateurs */
    private final IUtilisateurService utilisateurService;
    
    /**
     * Constructeur par défaut
     * Initialise le service utilisateur avec son implémentation
     */
    public AuthController() {
        this.utilisateurService = new com.gesrestaurant.service.UtilisateurServiceImpl();
    }
    
    /**
     * Authentifie un utilisateur avec son login et mot de passe
     * 
     * @param login Login de l'utilisateur
     * @param motDePasse Mot de passe de l'utilisateur
     * @return true si l'authentification réussit, false sinon
     */
    public boolean authentifier(String login, String motDePasse) {
        // Validation des paramètres d'entrée
        if (!validerParametres(login, motDePasse)) {
            return false;
        }
        
        // Appel au service d'authentification
        Utilisateur utilisateur = utilisateurService.authentifier(
            login.trim(), 
            motDePasse.trim()
        );
        
        // L'authentification réussit si un utilisateur est retourné
        return utilisateur != null;
    }
    
    /**
     * Vérifie si un login est disponible pour l'inscription
     * 
     * @param login Login à vérifier
     * @return true si le login est disponible, false sinon
     */
    public boolean loginDisponible(String login) {
        if (login == null || login.trim().isEmpty()) {
            return false;
        }
        return utilisateurService.loginDisponible(login.trim());
    }
    
    /**
     * Valide les paramètres d'authentification
     * 
     * @param login Login à valider
     * @param motDePasse Mot de passe à valider
     * @return true si les paramètres sont valides, false sinon
     */
    private boolean validerParametres(String login, String motDePasse) {
        // Vérification de null
        if (login == null || motDePasse == null) {
            return false;
        }
        
        // Vérification des chaînes vides
        if (login.trim().isEmpty() || motDePasse.trim().isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Récupère l'utilisateur authentifié (pour usage futur)
     * 
     * @param login Login de l'utilisateur
     * @param motDePasse Mot de passe de l'utilisateur
     * @return L'utilisateur authentifié ou null si échec
     */
    public Utilisateur getUtilisateurAuthentifie(String login, String motDePasse) {
        if (!validerParametres(login, motDePasse)) {
            return null;
        }
        return utilisateurService.authentifier(login.trim(), motDePasse.trim());
    }
}