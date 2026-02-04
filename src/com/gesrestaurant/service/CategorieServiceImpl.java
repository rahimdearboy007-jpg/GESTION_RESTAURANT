/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.service;

import com.gesrestaurant.dao.CategorieDAO;
import com.gesrestaurant.dao.ProduitDAO;
import com.gesrestaurant.model.Categorie;
import com.gesrestaurant.util.DatabaseConnection;
import java.sql.Connection;
import java.util.List;

public class CategorieServiceImpl implements ICategorieService {
    
    private CategorieDAO categorieDao;
    private ProduitDAO produitDao;
    
    public CategorieServiceImpl() {
        Connection conn = DatabaseConnection.getConnection();
        this.categorieDao = new CategorieDAO(conn);
        this.produitDao = new ProduitDAO(conn, categorieDao);
    }
    
    @Override
    public boolean ajouterCategorie(Categorie categorie) {
        // Règle 1 : Vérifier si le libellé n'existe pas déjà
        if (categorieExiste(categorie.getLibelle())) {
            System.err.println("Erreur : Catégorie '" + categorie.getLibelle() + "' existe déjà !");
            return false;
        }
        
        // Règle 2 : Libellé non vide
        if (categorie.getLibelle() == null || categorie.getLibelle().trim().isEmpty()) {
            System.err.println("Erreur : Libellé de catégorie vide !");
            return false;
        }
        
        return categorieDao.create(categorie);
    }
    
    @Override
    public boolean modifierCategorie(Categorie categorie) {
        // Règle 1 : Vérifier si la catégorie existe
        Categorie existante = categorieDao.read(categorie.getId());
        if (existante == null) {
            System.err.println("Erreur : Catégorie #" + categorie.getId() + " n'existe pas !");
            return false;
        }
        
        // Règle 2 : Si le libellé change, vérifier qu'il n'existe pas déjà
        if (!existante.getLibelle().equalsIgnoreCase(categorie.getLibelle())) {
            if (categorieExiste(categorie.getLibelle())) {
                System.err.println("Erreur : Catégorie '" + categorie.getLibelle() + "' existe déjà !");
                return false;
            }
        }
        
        return categorieDao.update(categorie);
    }
    
    @Override
    public boolean supprimerCategorie(int id) {
        // Règle : Ne pas supprimer si des produits utilisent cette catégorie
        if (categorieUtilisee(id)) {
            System.err.println("Erreur : Catégorie utilisée par des produits !");
            return false;
        }
        
        return categorieDao.delete(id);
    }
    
    @Override
    public Categorie getCategorieById(int id) {
        return categorieDao.read(id);
    }
    
    @Override
    public List<Categorie> getAllCategories() {
        return categorieDao.findAll();
    }
    
    @Override
    public boolean categorieExiste(String libelle) {
        return categorieDao.findByLibelle(libelle) != null;
    }
    
    @Override
    public boolean categorieUtilisee(int categorieId) {
        // Vérifier si des produits utilisent cette catégorie
        List<com.gesrestaurant.model.Produit> produits = produitDao.findByCategorie(categorieId);
        return produits != null && !produits.isEmpty();
    }
}
