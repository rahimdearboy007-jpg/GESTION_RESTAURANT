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

public class CommandeDAO implements IDAO<Commande> {
    
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
            stmt.setDate(1, date);
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
}