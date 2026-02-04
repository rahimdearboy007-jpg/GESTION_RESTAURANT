/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.model;

import java.util.Date;

public class Commande {
    private int id;
    private Date dateCommande;
    private String etat; // "EN_COURS", "VALIDEE", "ANNULEE"
    private double total;
    
    // Constructeurs
    public Commande() {
        this.dateCommande = new Date();
        this.etat = "EN_COURS";
        this.total = 0.0;
    }
    
    public Commande(int id, Date dateCommande, String etat, double total) {
        this.id = id;
        this.dateCommande = dateCommande;
        this.etat = etat;
        this.total = total;
    }
    
    public Commande(Date dateCommande) {
        this.dateCommande = dateCommande;
        this.etat = "EN_COURS";
        this.total = 0.0;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Date getDateCommande() {
        return dateCommande;
    }
    
    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }
    
    public String getEtat() {
        return etat;
    }
    
    public void setEtat(String etat) {
        this.etat = etat;
    }
    
    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    // toString
    @Override
    public String toString() {
        return "Commande #" + id + " - " + dateCommande + " - " + etat + " - " + total + "FCFA";
    }
}