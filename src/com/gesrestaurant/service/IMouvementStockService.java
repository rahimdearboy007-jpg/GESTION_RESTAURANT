/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.service;

import com.gesrestaurant.model.MouvementStock;
import java.util.Date;
import java.util.List;

public interface IMouvementStockService {
    // Gestion mouvements
    boolean enregistrerEntreeStock(int produitId, int quantite, String motif);
    boolean enregistrerSortieStock(int produitId, int quantite, String motif);
    boolean supprimerMouvement(int mouvementId);
    
    // Consultation
    List<MouvementStock> getAllMouvements();
    List<MouvementStock> getMouvementsByProduit(int produitId);
    List<MouvementStock> getMouvementsByType(String type);
    List<MouvementStock> getMouvementsByDateRange(Date dateDebut, Date dateFin);
    
    // Règles métier
    boolean verifierQuantiteSortie(int produitId, int quantite);
    int getStockActuel(int produitId);
}