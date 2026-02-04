/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.service;

import com.gesrestaurant.model.Categorie;
import java.util.List;

public interface ICategorieService {
    // CRUD
    boolean ajouterCategorie(Categorie categorie);
    boolean modifierCategorie(Categorie categorie);
    boolean supprimerCategorie(int id);
    Categorie getCategorieById(int id);
    List<Categorie> getAllCategories();
    
    // Règles métier spécifiques
    boolean categorieExiste(String libelle);
    boolean categorieUtilisee(int categorieId);
}