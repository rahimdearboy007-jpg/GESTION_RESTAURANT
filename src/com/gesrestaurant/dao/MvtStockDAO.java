/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.dao;

import com.gesrestaurant.model.MouvementStock;
import com.gesrestaurant.model.Produit;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MvtStockDAO implements IDAO<MouvementStock> {
    
    private Connection connection;
    
    public MvtStockDAO(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public boolean create(MouvementStock mouvement) {
        String sql = "INSERT INTO mouvementstock (produit_id, type, quantite, date_mouvement, motif) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, mouvement.getProduit().getId());
            stmt.setString(2, mouvement.getType());
            stmt.setInt(3, mouvement.getQuantite());
            stmt.setTimestamp(4, new Timestamp(mouvement.getDateMouvement().getTime()));
            stmt.setString(5, mouvement.getMotif());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    mouvement.setId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public MouvementStock read(int id) {
        String sql = "SELECT m.*, p.id as produit_id, p.nom as produit_nom, " +
                     "p.categorie_id, p.prix_vente, p.stock_actuel, p.seuil_alerte " +
                     "FROM mouvementstock m " +
                     "JOIN produit p ON m.produit_id = p.id " +
                     "WHERE m.id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Créer le produit
                Produit produit = new Produit();
                produit.setId(rs.getInt("produit_id"));
                produit.setNom(rs.getString("produit_nom"));
                
                // Créer et retourner le mouvement
                return new MouvementStock(
                    rs.getInt("id"),
                    produit,
                    rs.getString("type"),
                    rs.getInt("quantite"),
                    rs.getTimestamp("date_mouvement"),
                    rs.getString("motif")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean update(MouvementStock mouvement) {
        String sql = "UPDATE mouvementstock SET motif = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, mouvement.getMotif());
            stmt.setInt(2, mouvement.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM mouvementstock WHERE id = ?";
        
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
    public List<MouvementStock> findAll() {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT m.*, p.id as produit_id, p.nom as produit_nom, " +
                     "p.categorie_id, p.prix_vente, p.stock_actuel, p.seuil_alerte " +
                     "FROM mouvementstock m " +
                     "JOIN produit p ON m.produit_id = p.id " +
                     "ORDER BY m.date_mouvement DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // 1. Créer le produit
                Produit produit = new Produit();
                produit.setId(rs.getInt("produit_id"));
                produit.setNom(rs.getString("produit_nom"));
                
                // 2. Créer le mouvement avec le produit
                MouvementStock mouvement = new MouvementStock(
                    rs.getInt("id"),
                    produit,
                    rs.getString("type"),
                    rs.getInt("quantite"),
                    rs.getTimestamp("date_mouvement"),
                    rs.getString("motif")
                );
                
                mouvements.add(mouvement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mouvements;
    }
    
    // ===== MÉTHODES SPÉCIFIQUES =====
    
    public List<MouvementStock> findByProduit(int produitId) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT m.*, p.id as produit_id, p.nom as produit_nom " +
                     "FROM mouvementstock m " +
                     "JOIN produit p ON m.produit_id = p.id " +
                     "WHERE m.produit_id = ? " +
                     "ORDER BY m.date_mouvement DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("produit_id"));
                produit.setNom(rs.getString("produit_nom"));
                
                mouvements.add(new MouvementStock(
                    rs.getInt("id"),
                    produit,
                    rs.getString("type"),
                    rs.getInt("quantite"),
                    rs.getTimestamp("date_mouvement"),
                    rs.getString("motif")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mouvements;
    }
    
    public List<MouvementStock> findByType(String type) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT m.*, p.id as produit_id, p.nom as produit_nom " +
                     "FROM mouvementstock m " +
                     "JOIN produit p ON m.produit_id = p.id " +
                     "WHERE m.type = ? " +
                     "ORDER BY m.date_mouvement DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("produit_id"));
                produit.setNom(rs.getString("produit_nom"));
                
                mouvements.add(new MouvementStock(
                    rs.getInt("id"),
                    produit,
                    rs.getString("type"),
                    rs.getInt("quantite"),
                    rs.getTimestamp("date_mouvement"),
                    rs.getString("motif")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mouvements;
    }
    
    public List<MouvementStock> findByDateRange(Date dateDebut, Date dateFin) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT m.*, p.id as produit_id, p.nom as produit_nom " +
                     "FROM mouvementstock m " +
                     "JOIN produit p ON m.produit_id = p.id " +
                     "WHERE DATE(m.date_mouvement) BETWEEN ? AND ? " +
                     "ORDER BY m.date_mouvement DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
            stmt.setDate(2, new java.sql.Date(dateFin.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("produit_id"));
                produit.setNom(rs.getString("produit_nom"));
                
                mouvements.add(new MouvementStock(
                    rs.getInt("id"),
                    produit,
                    rs.getString("type"),
                    rs.getInt("quantite"),
                    rs.getTimestamp("date_mouvement"),
                    rs.getString("motif")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mouvements;
    }
    
    public List<MouvementStock> findRecent(int limit) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT m.*, p.id as produit_id, p.nom as produit_nom " +
                     "FROM mouvementstock m " +
                     "JOIN produit p ON m.produit_id = p.id " +
                     "ORDER BY m.date_mouvement DESC LIMIT ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("produit_id"));
                produit.setNom(rs.getString("produit_nom"));
                
                mouvements.add(new MouvementStock(
                    rs.getInt("id"),
                    produit,
                    rs.getString("type"),
                    rs.getInt("quantite"),
                    rs.getTimestamp("date_mouvement"),
                    rs.getString("motif")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mouvements;
    }
}