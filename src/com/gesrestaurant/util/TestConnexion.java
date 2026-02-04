/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.util;

import java.sql.Connection;

public class TestConnexion {
    public static void main(String[] args) {
        System.out.println("=== Test de connexion à la BD ===");
        
        Connection conn = DatabaseConnection.getConnection();
        
        if (conn != null) {
            System.out.println("✅ Connexion réussie !");
            
            try {
                System.out.println("URL: " + conn.getMetaData().getURL());
                System.out.println("User: " + conn.getMetaData().getUserName());
                System.out.println("Driver: " + conn.getMetaData().getDriverName());
                System.out.println("Version: " + conn.getMetaData().getDriverVersion());
                
                // Test simple : compter les catégories
                var stmt = conn.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) as nb FROM Categorie");
                if (rs.next()) {
                    System.out.println("Nombre de catégories: " + rs.getInt("nb"));
                }
                
                DatabaseConnection.closeConnection();
                System.out.println("✅ Test terminé avec succès !");
                
            } catch (Exception e) {
                System.err.println("❌ Erreur lors du test: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("❌ Échec de la connexion !");
        }
    }
}