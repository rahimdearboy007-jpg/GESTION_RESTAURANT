/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.model;

import java.util.Date;

public class MouvementStock {
    private int id;
    private Produit produit;
    private String type; // "ENTREE" ou "SORTIE"
    private int quantite;
    private Date dateMouvement;
    private String motif;
    
    // Constructeurs
    public MouvementStock() {
        this.dateMouvement = new Date();
    }
    
    public MouvementStock(int id, Produit produit, String type, int quantite, Date dateMouvement, String motif) {
        this.id = id;
        this.produit = produit;
        this.type = type;
        this.quantite = quantite;
        this.dateMouvement = dateMouvement;
        this.motif = motif;
    }
    
    public MouvementStock(Produit produit, String type, int quantite, String motif) {
        this.produit = produit;
        this.type = type;
        this.quantite = quantite;
        this.dateMouvement = new Date();
        this.motif = motif;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getQuantite() {
        return quantite;
    }
    
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
    
    public Date getDateMouvement() {
        return dateMouvement;
    }
    
    public void setDateMouvement(Date dateMouvement) {
        this.dateMouvement = dateMouvement;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    // toString
    @Override
    public String toString() {
        return type + " de " + quantite + " " + produit.getNom() + " - " + dateMouvement;
    }
}
