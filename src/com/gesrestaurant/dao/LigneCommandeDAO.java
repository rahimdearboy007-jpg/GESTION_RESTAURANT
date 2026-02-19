/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.dao;

import com.gesrestaurant.model.LigneCommande;
import com.gesrestaurant.model.Commande;
import com.gesrestaurant.model.Produit;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.gesrestaurant.model.Categorie;

public class LigneCommandeDAO implements IDAO<LigneCommande> {
    
    private Connection connection;
    
    public LigneCommandeDAO(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public boolean create(LigneCommande ligne) {
        // CORRECTION ICI : ajout de montant_ligne dans l'INSERT
        String sql = "INSERT INTO lignecommande (commande_id, produit_id, quantite, prix_unitaire, montant_ligne) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ligne.getCommande().getId());
            stmt.setInt(2, ligne.getProduit().getId());
            stmt.setInt(3, ligne.getQuantite());
            stmt.setDouble(4, ligne.getPrixUnitaire());
            // Calcul du montant de la ligne : quantite * prix_unitaire
            stmt.setDouble(5, ligne.getQuantite() * ligne.getPrixUnitaire());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    ligne.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
public LigneCommande read(int id) {
    String sql = "SELECT lc.*, " +
                 "p.id as produit_id, p.nom as produit_nom, p.prix_vente, " +
                 "c.id as categorie_id, c.libelle as categorie_libelle " +
                 "FROM lignecommande lc " +
                 "JOIN produit p ON lc.produit_id = p.id " +
                 "LEFT JOIN categorie c ON p.categorie_id = c.id " +
                 "WHERE lc.id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            // Créer la catégorie
            Categorie categorie = null;
            int catId = rs.getInt("categorie_id");
            if (!rs.wasNull()) {
                categorie = new Categorie(catId, rs.getString("categorie_libelle"));
            }

            // Créer le produit
            Produit produit = new Produit();
            produit.setId(rs.getInt("produit_id"));
            produit.setNom(rs.getString("produit_nom"));
            produit.setPrixVente(rs.getDouble("prix_vente"));
            produit.setCategorie(categorie);
            
            // Créer la commande (juste l'ID)
            Commande commande = new Commande();
            commande.setId(rs.getInt("commande_id"));

            // Créer la ligne
            LigneCommande ligne = new LigneCommande();
            ligne.setId(rs.getInt("id"));
            ligne.setCommande(commande);
            ligne.setProduit(produit);
            ligne.setQuantite(rs.getInt("quantite"));
            ligne.setPrixUnitaire(rs.getDouble("prix_unitaire"));
            
            try {
                ligne.setMontantLigne(rs.getDouble("montant_ligne"));
            } catch (SQLException e) {
                ligne.setMontantLigne(rs.getInt("quantite") * rs.getDouble("prix_unitaire"));
            }

            return ligne;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
    
    @Override
    public boolean update(LigneCommande ligne) {
        // CORRECTION ICI : ajout de montant_ligne dans l'UPDATE
        String sql = "UPDATE lignecommande SET quantite = ?, prix_unitaire = ?, montant_ligne = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ligne.getQuantite());
            stmt.setDouble(2, ligne.getPrixUnitaire());
            // Calcul du nouveau montant
            stmt.setDouble(3, ligne.getQuantite() * ligne.getPrixUnitaire());
            stmt.setInt(4, ligne.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM lignecommande WHERE id = ?";
        
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
    public List<LigneCommande> findAll() {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT * FROM lignecommande ORDER BY commande_id, id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                LigneCommande ligne = new LigneCommande(
                    rs.getInt("id"),
                    null,
                    null,
                    rs.getInt("quantite"),
                    rs.getDouble("prix_unitaire")
                );
                // Si votre modèle a un attribut pour montant_ligne :
                // ligne.setMontantLigne(rs.getDouble("montant_ligne"));
                lignes.add(ligne);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lignes;
    }
    
    // Méthodes spécifiques
    public List<LigneCommande> findByCommandeId(int commandeId) {
    List<LigneCommande> lignes = new ArrayList<>();
    
    // ✅ REQUÊTE AVEC JOINTURE POUR CHARGER LES PRODUITS
    String sql = "SELECT lc.*, " +
                 "p.id as produit_id, p.nom as produit_nom, p.prix_vente, " +
                 "c.id as categorie_id, c.libelle as categorie_libelle " +
                 "FROM lignecommande lc " +
                 "JOIN produit p ON lc.produit_id = p.id " +
                 "LEFT JOIN categorie c ON p.categorie_id = c.id " +
                 "WHERE lc.commande_id = ? " +
                 "ORDER BY lc.id";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, commandeId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            // ✅ 1. Créer la catégorie
            Categorie categorie = null;
            int catId = rs.getInt("categorie_id");
            if (!rs.wasNull()) {
                categorie = new Categorie(catId, rs.getString("categorie_libelle"));
            }

            // ✅ 2. Créer le produit avec toutes ses infos
            Produit produit = new Produit();
            produit.setId(rs.getInt("produit_id"));
            produit.setNom(rs.getString("produit_nom"));
            produit.setPrixVente(rs.getDouble("prix_vente"));
            produit.setCategorie(categorie);
            
            // ✅ 3. Créer la commande (juste l'ID)
            Commande commande = new Commande();
            commande.setId(commandeId);

            // ✅ 4. Créer la ligne de commande
            LigneCommande ligne = new LigneCommande();
            ligne.setId(rs.getInt("id"));
            ligne.setCommande(commande);
            ligne.setProduit(produit);
            ligne.setQuantite(rs.getInt("quantite"));
            ligne.setPrixUnitaire(rs.getDouble("prix_unitaire"));
            
            // Récupérer montant_ligne s'il existe dans la table
            try {
                double montant = rs.getDouble("montant_ligne");
                ligne.setMontantLigne(montant);
            } catch (SQLException e) {
                // Si la colonne n'existe pas, calculer
                ligne.setMontantLigne(rs.getInt("quantite") * rs.getDouble("prix_unitaire"));
            }

            lignes.add(ligne);
        }
        
        System.out.println("📦 Lignes trouvées pour commande #" + commandeId + " : " + lignes.size());

    } catch (SQLException e) {
        System.err.println("❌ Erreur findByCommandeId: " + e.getMessage());
        e.printStackTrace();
    }
    return lignes;
}
    
    public boolean deleteByCommandeId(int commandeId) {
        String sql = "DELETE FROM lignecommande WHERE commande_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public double getTotalByCommandeId(int commandeId) {
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
    
    public List<Object[]> getTopProduits(int limite) {
        List<Object[]> top = new ArrayList<>();
        String sql = "SELECT p.nom, c.libelle, SUM(lc.quantite) as total_qte, SUM(lc.montant_ligne) as total_ca " +
                     "FROM lignecommande lc " +
                     "JOIN produit p ON lc.produit_id = p.id " +
                     "JOIN commande cmd ON lc.commande_id = cmd.id " +
                     "JOIN categorie c ON p.categorie_id = c.id " +
                     "WHERE cmd.etat = 'VALIDÉE' " +
                     "GROUP BY p.id " +
                     "ORDER BY total_qte DESC " +
                     "LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            ResultSet rs = stmt.executeQuery();

            int rang = 1;
            while (rs.next()) {
                top.add(new Object[]{
                    rang++,
                    rs.getString("nom"),
                    rs.getString("libelle"),
                    rs.getInt("total_qte"),
                    rs.getDouble("total_ca")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return top;
    }
    
    
}