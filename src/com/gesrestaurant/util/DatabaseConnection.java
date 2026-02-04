/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_restaurant";
    private static final String USER = "root"; // À adapter
    private static final String PASSWORD = ""; // À adapter
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
            Class.forName("com.mysql.jdbc.Driver");                 
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion à la BD réussie !");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Erreur de connexion à la BD: " + e.getMessage());
            e.printStackTrace();
        }
        return connection; 
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture: " + e.getMessage());
        }
    }
}