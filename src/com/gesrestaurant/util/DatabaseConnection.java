/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.util;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static Connection connection = null;
    
    // Paramètres de configuration
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_restaurant";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    // Empêcher l'instanciation
    private DatabaseConnection() {}
    
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Charger le driver MySQL
                Class.forName("com.mysql.jdbc.Driver");
                
                // Établir la connexion
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                
                // Tester la connexion
                if (connection.isValid(2)) {
                    LOGGER.log(Level.INFO, "✅ Connexion BD établie avec succès");
                }
                
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "❌ Driver MySQL non trouvé", e);
                throw new RuntimeException("Driver MySQL non disponible: " + e.getMessage(), e);
                
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "❌ Erreur connexion BD", e);
                throw new RuntimeException("Impossible de se connecter à la base de données: " + e.getMessage(), e);
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                LOGGER.log(Level.INFO, "Connexion BD fermée");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Erreur fermeture connexion BD", e);
            }
        }
    }
    
    public static boolean testConnection() {
        try (Connection testConn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            return testConn.isValid(2);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, " Test connexion BD échoué", e);
            return false;
        }
    }
}