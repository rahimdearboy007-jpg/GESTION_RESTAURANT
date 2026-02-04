/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.service;

import com.gesrestaurant.model.Commande;
import com.gesrestaurant.model.LigneCommande;
import com.gesrestaurant.model.Produit;
import java.util.Date;
import java.util.List;

public interface ICommandeService {
    // CRUD Commandes
    Commande creerCommande();
    boolean ajouterLigneCommande(int commandeId, Produit produit, int quantite);
    boolean modifierQuantiteLigne(int ligneId, int nouvelleQuantite);
    boolean supprimerLigneCommande(int ligneId);
    boolean validerCommande(int commandeId);
    boolean annulerCommande(int commandeId);
    Commande getCommandeById(int id);
    List<Commande> getCommandesEnCours();
    List<Commande> getCommandesValidees();
    List<Commande> getCommandesByDate(Date date);
    
    // Gestion lignes
    List<LigneCommande> getLignesCommande(int commandeId);
    double calculerTotalCommande(int commandeId);
    
    // Règles métier
    boolean verifierStockPourCommande(int produitId, int quantite);
    boolean commanderProduit(int commandeId, int produitId, int quantite);
}