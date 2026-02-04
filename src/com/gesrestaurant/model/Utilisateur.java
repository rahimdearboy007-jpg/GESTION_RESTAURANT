/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.model;

public class Utilisateur {
    private int id;
    private String login;
    private String motDePasse;
    
    // Constructeurs
    public Utilisateur() {
    }
    
    public Utilisateur(int id, String login, String motDePasse) {
        this.id = id;
        this.login = login;
        this.motDePasse = motDePasse;
    }
    
    public Utilisateur(String login, String motDePasse) {
        this.login = login;
        this.motDePasse = motDePasse;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getLogin() {
        return login;
    }
    
    public void setLogin(String login) {
        this.login = login;
    }
    
    public String getMotDePasse() {
        return motDePasse;
    }
    
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
    
    // toString
    @Override
    public String toString() {
        return login;
    }
}