/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.service;

import com.gesrestaurant.model.Categorie;
import com.gesrestaurant.model.Produit;
import com.gesrestaurant.model.Commande;
import com.gesrestaurant.model.LigneCommande;
import com.gesrestaurant.model.MouvementStock;
import com.gesrestaurant.model.Utilisateur;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

public class TestAllService {
    public static void main(String[] args) {
        System.out.println("=== TEST COMPLET DE TOUS LES SERVICES ===\n");
        
        // Initialiser tous les services
        ICategorieService categorieService = new CategorieServiceImpl();
        IProduitService produitService = new ProduitServiceImpl();
        IUtilisateurService userService = new UtilisateurServiceImpl();
        ICommandeService commandeService = new CommandeServiceImpl();
        IMouvementStockService mouvementService = new MouvementStockServiceImpl();
        IStatistiqueService statsService = new StatistiqueServiceImpl();
        
        // ====================
        // 1. TEST CATÉGORIES
        // ====================
        System.out.println("1. TEST CATÉGORIE SERVICE");
        System.out.println("--------------------------");
        
        // Lister catégories existantes
        List<Categorie> categories = categorieService.getAllCategories();
        System.out.println("Catégories existantes (" + categories.size() + ") :");
        for (Categorie cat : categories) {
            System.out.println("  - " + cat.getLibelle() + " (ID: " + cat.getId() + ")");
        }
        
        // Vérifier si une catégorie existe
        String testCategorie = "Boissons";
        boolean existe = categorieService.categorieExiste(testCategorie);
        System.out.println("\nCatégorie '" + testCategorie + "' existe : " + existe);
        
        // ====================
        // 2. TEST PRODUITS
        // ====================
        System.out.println("\n\n2. TEST PRODUIT SERVICE");
        System.out.println("----------------------");
        
        // Trouver une catégorie pour tester
        Categorie categorieTest = null;
        for (Categorie cat : categories) {
            if (cat.getLibelle().equalsIgnoreCase("Boissons")) {
                categorieTest = cat;
                break;
            }
        }
        
        if (categorieTest != null) {
            // Lister produits par catégorie
            List<Produit> produitsCategorie = produitService.getProduitsByCategorie(categorieTest.getId());
            System.out.println("Produits dans '" + categorieTest.getLibelle() + "' (" + produitsCategorie.size() + ") :");
            for (Produit p : produitsCategorie) {
                System.out.println("  - " + p.getNom() + " (Stock: " + p.getStockActuel() + ")");
            }
            
            // Vérifier stock faible
            List<Produit> stockFaible = produitService.getProduitsStockFaible();
            System.out.println("\nProduits avec stock faible : " + stockFaible.size());
        }
        
        // ====================
        // 3. TEST MOUVEMENTS STOCK
        // ====================
        System.out.println("\n\n3. TEST MOUVEMENT STOCK SERVICE");
        System.out.println("------------------------------");
        
        // Prendre un produit pour tester
        List<Produit> tousProduits = produitService.getAllProduits();
        if (!tousProduits.isEmpty()) {
            Produit produitTest = tousProduits.get(0);
            System.out.println("Test avec produit : " + produitTest.getNom() + " (ID: " + produitTest.getId() + ")");
            
            // Stock actuel
            int stockInitial = mouvementService.getStockActuel(produitTest.getId());
            System.out.println("Stock initial : " + stockInitial);
            
            // Tester une entrée de stock
            System.out.println("\nTest entrée de stock (+10 unités)...");
            boolean entreeOK = mouvementService.enregistrerEntreeStock(
                produitTest.getId(), 
                10, 
                "Test entrée"
            );
            System.out.println("Entrée stock réussie : " + entreeOK);
            
            if (entreeOK) {
                int stockApresEntree = mouvementService.getStockActuel(produitTest.getId());
                System.out.println("Stock après entrée : " + stockApresEntree);
                
                // Tester une sortie de stock
                System.out.println("\nTest sortie de stock (-5 unités)...");
                boolean sortieOK = mouvementService.enregistrerSortieStock(
                    produitTest.getId(),
                    5,
                    "Test sortie"
                );
                System.out.println("Sortie stock réussie : " + sortieOK);
                
                if (sortieOK) {
                    int stockFinal = mouvementService.getStockActuel(produitTest.getId());
                    System.out.println("Stock final : " + stockFinal);
                    
                    // Vérifier calcul : initial + 10 - 5 = final
                    if (stockFinal == (stockInitial + 10 - 5)) {
                        System.out.println("✅ Calcul stock correct !");
                    } else {
                        System.out.println("❌ Erreur calcul stock !");
                    }
                }
            }
            
            // Lister les mouvements du produit
            List<MouvementStock> mouvements = mouvementService.getMouvementsByProduit(produitTest.getId());
            System.out.println("\nMouvements pour ce produit : " + mouvements.size());
            for (MouvementStock mvt : mouvements) {
                System.out.println("  - " + mvt.getType() + " " + mvt.getQuantite() + " (" + mvt.getMotif() + ")");
            }
        }
        
        // ====================
        // 4. TEST COMMANDES
        // ====================
        System.out.println("\n\n4. TEST COMMANDE SERVICE");
        System.out.println("------------------------");
        
        // Créer une nouvelle commande
        System.out.println("Création d'une nouvelle commande...");
        Commande nouvelleCommande = commandeService.creerCommande();
        
        if (nouvelleCommande != null) {
            System.out.println("Commande créée : ID #" + nouvelleCommande.getId());
            System.out.println("État : " + nouvelleCommande.getEtat());
            System.out.println("Total initial : " + nouvelleCommande.getTotal() + "€");
            
            // Ajouter des produits à la commande (si on a des produits)
            if (!tousProduits.isEmpty()) {
                Produit produitCommande = tousProduits.get(0);
                
                // Vérifier stock d'abord
                boolean stockDispo = commandeService.verifierStockPourCommande(
                    produitCommande.getId(), 
                    2
                );
                System.out.println("\nStock disponible pour " + produitCommande.getNom() + " (2 unités) : " + stockDispo);
                
                if (stockDispo) {
                    // Ajouter au panier
                    System.out.println("Ajout de 2 " + produitCommande.getNom() + " à la commande...");
                    boolean ajoutOK = commandeService.ajouterLigneCommande(
                        nouvelleCommande.getId(),
                        produitCommande,
                        2
                    );
                    System.out.println("Produit ajouté : " + ajoutOK);
                    
                    // Calculer le total
                    double total = commandeService.calculerTotalCommande(nouvelleCommande.getId());
                    System.out.println("Total commande : " + total + "€");
                    
                    // Récupérer les lignes de commande
                    List<LigneCommande> lignes = commandeService.getLignesCommande(nouvelleCommande.getId());
                    System.out.println("Lignes commande : " + lignes.size());
                    
                    // NE PAS VALIDER pour ne pas affecter le stock réel
                    System.out.println("\n⚠️  Commande non validée (pour ne pas affecter le stock réel)");
                    System.out.println("Pour valider : commandeService.validerCommande(" + nouvelleCommande.getId() + ")");
                }
            }
            
            // Lister les commandes en cours
            List<Commande> commandesEnCours = commandeService.getCommandesEnCours();
            System.out.println("\nCommandes en cours : " + commandesEnCours.size());
        }
        
        // ====================
        // 5. TEST STATISTIQUES
        // ====================
        System.out.println("\n\n5. TEST STATISTIQUE SERVICE");
        System.out.println("---------------------------");
        
        Date aujourdhui = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(aujourdhui);
        cal.add(Calendar.DATE, -7);
        Date ilYA7Jours = cal.getTime();
        
        // Chiffre d'affaires
        double caJour = statsService.getChiffreAffaires(aujourdhui);
        double caSemaine = statsService.getChiffreAffairesPeriode(ilYA7Jours, aujourdhui);
        
        System.out.println("Chiffre d'affaires aujourd'hui : " + caJour + "€");
        System.out.println("Chiffre d'affaires 7 derniers jours : " + caSemaine + "€");
        
        // Top produits
        Map<String, Integer> topProduits = statsService.getTopProduitsVendus(ilYA7Jours, aujourdhui, 3);
        System.out.println("\nTop 3 produits (7 derniers jours) :");
        for (Map.Entry<String, Integer> entry : topProduits.entrySet()) {
            System.out.println("  - " + entry.getKey() + " : " + entry.getValue() + " unités");
        }
        
        // Informations stock
        int stockFaible = statsService.getNombreProduitsStockFaible();
        int rupture = statsService.getNombreProduitsRupture();
        System.out.println("\nAlertes stock :");
        System.out.println("  - Produits stock faible : " + stockFaible);
        System.out.println("  - Produits en rupture : " + rupture);
        
        // Commandes en cours
        int cmdEnCours = statsService.getNombreCommandesEnCours();
        System.out.println("Commandes en cours : " + cmdEnCours);
        
        // ====================
        // 6. TEST UTILISATEUR
        // ====================
        System.out.println("\n\n6. TEST UTILISATEUR SERVICE");
        System.out.println("---------------------------");
        
        // Vérifier si un login est disponible
        String testLogin = "testuser";
        boolean loginDispo = userService.loginDisponible(testLogin);
        System.out.println("Login '" + testLogin + "' disponible : " + loginDispo);
        
        // Créer un utilisateur test (à décommenter si besoin)
        /*
        if (loginDispo) {
            Utilisateur nouvelUser = new Utilisateur(testLogin, "test123");
            boolean userCree = userService.creerUtilisateur(nouvelUser);
            System.out.println("Utilisateur créé : " + userCree);
            
            // Tester l'authentification
            Utilisateur authUser = userService.authentifier(testLogin, "test123");
            System.out.println("Authentification réussie : " + (authUser != null));
        }
        */
        
        // ====================
        // RÉSUMÉ
        // ====================
        System.out.println("\n\n=== RÉSUMÉ DU TEST ===");
        System.out.println("Services testés : 6/6");
        System.out.println("Catégories : " + categories.size());
        System.out.println("Produits : " + tousProduits.size());
        // Déclarer la variable en dehors du bloc if
        List<Commande> commandesEnCours = null;

// Plus loin dans le code, quand tu l'initialises :
        commandesEnCours = commandeService.getCommandesEnCours();
        System.out.println("\nCommandes en cours : " + (commandesEnCours != null ? commandesEnCours.size() : 0));

// Et à la fin, pour le résumé :
        System.out.println("Commandes en cours : " + (commandesEnCours != null ? commandesEnCours.size() : 0));
        
        System.out.println("Chiffre d'affaires aujourd'hui : " + caJour + "€");
        System.out.println("\n✅ TEST TERMINÉ AVEC SUCCÈS !");
        
        // Nettoyage (optionnel)
        System.out.println("\n⚠️  Note : La commande test créée n'a pas été validée");
        System.out.println("   Pour nettoyer : Supprimer manuellement la commande #" + 
                          (nouvelleCommande != null ? nouvelleCommande.getId() : "N/A"));
    }
}
