/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.service;

import com.gesrestaurant.dao.CommandeDAO;
import com.gesrestaurant.dao.LigneCommandeDAO;
import com.gesrestaurant.dao.ProduitDAO;
import com.gesrestaurant.dao.MvtStockDAO;
import com.gesrestaurant.model.Commande;
import com.gesrestaurant.model.LigneCommande;
import com.gesrestaurant.model.Produit;
import com.gesrestaurant.model.MouvementStock;
import com.gesrestaurant.util.DatabaseConnection;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

public class CommandeServiceImpl implements ICommandeService {
    
    private CommandeDAO commandeDao;
    private LigneCommandeDAO ligneCommandeDao;
    private ProduitDAO produitDao;
    private MvtStockDAO mouvementDao;
    private IProduitService produitService;
    
    public CommandeServiceImpl() {
        Connection conn = DatabaseConnection.getConnection();
        this.ligneCommandeDao = new LigneCommandeDAO(conn);
        this.commandeDao = new CommandeDAO(conn, ligneCommandeDao);
        this.produitDao = new ProduitDAO(conn, null); // CategorieDAO pas nécessaire ici
        this.mouvementDao = new MvtStockDAO(conn);
        this.produitService = new ProduitServiceImpl();
    }
    
    @Override
    public Commande creerCommande() {
        Commande nouvelleCommande = new Commande();
        nouvelleCommande.setDateCommande(new Date());
        nouvelleCommande.setEtat("EN_COURS");
        nouvelleCommande.setTotal(0.0);
        
        if (commandeDao.create(nouvelleCommande)) {
            return nouvelleCommande;
        }
        return null;
    }
    
    @Override
    public boolean ajouterLigneCommande(int commandeId, Produit produit, int quantite) {
        // Règle 1 : Vérifier que la commande existe et est en cours
        Commande commande = commandeDao.read(commandeId);
        if (commande == null || !"EN_COURS".equals(commande.getEtat())) {
            System.err.println("Erreur : Commande invalide ou déjà validée/annulée !");
            return false;
        }
        
        // Règle 2 : Vérifier stock disponible
        if (!produitService.verifierStockDisponible(produit.getId(), quantite)) {
            System.err.println("Erreur : Stock insuffisant pour " + produit.getNom() + 
                             " (demandé: " + quantite + ", disponible: " + produit.getStockActuel() + ")");
            return false;
        }
        
        // Règle 3 : Quantité > 0
        if (quantite <= 0) {
            System.err.println("Erreur : Quantité doit être > 0 !");
            return false;
        }
        
        // Créer la ligne de commande
        LigneCommande ligne = new LigneCommande(
            commande,
            produit,
            quantite,
            produit.getPrixVente()
        );
        
        boolean success = ligneCommandeDao.create(ligne);
        
        // Mettre à jour le total de la commande
        if (success) {
            double nouveauTotal = commandeDao.calculerTotalCommande(commandeId);
            commande.setTotal(nouveauTotal);
            commandeDao.update(commande);
        }
        
        return success;
    }
    
    @Override
    public boolean modifierQuantiteLigne(int ligneId, int nouvelleQuantite) {
        // Règle 1 : Quantité > 0
        if (nouvelleQuantite <= 0) {
            System.err.println("Erreur : Quantité doit être > 0 !");
            return false;
        }
        
        LigneCommande ligne = ligneCommandeDao.read(ligneId);
        if (ligne == null) {
            System.err.println("Erreur : Ligne de commande non trouvée !");
            return false;
        }
        
        // Règle 2 : Vérifier stock si augmentation
        int difference = nouvelleQuantite - ligne.getQuantite();
        if (difference > 0) {
            if (!produitService.verifierStockDisponible(ligne.getProduit().getId(), difference)) {
                System.err.println("Erreur : Stock insuffisant !");
                return false;
            }
        }
        
        ligne.setQuantite(nouvelleQuantite);
        boolean success = ligneCommandeDao.update(ligne);
        
        // Mettre à jour le total de la commande
        if (success) {
            Commande commande = ligne.getCommande();
            double nouveauTotal = commandeDao.calculerTotalCommande(commande.getId());
            commande.setTotal(nouveauTotal);
            commandeDao.update(commande);
        }
        
        return success;
    }
    
    @Override
    public boolean supprimerLigneCommande(int ligneId) {
        LigneCommande ligne = ligneCommandeDao.read(ligneId);
        if (ligne == null) {
            System.err.println("Erreur : Ligne de commande non trouvée !");
            return false;
        }
        
        boolean success = ligneCommandeDao.delete(ligneId);
        
        // Mettre à jour le total de la commande
        if (success) {
            Commande commande = ligne.getCommande();
            double nouveauTotal = commandeDao.calculerTotalCommande(commande.getId());
            commande.setTotal(nouveauTotal);
            commandeDao.update(commande);
        }
        
        return success;
    }
    
    @Override
    public boolean validerCommande(int commandeId) {
        // Règle 1 : Vérifier que la commande a au moins une ligne
        List<LigneCommande> lignes = getLignesCommande(commandeId);
        if (lignes == null || lignes.isEmpty()) {
            System.err.println("Erreur : Commande vide !");
            return false;
        }
        
        // Règle 2 : Vérifier stock pour toutes les lignes
        for (LigneCommande ligne : lignes) {
            if (!produitService.verifierStockDisponible(
                ligne.getProduit().getId(), 
                ligne.getQuantite())) {
                System.err.println("Erreur : Stock insuffisant pour " + ligne.getProduit().getNom());
                return false;
            }
        }
        
        // Règle 3 : Déduire le stock pour chaque produit
        for (LigneCommande ligne : lignes) {
            Produit produit = ligne.getProduit();
            int quantite = ligne.getQuantite();
            
            // Mettre à jour le stock
            produitService.mettreAJourStock(produit.getId(), -quantite);
            
            // Enregistrer le mouvement de stock
            MouvementStock mouvement = new MouvementStock(
                produit,
                "SORTIE",
                quantite,
                "Vente - Commande #" + commandeId
            );
            mouvementDao.create(mouvement);
        }
        
        // Valider la commande
        return commandeDao.validerCommande(commandeId);
    }
    
    @Override
    public boolean annulerCommande(int commandeId) {
        Commande commande = commandeDao.read(commandeId);
        if (commande == null) {
            System.err.println("Erreur : Commande non trouvée !");
            return false;
        }
        
        // Si la commande était validée, remettre le stock
        if ("VALIDEE".equals(commande.getEtat())) {
            List<LigneCommande> lignes = getLignesCommande(commandeId);
            for (LigneCommande ligne : lignes) {
                Produit produit = ligne.getProduit();
                int quantite = ligne.getQuantite();
                
                // Réapprovisionner le stock
                produitService.mettreAJourStock(produit.getId(), quantite);
                
                // Enregistrer le mouvement de stock
                MouvementStock mouvement = new MouvementStock(
                    produit,
                    "ENTREE",
                    quantite,
                    "Annulation commande #" + commandeId
                );
                mouvementDao.create(mouvement);
            }
        }
        
        return commandeDao.annulerCommande(commandeId);
    }
    
    @Override
    public Commande getCommandeById(int id) {
        return commandeDao.read(id);
    }
    
    @Override
    public List<Commande> getCommandesEnCours() {
        return commandeDao.findByEtat("EN_COURS");
    }
    
    @Override
    public List<Commande> getCommandesValidees() {
        return commandeDao.findByEtat("VALIDEE");
    }
    
    @Override
    public List<Commande> getCommandesByDate(Date date) {
        return commandeDao.findByDate(new java.sql.Date(date.getTime()));
    }
    
    @Override
    public List<LigneCommande> getLignesCommande(int commandeId) {
        return ligneCommandeDao.findByCommandeId(commandeId);
    }
    
    @Override
    public double calculerTotalCommande(int commandeId) {
        return commandeDao.calculerTotalCommande(commandeId);
    }
    
    @Override
    public boolean verifierStockPourCommande(int produitId, int quantite) {
        return produitService.verifierStockDisponible(produitId, quantite);
    }
    
    @Override
    public boolean commanderProduit(int commandeId, int produitId, int quantite) {
        Produit produit = produitDao.read(produitId);
        if (produit == null) {
            System.err.println("Erreur : Produit non trouvé !");
            return false;
        }
        
        return ajouterLigneCommande(commandeId, produit, quantite);
    }
}