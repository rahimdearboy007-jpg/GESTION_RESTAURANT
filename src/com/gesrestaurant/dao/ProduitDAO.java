/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.dao;
import com.gesrestaurant.model.Produit;
import com.gesrestaurant.model.Categorie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO implements IDAO<Produit> {
    
    private Connection connection;
    private CategorieDAO categorieDao;
    
    public ProduitDAO(Connection connection, CategorieDAO categorieDao) {
        this.connection = connection;
        this.categorieDao = categorieDao;
    }
    
    @Override
    public boolean create(Produit produit) {
        String sql = "INSERT INTO Produit (nom, categorie_id, prix_vente, stock_actuel, seuil_alerte) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produit.getNom());
            stmt.setInt(2, produit.getCategorie().getId());
            stmt.setDouble(3, produit.getPrixVente());
            stmt.setInt(4, produit.getStockActuel());
            stmt.setInt(5, produit.getSeuilAlerte());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    produit.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public Produit read(int id) {
        String sql = "SELECT p.*, c.libelle as categorie_libelle FROM Produit p " +
                     "JOIN Categorie c ON p.categorie_id = c.id WHERE p.id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Categorie categorie = new Categorie(
                    rs.getInt("categorie_id"),
                    rs.getString("categorie_libelle")
                );
                
                return new Produit(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    categorie,
                    rs.getDouble("prix_vente"),
                    rs.getInt("stock_actuel"),
                    rs.getInt("seuil_alerte")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean update(Produit produit) {
        String sql = "UPDATE Produit SET nom = ?, categorie_id = ?, prix_vente = ?, stock_actuel = ?, seuil_alerte = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produit.getNom());
            stmt.setInt(2, produit.getCategorie().getId());
            stmt.setDouble(3, produit.getPrixVente());
            stmt.setInt(4, produit.getStockActuel());
            stmt.setInt(5, produit.getSeuilAlerte());
            stmt.setInt(6, produit.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Produit WHERE id = ?";
        
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
    public List<Produit> findAll() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle as categorie_libelle FROM Produit p " +
                     "JOIN Categorie c ON p.categorie_id = c.id ORDER BY p.nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Categorie categorie = new Categorie(
                    rs.getInt("categorie_id"),
                    rs.getString("categorie_libelle")
                );
                
                produits.add(new Produit(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    categorie,
                    rs.getDouble("prix_vente"),
                    rs.getInt("stock_actuel"),
                    rs.getInt("seuil_alerte")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
    
    // Méthodes supplémentaires
    public List<Produit> findByCategorie(int categorieId) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle as categorie_libelle FROM Produit p " +
                     "JOIN Categorie c ON p.categorie_id = c.id WHERE p.categorie_id = ? ORDER BY p.nom";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categorieId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Categorie categorie = new Categorie(
                    rs.getInt("categorie_id"),
                    rs.getString("categorie_libelle")
                );
                
                produits.add(new Produit(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    categorie,
                    rs.getDouble("prix_vente"),
                    rs.getInt("stock_actuel"),
                    rs.getInt("seuil_alerte")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
    public List<Produit> search(String motCle) {
    List<Produit> produits = new ArrayList<>();
    String sql = "SELECT p.*, c.libelle as categorie_libelle FROM Produit p " +
                 "JOIN Categorie c ON p.categorie_id = c.id " +
                 "WHERE p.nom LIKE ? OR c.libelle LIKE ? " +
                 "ORDER BY p.nom";
    
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        String pattern = "%" + motCle + "%";
        stmt.setString(1, pattern);
        stmt.setString(2, pattern);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Categorie categorie = new Categorie(
                rs.getInt("categorie_id"),
                rs.getString("categorie_libelle")
            );
            
            produits.add(new Produit(
                rs.getInt("id"),
                rs.getString("nom"),
                categorie,
                rs.getDouble("prix_vente"),
                rs.getInt("stock_actuel"),
                rs.getInt("seuil_alerte")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
        return produits;
    }
    
    public List<Produit> findStockBelowSeuil() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle as categorie_libelle FROM Produit p " +
                     "JOIN Categorie c ON p.categorie_id = c.id " +
                     "WHERE p.stock_actuel <= p.seuil_alerte ORDER BY p.stock_actuel ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Categorie categorie = new Categorie(
                    rs.getInt("categorie_id"),
                    rs.getString("categorie_libelle")
                );
                
                produits.add(new Produit(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    categorie,
                    rs.getDouble("prix_vente"),
                    rs.getInt("stock_actuel"),
                    rs.getInt("seuil_alerte")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
    
    public boolean updateStock(int produitId, int quantite) {
        String sql = "UPDATE Produit SET stock_actuel = stock_actuel + ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantite);
            stmt.setInt(2, produitId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}