/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.service;

import com.gesrestaurant.model.Produit;
import java.util.List;

public interface IProduitService {
    // CRUD
    boolean ajouterProduit(Produit produit);
    boolean modifierProduit(Produit produit);
    boolean supprimerProduit(int id);
    Produit getProduitById(int id);
    List<Produit> getAllProduits();
    
    // Règles métier spécifiques
    List<Produit> getProduitsByCategorie(int categorieId);
    List<Produit> getProduitsStockFaible();
    boolean mettreAJourStock(int produitId, int quantite);
    boolean verifierStockDisponible(int produitId, int quantiteDemandee);
}
