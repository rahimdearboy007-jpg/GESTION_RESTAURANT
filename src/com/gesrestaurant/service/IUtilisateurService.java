/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.service;

import com.gesrestaurant.model.Utilisateur;

public interface IUtilisateurService {
    // Authentification
    Utilisateur authentifier(String login, String motDePasse);
    
    // Gestion utilisateur
    boolean creerUtilisateur(Utilisateur utilisateur);
    boolean modifierUtilisateur(Utilisateur utilisateur);
    boolean supprimerUtilisateur(int id);
    Utilisateur getUtilisateurById(int id);
    
    // Validation
    boolean loginDisponible(String login);
    boolean validerMotDePasse(String motDePasse);
}
