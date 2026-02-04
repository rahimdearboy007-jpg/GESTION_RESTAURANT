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
        String sql = "SELECT * FROM mouvementstock WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new MouvementStock(
                    rs.getInt("id"),
                    null, // Produit sera mis à jour après
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
        String sql = "SELECT * FROM mouvementstock ORDER BY date_mouvement DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                mouvements.add(new MouvementStock(
                    rs.getInt("id"),
                    null,
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
    
    // Méthodes spécifiques
    public List<MouvementStock> findByProduit(int produitId) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT * FROM mouvementstock WHERE produit_id = ? ORDER BY date_mouvement DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                mouvements.add(new MouvementStock(
                    rs.getInt("id"),
                    null,
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
        String sql = "SELECT * FROM mouvementstock WHERE type = ? ORDER BY date_mouvement DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                mouvements.add(new MouvementStock(
                    rs.getInt("id"),
                    null,
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
        String sql = "SELECT * FROM mouvementstock WHERE date_mouvement BETWEEN ? AND ? ORDER BY date_mouvement DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, dateDebut);
            stmt.setDate(2, dateFin);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                mouvements.add(new MouvementStock(
                    rs.getInt("id"),
                    null,
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