/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.service;

import java.util.Date;
import java.util.Map;

public interface IStatistiqueService {
    // Chiffre d'affaires
    double getChiffreAffaires(Date date);
    double getChiffreAffairesPeriode(Date dateDebut, Date dateFin);
    
    // Ventes
    Map<String, Integer> getTopProduitsVendus(Date dateDebut, Date dateFin, int limit);
    Map<String, Double> getProduitsPlusRentables(Date dateDebut, Date dateFin, int limit);
    
    // Stock
    int getNombreProduitsStockFaible();
    int getNombreProduitsRupture();
    
    // Commandes
    int getNombreCommandesEnCours();
    int getNombreCommandesValidees(Date date);
    double getValeurMoyenneCommande(Date dateDebut, Date dateFin);
}
