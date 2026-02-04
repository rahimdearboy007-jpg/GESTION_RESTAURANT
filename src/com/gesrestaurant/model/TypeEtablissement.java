/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.model;

public class TypeEtablissement {
    private int id;
    private String code;
    private String nom;
    private String description;
    
    // Constructeurs
    public TypeEtablissement() {}
    
    public TypeEtablissement(int id, String code, String nom, String description) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.description = description;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return nom + " (" + code + ")";
    }
}
