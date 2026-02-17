/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.model;

public class LigneCommande {
    private int id;
    private Commande commande;
    private Produit produit;
    private int quantite;
    private double prixUnitaire;
    private double montantLigne;
    
    // Constructeurs
    public LigneCommande() {
    }
    
    public LigneCommande(int id, Commande commande, Produit produit, int quantite, double prixUnitaire) {
        this.id = id;
        this.commande = commande;
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.montantLigne = quantite * prixUnitaire;
    }
    
    public LigneCommande(Commande commande, Produit produit, int quantite, double prixUnitaire) {
        this.commande = commande;
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.montantLigne = quantite * prixUnitaire;
    }
    
    // Ajoute cette méthode pour setCommandeId
    public void setCommandeId(int commandeId) {
        if (this.commande == null) {
            this.commande = new Commande();
        }
        this.commande.setId(commandeId);
    }

    // Ajoute cette méthode si tu veux pouvoir setter manuellement (optionnel)
    public void setMontantLigne(double montantLigne) {
        this.montantLigne = montantLigne;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Commande getCommande() {
        return commande;
    }
    
    public void setCommande(Commande commande) {
        this.commande = commande;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public int getQuantite() {
        return quantite;
    }
    
    public void setQuantite(int quantite) {
        this.quantite = quantite;
        this.montantLigne = this.quantite * this.prixUnitaire;
    }
    
    public double getPrixUnitaire() {
        return prixUnitaire;
    }
    
    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        this.montantLigne = this.quantite * this.prixUnitaire;
    }
    
    public double getMontantLigne() {
        return montantLigne;
    }
    
    // Pas de setter pour montantLigne - calculé automatiquement
    
    // toString
    @Override
    public String toString() {
        return quantite + " x " + produit.getNom() + " (" + prixUnitaire + "€) = " + montantLigne + "€";
    }
}