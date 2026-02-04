/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.service;

import com.gesrestaurant.dao.MvtStockDAO;
import com.gesrestaurant.dao.ProduitDAO;
import com.gesrestaurant.model.MouvementStock;
import com.gesrestaurant.model.Produit;
import com.gesrestaurant.util.DatabaseConnection;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

public class MouvementStockServiceImpl implements IMouvementStockService {
    
    private MvtStockDAO mouvementDao;
    private ProduitDAO produitDao;
    private IProduitService produitService;
    
    public MouvementStockServiceImpl() {
        Connection conn = DatabaseConnection.getConnection();
        this.mouvementDao = new MvtStockDAO(conn);
        this.produitDao = new ProduitDAO(conn, null);
        this.produitService = new ProduitServiceImpl();
    }
    
    @Override
    public boolean enregistrerEntreeStock(int produitId, int quantite, String motif) {
        // Règle 1 : Quantité > 0
        if (quantite <= 0) {
            System.err.println("Erreur : Quantité doit être > 0 !");
            return false;
        }
        
        // Règle 2 : Produit existe
        Produit produit = produitDao.read(produitId);
        if (produit == null) {
            System.err.println("Erreur : Produit non trouvé !");
            return false;
        }
        
        // Règle 3 : Motif non vide
        if (motif == null || motif.trim().isEmpty()) {
            motif = "Entrée de stock";
        }
        
        // Créer le mouvement
        MouvementStock mouvement = new MouvementStock(
            produit,
            "ENTREE",
            quantite,
            motif
        );
        
        boolean mouvementSuccess = mouvementDao.create(mouvement);
        
        // Mettre à jour le stock du produit
        if (mouvementSuccess) {
            boolean stockSuccess = produitService.mettreAJourStock(produitId, quantite);
            if (!stockSuccess) {
                System.err.println("Avertissement : Mouvement enregistré mais échec mise à jour stock !");
            }
        }
        
        return mouvementSuccess;
    }
    
    @Override
    public boolean enregistrerSortieStock(int produitId, int quantite, String motif) {
        // Règle 1 : Quantité > 0
        if (quantite <= 0) {
            System.err.println("Erreur : Quantité doit être > 0 !");
            return false;
        }
        
        // Règle 2 : Produit existe
        Produit produit = produitDao.read(produitId);
        if (produit == null) {
            System.err.println("Erreur : Produit non trouvé !");
            return false;
        }
        
        // Règle 3 : Stock suffisant
        if (!verifierQuantiteSortie(produitId, quantite)) {
            System.err.println("Erreur : Stock insuffisant ! Stock actuel: " + produit.getStockActuel());
            return false;
        }
        
        // Règle 4 : Motif non vide
        if (motif == null || motif.trim().isEmpty()) {
            motif = "Sortie de stock";
        }
        
        // Créer le mouvement
        MouvementStock mouvement = new MouvementStock(
            produit,
            "SORTIE",
            quantite,
            motif
        );
        
        boolean mouvementSuccess = mouvementDao.create(mouvement);
        
        // Mettre à jour le stock du produit
        if (mouvementSuccess) {
            boolean stockSuccess = produitService.mettreAJourStock(produitId, -quantite);
            if (!stockSuccess) {
                System.err.println("Avertissement : Mouvement enregistré mais échec mise à jour stock !");
            }
        }
        
        return mouvementSuccess;
    }
    
    @Override
    public boolean supprimerMouvement(int mouvementId) {
        MouvementStock mouvement = mouvementDao.read(mouvementId);
        if (mouvement == null) {
            System.err.println("Erreur : Mouvement non trouvé !");
            return false;
        }
        
        // Règle : Annuler l'effet du mouvement sur le stock
        Produit produit = mouvement.getProduit();
        int quantite = mouvement.getQuantite();
        
        if ("ENTREE".equals(mouvement.getType())) {
            // Si c'était une entrée, retirer du stock
            produitService.mettreAJourStock(produit.getId(), -quantite);
        } else if ("SORTIE".equals(mouvement.getType())) {
            // Si c'était une sortie, remettre en stock
            produitService.mettreAJourStock(produit.getId(), quantite);
        }
        
        return mouvementDao.delete(mouvementId);
    }
    
    @Override
    public List<MouvementStock> getAllMouvements() {
        return mouvementDao.findAll();
    }
    
    @Override
    public List<MouvementStock> getMouvementsByProduit(int produitId) {
        return mouvementDao.findByProduit(produitId);
    }
    
    @Override
    public List<MouvementStock> getMouvementsByType(String type) {
        if (!"ENTREE".equals(type) && !"SORTIE".equals(type)) {
            System.err.println("Erreur : Type invalide (doit être 'ENTREE' ou 'SORTIE')");
            return null;
        }
        return mouvementDao.findByType(type);
    }
    
    @Override
    public List<MouvementStock> getMouvementsByDateRange(Date dateDebut, Date dateFin) {
        if (dateDebut == null || dateFin == null) {
            System.err.println("Erreur : Dates invalides !");
            return null;
        }
        
        if (dateDebut.after(dateFin)) {
            System.err.println("Erreur : Date début après date fin !");
            return null;
        }
        
        return mouvementDao.findByDateRange(
            new java.sql.Date(dateDebut.getTime()),
            new java.sql.Date(dateFin.getTime())
        );
    }
    
    @Override
    public boolean verifierQuantiteSortie(int produitId, int quantite) {
        Produit produit = produitDao.read(produitId);
        if (produit == null) {
            return false;
        }
        
        return produit.getStockActuel() >= quantite;
    }
    
    @Override
    public int getStockActuel(int produitId) {
        Produit produit = produitDao.read(produitId);
        if (produit == null) {
            return -1; // Indique une erreur
        }
        
        return produit.getStockActuel();
    }
}
