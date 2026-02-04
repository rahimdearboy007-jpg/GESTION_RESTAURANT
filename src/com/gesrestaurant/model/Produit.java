/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.model;

/**
 *
 * @author rahim
 */
public class Produit {
    private int id;
    private String nom;
    private Categorie categorie;
    private double prixVente;
    private int stockActuel;
    private int seuilAlerte;
    
    // Constructeurs
    public Produit() {
    }
    
    public Produit(int id, String nom, Categorie categorie, double prixVente, int stockActuel, int seuilAlerte) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.prixVente = prixVente;
        this.stockActuel = stockActuel;
        this.seuilAlerte = seuilAlerte;
    }
    
    public Produit(String nom, Categorie categorie, double prixVente, int stockActuel, int seuilAlerte) {
        this.nom = nom;
        this.categorie = categorie;
        this.prixVente = prixVente;
        this.stockActuel = stockActuel;
        this.seuilAlerte = seuilAlerte;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public Categorie getCategorie() {
        return categorie;
    }
    
    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }
    
    public double getPrixVente() {
        return prixVente;
    }
    
    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }
    
    public int getStockActuel() {
        return stockActuel;
    }
    
    public void setStockActuel(int stockActuel) {
        this.stockActuel = stockActuel;
    }
    
    public int getSeuilAlerte() {
        return seuilAlerte;
    }
    
    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }
    
    // toString
    @Override
    public String toString() {
        return nom + " (" + categorie.getLibelle() + ") - " + prixVente + "€";
    }
}
    

