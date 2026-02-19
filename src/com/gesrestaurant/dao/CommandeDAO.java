/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.dao;

import com.gesrestaurant.model.Commande;
import com.gesrestaurant.model.LigneCommande;
import com.gesrestaurant.model.Produit;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Date;
import java.util.Map;
import java.util.LinkedHashMap;
import java.text.SimpleDateFormat;  
import java.util.Calendar;          
import java.util.Date;           

public class CommandeDAO implements IDAO<Commande> {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CommandeDAO.class.getName());
    
    private Connection connection;
    private LigneCommandeDAO ligneCommandeDao;
    
    public CommandeDAO(Connection connection, LigneCommandeDAO ligneCommandeDao) {
        this.connection = connection;
        this.ligneCommandeDao = ligneCommandeDao;
    }
    
    @Override
    public boolean create(Commande commande) {
        String sql = "INSERT INTO commande (date_commande, etat, total) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, new Timestamp(commande.getDateCommande().getTime()));
            stmt.setString(2, commande.getEtat());
            stmt.setDouble(3, commande.getTotal());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    commande.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public Commande read(int id) {
        String sql = "SELECT * FROM commande WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Commande commande = new Commande(
                    rs.getInt("id"),
                    rs.getTimestamp("date_commande"),
                    rs.getString("etat"),
                    rs.getDouble("total")
                );
                
                // Charger les lignes de commande
                commande.setTotal(calculerTotalCommande(id));
                
                return commande;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean update(Commande commande) {
        String sql = "UPDATE commande SET etat = ?, total = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, commande.getEtat());
            stmt.setDouble(2, commande.getTotal());
            stmt.setInt(3, commande.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        // D'abord supprimer les lignes de commande
        ligneCommandeDao.deleteByCommandeId(id);
        
        String sql = "DELETE FROM commande WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public List<Commande> findAll() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande ORDER BY date_commande DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Commande commande = new Commande(
                    rs.getInt("id"),
                    rs.getTimestamp("date_commande"),
                    rs.getString("etat"),
                    rs.getDouble("total")
                );
                commandes.add(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
    
    // Méthodes spécifiques
    public List<Commande> findByEtat(String etat) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande WHERE etat = ? ORDER BY date_commande DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, etat);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                commandes.add(new Commande(
                    rs.getInt("id"),
                    rs.getTimestamp("date_commande"),
                    rs.getString("etat"),
                    rs.getDouble("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
    
    public List<Commande> findByDate(Date date) {
    List<Commande> commandes = new ArrayList<>();
    String sql = "SELECT * FROM commande WHERE DATE(date_commande) = ? ORDER BY date_commande DESC";
    
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        
        // Convertir java.util.Date en java.sql.Date
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        
        // Utiliser la date dans la requête
        stmt.setDate(1, sqlDate);
        
        // Exécuter la requête
        ResultSet rs = stmt.executeQuery();
        
        // Parcourir les résultats
        while (rs.next()) {
            Commande commande = new Commande(
                rs.getInt("id"),
                rs.getTimestamp("date_commande"),
                rs.getString("etat"),
                rs.getDouble("total")
            );
            commandes.add(commande);
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
        logger.severe("Erreur findByDate: " + e.getMessage());
    }
    
    return commandes;
}
    
    public double calculerTotalCommande(int commandeId) {
        // CORRECTION ICI : montantLigne → montant_ligne
        String sql = "SELECT SUM(montant_ligne) as total FROM lignecommande WHERE commande_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public boolean validerCommande(int commandeId) {
        String sql = "UPDATE commande SET etat = 'VALIDEE' WHERE id = ? AND etat = 'EN_COURS'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean annulerCommande(int commandeId) {
        String sql = "UPDATE commande SET etat = 'ANNULEE' WHERE id = ? AND etat = 'EN_COURS'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public double getCAJour() {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM commande WHERE DATE(date_commande) = CURDATE() AND etat = 'VALIDÉE'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getCASemaine() {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM commande WHERE YEARWEEK(date_commande) = YEARWEEK(CURDATE()) AND etat = 'VALIDÉE'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getCAMois() {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM commande WHERE MONTH(date_commande) = MONTH(CURDATE()) AND YEAR(date_commande) = YEAR(CURDATE()) AND etat = 'VALIDÉE'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public double getCAMoisPrecedent() {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM commande " +
                     "WHERE MONTH(date_commande) = MONTH(CURDATE() - INTERVAL 1 MONTH) " +
                     "AND YEAR(date_commande) = YEAR(CURDATE() - INTERVAL 1 MONTH) " +
                     "AND etat = 'VALIDÉE'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public Map<String, Double> getCA7Jours() {
        Map<String, Double> caParJour = new LinkedHashMap<>();

        // ✅ Jours en français pour l'affichage
        String[] joursFrancais = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};

        // Déterminer quel jour nous sommes aujourd'hui
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.FRENCH);
        String aujourdhui = sdf.format(new Date());
        aujourdhui = aujourdhui.substring(0, 1).toUpperCase() + aujourdhui.substring(1);

        // Trouver l'index d'aujourd'hui
        int indexAujourdhui = -1;
        for (int i = 0; i < joursFrancais.length; i++) {
            if (joursFrancais[i].equals(aujourdhui)) {
                indexAujourdhui = i;
                break;
            }
        }

        // Créer la liste des 7 derniers jours (de J-6 à J-1)
        for (int i = 6; i >= 1; i--) {
            int jourIndex = (indexAujourdhui - i + 7) % 7;
            caParJour.put(joursFrancais[jourIndex], 0.0);
        }

        // ✅ Récupérer les CA réels
        String sql = "SELECT DAYNAME(date_commande) as jour_anglais, SUM(total) as ca " +
                     "FROM commande " +
                     "WHERE date_commande >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                     "AND date_commande < CURDATE() " +
                     "AND etat = 'VALIDÉE' " +
                     "GROUP BY DATE(date_commande)";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String jourAnglais = rs.getString("jour_anglais");
                double ca = rs.getDouble("ca");

                // 🔥 CONVERTIR le jour anglais en français
                String jourFrancais = traduireJour(jourAnglais);

                // Mettre à jour la valeur dans la map
                if (caParJour.containsKey(jourFrancais)) {
                    caParJour.put(jourFrancais, ca);
                    System.out.println("✅ " + jourFrancais + " = " + ca + " F");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return caParJour;
    }

    // 📝 Méthode utilitaire pour traduire les jours
    private String traduireJour(String jourAnglais) {
        switch(jourAnglais) {
            case "Monday":    return "Lundi";
            case "Tuesday":   return "Mardi";
            case "Wednesday": return "Mercredi";
            case "Thursday":  return "Jeudi";
            case "Friday":    return "Vendredi";
            case "Saturday":  return "Samedi";
            case "Sunday":    return "Dimanche";
            default:          return jourAnglais;
        }
    }
    
    public List<Commande> findByDateAndEtat(Date date, String etat) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande WHERE DATE(date_commande) = ? AND etat = ? ORDER BY date_commande DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            stmt.setString(2, etat);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                commandes.add(new Commande(
                    rs.getInt("id"),
                    rs.getTimestamp("date_commande"),
                    rs.getString("etat"),
                    rs.getDouble("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
    


    
}