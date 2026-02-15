/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.util;

import com.gesrestaurant.model.Utilisateur;

public class Session {
    private static Utilisateur utilisateurConnecte;
    
    public static void setUtilisateur(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
    }
    
    public static Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }
    
    public static boolean isAdmin() {
        return utilisateurConnecte != null && utilisateurConnecte.isAdmin();
    }
    
    public static boolean isEmploye() {
        return utilisateurConnecte != null && "EMPLOYE".equals(utilisateurConnecte.getRole());
    }
    
    public static String getRole() {
        return utilisateurConnecte != null ? utilisateurConnecte.getRole() : "INVITÉ";
    }
    
    public static String getLogin() {
        return utilisateurConnecte != null ? utilisateurConnecte.getLogin() : "";
    }
    
    public static void clear() {
        utilisateurConnecte = null;
    }
}
