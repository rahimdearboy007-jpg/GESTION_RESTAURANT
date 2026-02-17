/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.gesrestaurant.views;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import com.gesrestaurant.model.*;
import com.gesrestaurant.dao.*;
import com.gesrestaurant.util.DatabaseConnection;
import com.gesrestaurant.util.Session;
import java.util.List;


/**
 *
 * @author rahim
 */
public class GestionCommandesFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GestionCommandesFrame.class.getName());
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BG_PRIMARY = new Color(250, 245, 240);  // Beige crème
    private static final Color BG_CARD = Color.WHITE;
    private static final Color BORDER_LIGHT = new Color(225, 225, 220);
    
    // ===== DAO =====
    private ProduitDAO produitDAO;
    private CommandeDAO commandeDAO;
    private LigneCommandeDAO ligneCommandeDAO;
    private Connection connection;
    
    // ===== DONNÉES =====
    private List<Produit> produitsDisponibles;
    private Map<Integer, Integer> stockTemporel; // Stock pendant la commande
    private Commande commandeEnCours;
    private Utilisateur utilisateurConnecte;
    
    // ===== COMPOSANTS UI - Onglet 1 : Prise de commande =====
    private JComboBox<Produit> comboProduits;
    private JSpinner spinQuantite;
    private JTable tablePanier;
    private DefaultTableModel tableModelPanier;
    private JLabel lblPrix, lblStock, lblTotal, lblEtat, lblNumeroCommande;
    private JTextField txtTable;
    private JButton btnAjouter, btnSupprimerLigne, btnAnnulerCommande, btnValiderCommande, btnFacture;
    
    // ===== COMPOSANTS UI - Onglet 2 : Historique =====
    private JTable tableHistorique;
    private DefaultTableModel tableModelHistorique;
    private JComboBox<String> comboFiltreEtat;
    private JTextField txtRechercheDate;
    private JButton btnRechercher, btnVoirDetails, btnImprimerFacture;
    private JLabel lblTotalJour;
    
    // ===== UTILISATEUR CONNECTÉ =====
    private JLabel lblStatus;
    /**
     * Creates new form GestionCommandesFrame
     */
    public GestionCommandesFrame() {
        this.utilisateurConnecte = Session.getUtilisateur();
        
        initDAO();
        initComponentsCustom();
        setTitle("🛒 Gestion des Commandes Clients");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        chargerProduits();
        nouvelleCommande();
        chargerHistorique();
    }
    
    private void initDAO() {
        try {
            this.connection = DatabaseConnection.getConnection();
            CategorieDAO categorieDAO = new CategorieDAO(connection);
            
            this.produitDAO = new ProduitDAO(connection, categorieDAO);
            this.ligneCommandeDAO = new LigneCommandeDAO(connection);
            this.commandeDAO = new CommandeDAO(connection, ligneCommandeDAO);
            
            logger.info("✅ Connexion BDD établie pour module commandes");
        } catch (Exception e) {
            logger.severe("❌ Erreur connexion BDD: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Impossible de se connecter à la base de données",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initComponentsCustom() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_PRIMARY);
        
        // ===== HEADER =====
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // ===== ONGLETS =====
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(BG_CARD);
        tabbedPane.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
        
        // Onglet 1 : Prise de commande
        JPanel panelPriseCommande = createPriseCommandePanel();
        tabbedPane.addTab("🛒 Prise de commande", panelPriseCommande);
        
        // Onglet 2 : Historique des commandes
        JPanel panelHistorique = createHistoriquePanel();
        tabbedPane.addTab("📋 Historique des commandes", panelHistorique);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // ===== FOOTER =====
        add(createFooterPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("🛒 GESTION DES COMMANDES CLIENTS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        
        JButton btnRetour = new JButton("🔙 Retour au Dashboard");
        btnRetour.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRetour.setBackground(new Color(255, 255, 255, 30));
        btnRetour.setForeground(Color.WHITE);
        btnRetour.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRetour.setFocusPainted(false);
        btnRetour.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRetour.addActionListener(e -> dispose());
        
        header.add(title, BorderLayout.WEST);
        header.add(btnRetour, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createPriseCommandePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ===== PANEL HAUT : Informations table et sélection produit =====
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(BG_CARD);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Ligne 1 : Numéro de table
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        topPanel.add(new JLabel("Table n°:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        txtTable = new JTextField();
        txtTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTable.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        topPanel.add(txtTable, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0.5;
        topPanel.add(new JLabel("(Laissez vide pour emporter)"), gbc);
        
        // Ligne 2 : Produit
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.2;
        topPanel.add(new JLabel("Produit:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        comboProduits = new JComboBox<>();
        comboProduits.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboProduits.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        comboProduits.addActionListener(e -> afficherInfosProduit());
        topPanel.add(comboProduits, gbc);
        
        // Ligne 3 : Prix et stock
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.2;
        topPanel.add(new JLabel("Prix:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        lblPrix = new JLabel("-");
        lblPrix.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPrix.setForeground(SUCCESS_COLOR);
        topPanel.add(lblPrix, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0.2;
        topPanel.add(new JLabel("Stock:"), gbc);
        
        gbc.gridx = 3;
        gbc.weightx = 0.3;
        lblStock = new JLabel("-");
        lblStock.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStock.setForeground(ACCENT_COLOR);
        topPanel.add(lblStock, gbc);
        
        // Ligne 4 : Quantité et bouton ajouter
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.2;
        topPanel.add(new JLabel("Quantité:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.2;
        spinQuantite = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spinQuantite.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinQuantite.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topPanel.add(spinQuantite, gbc);
        
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0.6;
        btnAjouter = new JButton("➕ Ajouter au panier");
        btnAjouter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAjouter.setBackground(SUCCESS_COLOR);
        btnAjouter.setForeground(Color.WHITE);
        btnAjouter.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAjouter.setFocusPainted(false);
        btnAjouter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAjouter.addActionListener(e -> ajouterAuPanier());
        topPanel.add(btnAjouter, gbc);
        
        // ===== PANIER =====
        JPanel panierPanel = new JPanel(new BorderLayout());
        panierPanel.setBackground(BG_CARD);
        panierPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel panierTitle = new JLabel("🛒 PANIER");
        panierTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panierTitle.setForeground(PRIMARY_COLOR);
        panierPanel.add(panierTitle, BorderLayout.NORTH);
        
        // Tableau du panier
        String[] colonnesPanier = {"#", "Produit", "Quantité", "Prix U. (F)", "Montant (F)"};
        tableModelPanier = new DefaultTableModel(colonnesPanier, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablePanier = new JTable(tableModelPanier);
        tablePanier.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablePanier.setRowHeight(35);
        tablePanier.setShowGrid(true);
        tablePanier.setGridColor(BORDER_LIGHT);
        tablePanier.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablePanier.getTableHeader().setBackground(new Color(245, 245, 245));
        tablePanier.getTableHeader().setForeground(PRIMARY_COLOR);
        
        JScrollPane scrollPanier = new JScrollPane(tablePanier);
        scrollPanier.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
        panierPanel.add(scrollPanier, BorderLayout.CENTER);
        
        // ===== BAS DU PANIER : Total et boutons =====
        JPanel bottomPanierPanel = new JPanel(new BorderLayout());
        bottomPanierPanel.setOpaque(false);
        bottomPanierPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setOpaque(false);
        
        JLabel totalLabel = new JLabel("TOTAL:");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(PRIMARY_COLOR);
        
        lblTotal = new JLabel("0 F");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(SUCCESS_COLOR);
        
        totalPanel.add(totalLabel);
        totalPanel.add(Box.createHorizontalStrut(10));
        totalPanel.add(lblTotal);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);
        
        btnSupprimerLigne = new JButton("🗑️ Supprimer ligne");
        btnSupprimerLigne.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSupprimerLigne.setBackground(DANGER_COLOR);
        btnSupprimerLigne.setForeground(Color.WHITE);
        btnSupprimerLigne.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnSupprimerLigne.setFocusPainted(false);
        btnSupprimerLigne.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSupprimerLigne.addActionListener(e -> supprimerLigne());
        
        btnAnnulerCommande = new JButton("❌ Annuler commande");
        btnAnnulerCommande.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAnnulerCommande.setBackground(new Color(149, 165, 166));
        btnAnnulerCommande.setForeground(Color.WHITE);
        btnAnnulerCommande.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnAnnulerCommande.setFocusPainted(false);
        btnAnnulerCommande.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnnulerCommande.addActionListener(e -> annulerCommande());
        
        btnValiderCommande = new JButton("✅ Valider commande");
        btnValiderCommande.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnValiderCommande.setBackground(SUCCESS_COLOR);
        btnValiderCommande.setForeground(Color.WHITE);
        btnValiderCommande.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnValiderCommande.setFocusPainted(false);
        btnValiderCommande.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnValiderCommande.addActionListener(e -> validerCommande());
        
        btnFacture = new JButton("🧾 Facture");
        btnFacture.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFacture.setBackground(ACCENT_COLOR);
        btnFacture.setForeground(Color.WHITE);
        btnFacture.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnFacture.setFocusPainted(false);
        btnFacture.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFacture.setEnabled(false);
        btnFacture.addActionListener(e -> imprimerFacture());
        
        actionPanel.add(btnSupprimerLigne);
        actionPanel.add(btnAnnulerCommande);
        actionPanel.add(btnValiderCommande);
        actionPanel.add(btnFacture);
        
        bottomPanierPanel.add(totalPanel, BorderLayout.EAST);
        bottomPanierPanel.add(actionPanel, BorderLayout.WEST);
        
        panierPanel.add(bottomPanierPanel, BorderLayout.SOUTH);
        
        // Assemblage
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(panierPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createHistoriquePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ===== FILTRES =====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(BG_CARD);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        filterPanel.add(new JLabel("Filtre par état:"));
        
        comboFiltreEtat = new JComboBox<>(new String[]{"Tous", "EN_COURS", "VALIDÉE", "ANNULÉE"});
        comboFiltreEtat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboFiltreEtat.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        filterPanel.add(comboFiltreEtat);
        filterPanel.add(Box.createHorizontalStrut(20));
        
        filterPanel.add(new JLabel("Date (JJ/MM/AAAA):"));
        
        txtRechercheDate = new JTextField(10);
        txtRechercheDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtRechercheDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        filterPanel.add(txtRechercheDate);
        
        btnRechercher = new JButton("🔍 Rechercher");
        btnRechercher.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRechercher.setBackground(ACCENT_COLOR);
        btnRechercher.setForeground(Color.WHITE);
        btnRechercher.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnRechercher.setFocusPainted(false);
        btnRechercher.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRechercher.addActionListener(e -> rechercherCommandes());
        
        filterPanel.add(btnRechercher);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // ===== TABLEAU HISTORIQUE =====
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BG_CARD);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel histTitle = new JLabel("📋 LISTE DES COMMANDES");
        histTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        histTitle.setForeground(PRIMARY_COLOR);
        tablePanel.add(histTitle, BorderLayout.NORTH);
        
        String[] colonnesHist = {"ID", "Date", "Table", "Total (F)", "État", "Actions"};
        tableModelHistorique = new DefaultTableModel(colonnesHist, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Actions seulement
            }
        };
        
        tableHistorique = new JTable(tableModelHistorique);
        tableHistorique.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableHistorique.setRowHeight(40);
        tableHistorique.setShowGrid(true);
        tableHistorique.setGridColor(BORDER_LIGHT);
        tableHistorique.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableHistorique.getTableHeader().setBackground(new Color(245, 245, 245));
        tableHistorique.getTableHeader().setForeground(PRIMARY_COLOR);
        
        // Renderer pour la colonne État
        tableHistorique.getColumn("État").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if ("VALIDÉE".equals(value)) {
                    c.setForeground(SUCCESS_COLOR);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if ("EN_COURS".equals(value)) {
                    c.setForeground(WARNING_COLOR);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if ("ANNULÉE".equals(value)) {
                    c.setForeground(DANGER_COLOR);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                return c;
            }
        });
        
        // Boutons d'action
        tableHistorique.getColumn("Actions").setCellRenderer(new ActionsRenderer());
        tableHistorique.getColumn("Actions").setCellEditor(new ActionsEditor());
        
        JScrollPane scrollHist = new JScrollPane(tableHistorique);
        scrollHist.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
        tablePanel.add(scrollHist, BorderLayout.CENTER);
        
        // Total du jour
        JPanel totalJourPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalJourPanel.setOpaque(false);
        totalJourPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel totalJourLabel = new JLabel("Total du jour:");
        totalJourLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        lblTotalJour = new JLabel("0 F");
        lblTotalJour.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalJour.setForeground(SUCCESS_COLOR);
        
        totalJourPanel.add(totalJourLabel);
        totalJourPanel.add(Box.createHorizontalStrut(10));
        totalJourPanel.add(lblTotalJour);
        
        tablePanel.add(totalJourPanel, BorderLayout.SOUTH);
        
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(240, 240, 240));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        String userInfo = "✅ Connecté en tant que: ";
        if (utilisateurConnecte != null) {
            userInfo += utilisateurConnecte.getLogin() + " (" + utilisateurConnecte.getRole() + ")";
        }
        
        lblStatus = new JLabel(userInfo);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(100, 100, 100));
        
        JLabel infoLabel = new JLabel("🛒 Module Commandes • Section 2.3 du sujet");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(150, 150, 150));
        
        footer.add(lblStatus, BorderLayout.WEST);
        footer.add(infoLabel, BorderLayout.EAST);
        
        return footer;
    }
    
    private void chargerProduits() {
        try {
            produitsDisponibles = produitDAO.findAll();
            comboProduits.removeAllItems();
            comboProduits.addItem(null);
            
            for (Produit p : produitsDisponibles) {
                comboProduits.addItem(p);
            }
        } catch (Exception e) {
            logger.severe("Erreur chargement produits: " + e.getMessage());
        }
    }
    
    private void nouvelleCommande() {
    commandeEnCours = new Commande();
    commandeEnCours.setDateCommande(new Date());
    commandeEnCours.setEtat("EN_COURS");
    
    // Initialiser le stock temporaire
    stockTemporel = new HashMap<>();
    if (produitsDisponibles != null) {
        for (Produit p : produitsDisponibles) {
            stockTemporel.put(p.getId(), p.getStockActuel());
        }
    }
    
    // Vider le panier
    tableModelPanier.setRowCount(0);
    lblTotal.setText("0 F");
    
    // ✅ GÉRER LE NUMÉRO DE COMMANDE
    if (lblNumeroCommande != null) {
        lblNumeroCommande.setText("--");
    }
    
    txtTable.setText("");
    
    // Réinitialiser l'affichage produit
    afficherInfosProduit();
}
    
    private void afficherInfosProduit() {
        Produit p = (Produit) comboProduits.getSelectedItem();
        if (p != null) {
            int stockDispo = stockTemporel.getOrDefault(p.getId(), p.getStockActuel());
            lblPrix.setText(String.format("%,d F", (int)p.getPrixVente()));
            lblStock.setText(stockDispo + " unités");
            
            // Ajuster le maximum du spinner
            SpinnerNumberModel model = (SpinnerNumberModel) spinQuantite.getModel();
            model.setMaximum(Math.max(1, stockDispo));
        } else {
            lblPrix.setText("-");
            lblStock.setText("-");
        }
    }
    
    private void ajouterAuPanier() {
        Produit p = (Produit) comboProduits.getSelectedItem();
        if (p == null) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un produit",
                "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quantite = (int) spinQuantite.getValue();
        int stockRestant = stockTemporel.get(p.getId());
        
        if (quantite > stockRestant) {
            JOptionPane.showMessageDialog(this,
                "Stock insuffisant !\nDisponible: " + stockRestant,
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Mettre à jour le stock temporaire
        stockTemporel.put(p.getId(), stockRestant - quantite);
        
        // Ajouter au tableau
        double montant = quantite * p.getPrixVente();
        tableModelPanier.addRow(new Object[]{
            tableModelPanier.getRowCount() + 1,
            p.getNom(),
            quantite,
            String.format("%,d", (int)p.getPrixVente()),
            String.format("%,d", (int)montant)
        });
        
        calculerTotal();
        afficherInfosProduit();
    }
    
    private void calculerTotal() {
        double total = 0;
        for (int i = 0; i < tableModelPanier.getRowCount(); i++) {
            String montantStr = (String) tableModelPanier.getValueAt(i, 4);
            montantStr = montantStr.replace(" ", "");
            total += Double.parseDouble(montantStr);
        }
        lblTotal.setText(String.format("%,d F", (int)total));
    }
    
    private void supprimerLigne() {
        int row = tablePanier.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une ligne à supprimer",
                "Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Récupérer les infos pour remettre en stock
        String nomProduit = (String) tableModelPanier.getValueAt(row, 1);
        int quantite = (int) tableModelPanier.getValueAt(row, 2);
        
        // Trouver le produit correspondant
        for (Produit p : produitsDisponibles) {
            if (p.getNom().equals(nomProduit)) {
                int stockActuel = stockTemporel.get(p.getId());
                stockTemporel.put(p.getId(), stockActuel + quantite);
                break;
            }
        }
        
        tableModelPanier.removeRow(row);
        
        // Réorganiser les numéros
        for (int i = 0; i < tableModelPanier.getRowCount(); i++) {
            tableModelPanier.setValueAt(i + 1, i, 0);
        }
        
        calculerTotal();
        afficherInfosProduit();
    }
    
    private void annulerCommande() {
        if (tableModelPanier.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Aucune commande en cours",
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment annuler cette commande ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            nouvelleCommande();
            JOptionPane.showMessageDialog(this,
                "Commande annulée",
                "Succès", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void validerCommande() {
        if (tableModelPanier.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "La commande est vide",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Récupérer le numéro de table
            String tableStr = txtTable.getText().trim();
            int tableNum = 0;
            if (!tableStr.isEmpty()) {
                try {
                    tableNum = Integer.parseInt(tableStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "Le numéro de table doit être un nombre",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Calculer le total
            double total = 0;
            String totalStr = lblTotal.getText().replace(" F", "").replace(" ", "");
            total = Double.parseDouble(totalStr);
            
            // Créer la commande
            commandeEnCours.setTotal(total);
            commandeEnCours.setEtat("VALIDÉE");
            
            if (commandeDAO.create(commandeEnCours)) {
                // Créer les lignes de commande et mettre à jour le stock
                for (int i = 0; i < tableModelPanier.getRowCount(); i++) {
                    String nomProduit = (String) tableModelPanier.getValueAt(i, 1);
                    int quantite = (int) tableModelPanier.getValueAt(i, 2);
                    String prixStr = ((String) tableModelPanier.getValueAt(i, 3)).replace(" ", "");
                    double prixUnitaire = Double.parseDouble(prixStr);
                    double montantLigne = (double) Integer.parseInt(((String) tableModelPanier.getValueAt(i, 4)).replace(" ", ""));
                    
                    // Trouver le produit
                    for (Produit p : produitsDisponibles) {
                        if (p.getNom().equals(nomProduit)) {
                            // Créer la ligne de commande
                            LigneCommande ligne = new LigneCommande();
                            ligne.setCommandeId(commandeEnCours.getId());
                            ligne.setProduit(p);
                            ligne.setQuantite(quantite);
                            ligne.setPrixUnitaire(prixUnitaire);
                            ligne.setMontantLigne(montantLigne);
                            ligneCommandeDAO.create(ligne);
                            
                            // Mettre à jour le stock réel
                            p.setStockActuel(stockTemporel.get(p.getId()));
                            produitDAO.update(p);
                            break;
                        }
                    }
                }
                
                JOptionPane.showMessageDialog(this,
                    "✅ Commande validée avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                btnFacture.setEnabled(true);
                chargerHistorique(); // Rafraîchir l'historique
                
                // Proposer une nouvelle commande
                int reponse = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous créer une nouvelle commande ?",
                    "Nouvelle commande", JOptionPane.YES_NO_OPTION);
                
                if (reponse == JOptionPane.YES_OPTION) {
                    nouvelleCommande();
                } else {
                    // Réinitialiser pour une nouvelle commande mais garder la fenêtre ouverte
                    nouvelleCommande();
                }
            }
        } catch (Exception e) {
            logger.severe("Erreur validation: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "❌ Erreur lors de la validation",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void imprimerFacture() {
        JOptionPane.showMessageDialog(this,
            "🧾 Impression de la facture\n\nFonctionnalité à venir !",
            "Facture", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void chargerHistorique() {
        try {
            List<Commande> commandes = commandeDAO.findAll();
            tableModelHistorique.setRowCount(0);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            double totalJour = 0;
            Calendar today = Calendar.getInstance();
            
            for (Commande c : commandes) {
                // Vérifier si c'est une commande d'aujourd'hui pour le total
                Calendar dateCommande = Calendar.getInstance();
                dateCommande.setTime(c.getDateCommande());
                
                if (dateCommande.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    dateCommande.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                    "VALIDÉE".equals(c.getEtat())) {
                    totalJour += c.getTotal();
                }
                
                tableModelHistorique.addRow(new Object[]{
                    c.getId(),
                    sdf.format(c.getDateCommande()),
                    "Table " + (c.getId() % 10 + 1), // Simulation de table
                    String.format("%,d", (int)c.getTotal()),
                    c.getEtat(),
                    "Voir détails"
                });
            }
            
            lblTotalJour.setText(String.format("%,d F", (int)totalJour));
            
        } catch (Exception e) {
            logger.severe("Erreur chargement historique: " + e.getMessage());
        }
    }
    
    private void rechercherCommandes() {
        String etat = (String) comboFiltreEtat.getSelectedItem();
        String dateStr = txtRechercheDate.getText().trim();
        
        try {
            List<Commande> commandes;
            
            if (!"Tous".equals(etat) && !dateStr.isEmpty()) {
                // Filtrer par état et date
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date date = sdf.parse(dateStr);
                commandes = commandeDAO.findByDate(new java.sql.Date(date.getTime()));
                // Filtrer par état manuellement
                commandes.removeIf(c -> !c.getEtat().equals(etat));
            } else if (!"Tous".equals(etat)) {
                commandes = commandeDAO.findByEtat(etat);
            } else if (!dateStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date date = sdf.parse(dateStr);
                commandes = commandeDAO.findByDate(new java.sql.Date(date.getTime()));
            } else {
                commandes = commandeDAO.findAll();
            }
            
            tableModelHistorique.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            for (Commande c : commandes) {
                tableModelHistorique.addRow(new Object[]{
                    c.getId(),
                    sdf.format(c.getDateCommande()),
                    "Table " + (c.getId() % 10 + 1),
                    String.format("%,d", (int)c.getTotal()),
                    c.getEtat(),
                    "Voir détails"
                });
            }
            
        } catch (Exception e) {
            logger.severe("Erreur recherche: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Format de date invalide (JJ/MM/AAAA)",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ===== CLASSES POUR LES BOUTONS D'ACTION DANS L'HISTORIQUE =====
    class ActionsRenderer extends JPanel implements TableCellRenderer {
        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            
            JButton btnDetails = new JButton("📋 Détails");
            btnDetails.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnDetails.setBackground(ACCENT_COLOR);
            btnDetails.setForeground(Color.WHITE);
            btnDetails.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            add(btnDetails);
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(row % 2 == 0 ? BG_CARD : new Color(250, 250, 250));
            }
            
            return this;
        }
    }
    
    class ActionsEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton btnDetails;
        private int currentId;
        
        public ActionsEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setOpaque(true);
            
            btnDetails = new JButton("📋 Détails");
            btnDetails.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnDetails.setBackground(ACCENT_COLOR);
            btnDetails.setForeground(Color.WHITE);
            btnDetails.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            btnDetails.addActionListener(e -> {
                afficherDetailsCommande(currentId);
                fireEditingStopped();
            });
            
            panel.add(btnDetails);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentId = (int) table.getValueAt(row, 0);
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Détails";
        }
    }
    
    private void afficherDetailsCommande(int commandeId) {
        JOptionPane.showMessageDialog(this,
            "📋 Détails de la commande #" + commandeId + "\n\nFonctionnalité à venir !",
            "Détails commande", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new GestionCommandesFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
