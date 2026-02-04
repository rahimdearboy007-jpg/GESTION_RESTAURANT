/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.service;

import com.gesrestaurant.dao.ProduitDAO;
import com.gesrestaurant.dao.CategorieDAO;
import com.gesrestaurant.dao.MvtStockDAO;
import com.gesrestaurant.model.Produit;
import com.gesrestaurant.model.MouvementStock;
import com.gesrestaurant.util.DatabaseConnection;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

public class ProduitServiceImpl implements IProduitService {
    
    private ProduitDAO produitDao;
    private CategorieDAO categorieDao;
    private MvtStockDAO mouvementDao;
    
    public ProduitServiceImpl() {
        Connection conn = DatabaseConnection.getConnection();
        this.categorieDao = new CategorieDAO(conn);
        this.produitDao = new ProduitDAO(conn, categorieDao);
        this.mouvementDao = new MvtStockDAO(conn);
    }
    
    @Override
    public boolean ajouterProduit(Produit produit) {
        // Règle 1 : Vérifier que la catégorie existe
        if (produit.getCategorie() == null || produit.getCategorie().getId() == 0) {
            System.err.println("Erreur : Catégorie invalide !");
            return false;
        }
        
        if (categorieDao.read(produit.getCategorie().getId()) == null) {
            System.err.println("Erreur : Catégorie #" + produit.getCategorie().getId() + " n'existe pas !");
            return false;
        }
        
        // Règle 2 : Prix > 0
        if (produit.getPrixVente() <= 0) {
            System.err.println("Erreur : Prix doit être > 0 !");
            return false;
        }
        
        // Règle 3 : Stock initial >= 0
        if (produit.getStockActuel() < 0) {
            System.err.println("Erreur : Stock ne peut pas être négatif !");
            return false;
        }
        
        // Règle 4 : Seuil alerte >= 0
        if (produit.getSeuilAlerte() < 0) {
            System.err.println("Erreur : Seuil alerte ne peut pas être négatif !");
            return false;
        }
        
        boolean success = produitDao.create(produit);
        
        // Si produit créé et stock initial > 0, créer un mouvement d'entrée
        if (success && produit.getStockActuel() > 0) {
            MouvementStock mouvement = new MouvementStock(
                produit, 
                "ENTREE", 
                produit.getStockActuel(), 
                "Stock initial"
            );
            mouvementDao.create(mouvement);
        }
        
        return success;
    }
    
    @Override
    public boolean modifierProduit(Produit produit) {
        // Règle 1 : Vérifier que le produit existe
        Produit existant = produitDao.read(produit.getId());
        if (existant == null) {
            System.err.println("Erreur : Produit #" + produit.getId() + " n'existe pas !");
            return false;
        }
        
        // Règle 2 : Vérifier catégorie
        if (categorieDao.read(produit.getCategorie().getId()) == null) {
            System.err.println("Erreur : Catégorie invalide !");
            return false;
        }
        
        // Règle 3 : Prix > 0
        if (produit.getPrixVente() <= 0) {
            System.err.println("Erreur : Prix doit être > 0 !");
            return false;
        }
        
        // Règle 4 : Stock actuel >= 0
        if (produit.getStockActuel() < 0) {
            System.err.println("Erreur : Stock ne peut pas être négatif !");
            return false;
        }
        
        // Règle 5 : Seuil alerte >= 0
        if (produit.getSeuilAlerte() < 0) {
            System.err.println("Erreur : Seuil alerte ne peut pas être négatif !");
            return false;
        }
        
        return produitDao.update(produit);
    }
    
    @Override
    public boolean supprimerProduit(int id) {
        // Règle : Vérifier si le produit est utilisé dans des commandes
        // (À implémenter plus tard avec CommandeDAO)
        
        return produitDao.delete(id);
    }
    
    @Override
    public Produit getProduitById(int id) {
        return produitDao.read(id);
    }
    
    @Override
    public List<Produit> getAllProduits() {
        return produitDao.findAll();
    }
    
    @Override
    public List<Produit> getProduitsByCategorie(int categorieId) {
        return produitDao.findByCategorie(categorieId);
    }
    
    @Override
    public List<Produit> getProduitsStockFaible() {
        return produitDao.findStockBelowSeuil();
    }
    
    @Override
    public boolean mettreAJourStock(int produitId, int quantite) {
        // Règle : Ne pas permettre de descendre en dessous de 0
        Produit produit = produitDao.read(produitId);
        if (produit == null) {
            System.err.println("Erreur : Produit #" + produitId + " n'existe pas !");
            return false;
        }
        
        int nouveauStock = produit.getStockActuel() + quantite;
        if (nouveauStock < 0) {
            System.err.println("Erreur : Stock insuffisant ! Stock actuel: " + produit.getStockActuel());
            return false;
        }
        
        return produitDao.updateStock(produitId, quantite);
    }
    
    @Override
    public boolean verifierStockDisponible(int produitId, int quantiteDemandee) {
        Produit produit = produitDao.read(produitId);
        if (produit == null) {
            return false;
        }
        
        return produit.getStockActuel() >= quantiteDemandee;
    }
}