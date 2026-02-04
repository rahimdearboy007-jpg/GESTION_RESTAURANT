/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.service;

import com.gesrestaurant.dao.CommandeDAO;
import com.gesrestaurant.dao.LigneCommandeDAO;
import com.gesrestaurant.dao.ProduitDAO;
import com.gesrestaurant.model.Commande;
import com.gesrestaurant.model.LigneCommande;
import com.gesrestaurant.model.Produit;
import com.gesrestaurant.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class StatistiqueServiceImpl implements IStatistiqueService {
    
    private CommandeDAO commandeDao;
    private LigneCommandeDAO ligneCommandeDao;
    private ProduitDAO produitDao;
    private Connection connection;
    private IProduitService produitService;
    
    public StatistiqueServiceImpl() {
        this.connection = DatabaseConnection.getConnection();
        this.ligneCommandeDao = new LigneCommandeDAO(connection);
        this.commandeDao = new CommandeDAO(connection, ligneCommandeDao);
        this.produitDao = new ProduitDAO(connection, null);
        this.produitService = new ProduitServiceImpl();
    }
    
    @Override
    public double getChiffreAffaires(Date date) {
        double chiffreAffaires = 0.0;
        String sql = "SELECT SUM(total) as ca FROM commande WHERE etat = 'VALIDEE' AND DATE(date_commande) = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                chiffreAffaires = rs.getDouble("ca");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return chiffreAffaires;
    }
    
    @Override
    public double getChiffreAffairesPeriode(Date dateDebut, Date dateFin) {
        double chiffreAffaires = 0.0;
        String sql = "SELECT SUM(total) as ca FROM commande WHERE etat = 'VALIDEE' " +
                     "AND date_commande BETWEEN ? AND ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
            stmt.setDate(2, new java.sql.Date(dateFin.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                chiffreAffaires = rs.getDouble("ca");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return chiffreAffaires;
    }
    
    @Override
    public Map<String, Integer> getTopProduitsVendus(Date dateDebut, Date dateFin, int limit) {
        Map<String, Integer> topProduits = new LinkedHashMap<>();
        String sql = "SELECT p.nom, SUM(lc.quantite) as total_vendu " +
                     "FROM lignecommande lc " +
                     "JOIN commande c ON lc.commande_id = c.id " +
                     "JOIN produit p ON lc.produit_id = p.id " +
                     "WHERE c.etat = 'VALIDEE' AND c.date_commande BETWEEN ? AND ? " +
                     "GROUP BY p.id, p.nom " +
                     "ORDER BY total_vendu DESC " +
                     "LIMIT ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
            stmt.setDate(2, new java.sql.Date(dateFin.getTime()));
            stmt.setInt(3, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                topProduits.put(
                    rs.getString("nom"),
                    rs.getInt("total_vendu")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return topProduits;
    }
    
    @Override
    public Map<String, Double> getProduitsPlusRentables(Date dateDebut, Date dateFin, int limit) {
        Map<String, Double> produitsRentables = new LinkedHashMap<>();
        String sql = "SELECT p.nom, SUM(lc.montantLigne) as chiffre_affaires " +
                     "FROM lignecommande lc " +
                     "JOIN commande c ON lc.commande_id = c.id " +
                     "JOIN produit p ON lc.produit_id = p.id " +
                     "WHERE c.etat = 'VALIDEE' AND c.date_commande BETWEEN ? AND ? " +
                     "GROUP BY p.id, p.nom " +
                     "ORDER BY chiffre_affaires DESC " +
                     "LIMIT ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
            stmt.setDate(2, new java.sql.Date(dateFin.getTime()));
            stmt.setInt(3, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                produitsRentables.put(
                    rs.getString("nom"),
                    rs.getDouble("chiffre_affaires")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return produitsRentables;
    }
    
    @Override
    public int getNombreProduitsStockFaible() {
        List<Produit> produits = produitService.getProduitsStockFaible();
        return produits != null ? produits.size() : 0;
    }
    
    @Override
    public int getNombreProduitsRupture() {
        int nombreRupture = 0;
        String sql = "SELECT COUNT(*) as nb FROM produit WHERE stock_actuel = 0";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                nombreRupture = rs.getInt("nb");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return nombreRupture;
    }
    
    @Override
    public int getNombreCommandesEnCours() {
        List<Commande> commandes = commandeDao.findByEtat("EN_COURS");
        return commandes != null ? commandes.size() : 0;
    }
    
    @Override
    public int getNombreCommandesValidees(Date date) {
        int nombre = 0;
        String sql = "SELECT COUNT(*) as nb FROM commande WHERE etat = 'VALIDEE' AND DATE(date_commande) = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                nombre = rs.getInt("nb");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return nombre;
    }
    
    @Override
    public double getValeurMoyenneCommande(Date dateDebut, Date dateFin) {
        double moyenne = 0.0;
        String sql = "SELECT AVG(total) as moyenne FROM commande WHERE etat = 'VALIDEE' " +
                     "AND date_commande BETWEEN ? AND ? AND total > 0";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
            stmt.setDate(2, new java.sql.Date(dateFin.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                moyenne = rs.getDouble("moyenne");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return moyenne;
    }
    
    // Méthode supplémentaire utile
    public Map<String, Object> getTableauDeBord() {
        Map<String, Object> dashboard = new HashMap<>();
        
        Date aujourdhui = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(aujourdhui);
        cal.add(Calendar.DATE, -30);
        Date ilYA30Jours = cal.getTime();
        
        // Remplir le tableau de bord
        dashboard.put("chiffreAffairesJour", getChiffreAffaires(aujourdhui));
        dashboard.put("chiffreAffairesMois", getChiffreAffairesPeriode(ilYA30Jours, aujourdhui));
        dashboard.put("commandesEnCours", getNombreCommandesEnCours());
        dashboard.put("produitsStockFaible", getNombreProduitsStockFaible());
        dashboard.put("produitsRupture", getNombreProduitsRupture());
        dashboard.put("topProduits", getTopProduitsVendus(ilYA30Jours, aujourdhui, 5));
        
        return dashboard;
    }
}