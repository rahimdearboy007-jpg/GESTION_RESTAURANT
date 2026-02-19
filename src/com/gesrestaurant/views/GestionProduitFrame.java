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
import com.gesrestaurant.model.*;
import com.gesrestaurant.dao.*;
import com.gesrestaurant.controller.*;
import java.text.SimpleDateFormat;
import com.gesrestaurant.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;



/**
 *
 * @author rahim
 */
public class GestionProduitFrame extends javax.swing.JFrame {
    
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GestionProduitFrame.class.getName());
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BG_CARD = Color.WHITE;  // ✅ AJOUTÉ !
    
    private JTable tableProduits;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtPrix, txtStock, txtSeuil;
    private JComboBox<Categorie> comboCategories;
    private JLabel lblStatus;
    private ProduitDAO produitDAO;
    private CategorieDAO categorieDAO;
    private Connection connection;
    
    public GestionProduitFrame() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            this.connection = conn;
            this.categorieDAO = new CategorieDAO(conn);
            this.produitDAO = new ProduitDAO(conn, this.categorieDAO);
            logger.info("✅ Connexion BDD établie");
        } catch (Exception e) {
            logger.severe("❌ Erreur connexion BDD: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Impossible de se connecter à la base de données.\n" +
                "L'application fonctionnera en mode démonstration.",
                "Erreur de connexion", JOptionPane.WARNING_MESSAGE);
        }
        initComponentsCustom();
        setTitle("📦 Gestion des Produits & Catégories");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void initComponentsCustom() {
        // ============================================
        // LAYOUT PRINCIPAL
        // ============================================
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        
        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📦 GESTION DES PRODUITS & CATÉGORIES");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JButton btnRetour = new JButton("🔙 Retour au Dashboard");
        btnRetour.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRetour.setBackground(new Color(255, 255, 255, 30));
        btnRetour.setForeground(Color.WHITE);
        btnRetour.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRetour.setFocusPainted(false);
        btnRetour.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRetour.addActionListener(e -> dispose());
        
        btnRetour.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRetour.setBackground(new Color(255, 255, 255, 60));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnRetour.setBackground(new Color(255, 255, 255, 30));
            }
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnRetour, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // ===== BARRE D'OUTILS =====
        JPanel toolBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolBarPanel.setBackground(new Color(250, 250, 250));
        toolBarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        JButton btnNouveau = createToolButton("➕", "Nouveau produit", "Ctrl+N", SUCCESS_COLOR);
        btnNouveau.addActionListener(e -> nouveauProduit());

        JButton btnModifier = createToolButton("✏️", "Modifier", "Ctrl+E", ACCENT_COLOR);
        btnModifier.addActionListener(e -> modifierProduit());

        JButton btnSupprimer = createToolButton("🗑️", "Supprimer", "Suppr", DANGER_COLOR);
        btnSupprimer.addActionListener(e -> supprimerProduit());
        
        JButton btnRechercher = createToolButton("🔍", "Rechercher", "Ctrl+F", new Color(100, 100, 100));
        btnRechercher.addActionListener(e -> rechercherProduits());
        
        toolBarPanel.add(btnNouveau);
        toolBarPanel.add(btnModifier);
        toolBarPanel.add(btnSupprimer);
        toolBarPanel.add(Box.createHorizontalStrut(20));
        toolBarPanel.add(btnRechercher);
        
        add(toolBarPanel, BorderLayout.NORTH);
        
        // ===== TABBEDPANE AVEC 2 ONGLETS =====
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        
        // Onglet 1 : Gestion des produits
        tabbedPane.addTab("📦 Produits", createProduitsPanel());
        
        // Onglet 2 : Gestion des catégories
        tabbedPane.addTab("📁 Catégories", createCategoriesPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // ===== FOOTER =====
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        lblStatus = new JLabel("✅ Prêt • Connecté à la base de données");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(100, 100, 100));
        
        JLabel infoLabel = new JLabel("📦 Gestion des produits • Section 2.1 du sujet");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(150, 150, 150));
        
        footerPanel.add(lblStatus, BorderLayout.WEST);
        footerPanel.add(infoLabel, BorderLayout.EAST);
        
        add(footerPanel, BorderLayout.SOUTH);
        
        // ===== CHARGEMENT DES DONNÉES =====
        chargerCategories();
        chargerProduits();
    }
    
    // ============================================
    // MÉTHODES MÉTIER PRODUITS
    // ============================================

    private void nouveauProduit() {
        viderFormulaire();
        txtNom.requestFocus();
    }

    private void modifierProduit() {
        int selectedRow = tableProduits.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Sélectionnez un produit à modifier",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        chargerProduitSelectionne();
    }

    private void supprimerProduit() {
        int selectedRow = tableProduits.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Sélectionnez un produit à supprimer",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nom = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Supprimer le produit : " + nom + " ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (produitDAO != null) {
                    produitDAO.delete(id);
                    JOptionPane.showMessageDialog(this, "✅ Produit supprimé !");
                }
                chargerProduits();
                viderFormulaire();
            } catch (Exception e) {
                logger.severe("Erreur suppression: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void enregistrerProduit() {
        // Validation
        if (txtNom.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom est obligatoire");
            txtNom.requestFocus();
            return;
        }
        
        Categorie cat = (Categorie) comboCategories.getSelectedItem();
        if (cat == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une catégorie");
            return;
        }
        
        try {
            double prix = Double.parseDouble(txtPrix.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            int seuil = Integer.parseInt(txtSeuil.getText().trim());
            
            if (prix <= 0 || stock < 0 || seuil < 0) {
                throw new NumberFormatException();
            }
            
            Produit produit = new Produit(
                txtNom.getText().trim(),
                cat,
                prix,
                stock,
                seuil
            );
            
            int selectedRow = tableProduits.getSelectedRow();
            
            if (selectedRow == -1) {
                // AJOUT
                if (produitDAO != null) {
                    produitDAO.create(produit);
                    JOptionPane.showMessageDialog(this, "✅ Produit ajouté !");
                }
            } else {
                // MODIFICATION
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                produit.setId(id);
                if (produitDAO != null) {
                    produitDAO.update(produit);
                    JOptionPane.showMessageDialog(this, "✅ Produit modifié !");
                }
            }
            
            chargerProduits();
            viderFormulaire();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Valeurs numériques invalides\n" +
                "Prix > 0, Stock ≥ 0, Seuil ≥ 0",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.severe("Erreur enregistrement: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'enregistrement",
                "Erreur BDD", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void rechercherProduits() {
        // Dialogue de recherche
        String motCle = JOptionPane.showInputDialog(this,
            "🔍 Entrez un nom de produit ou une catégorie :",
            "Rechercher un produit",
            JOptionPane.QUESTION_MESSAGE);
        
        if (motCle == null || motCle.trim().isEmpty()) {
            return; // Annulé ou vide
        }
        
        motCle = motCle.trim().toLowerCase();
        
        // Mode démo (pas de BDD)
        if (produitDAO == null) {
            rechercherProduitsDemo(motCle);
            return;
        }
        
        // Mode BDD
        try {
            List<Produit> resultats = produitDAO.search(motCle);
            tableModel.setRowCount(0);
            
            if (resultats.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "🔍 Aucun produit trouvé pour : \"" + motCle + "\"",
                    "Résultat de recherche",
                    JOptionPane.INFORMATION_MESSAGE);
                chargerProduits();
                return;
            }
            
            for (Produit p : resultats) {
                tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getNom(),
                    p.getCategorie().getLibelle(),
                    p.getPrixVente(),
                    p.getStockActuel(),
                    p.getSeuilAlerte(),
                    p.getStatut()
                });
            }
            
            updateCountLabel();
            
            JOptionPane.showMessageDialog(this,
                "🔍 " + resultats.size() + " produit(s) trouvé(s)",
                "Résultat de recherche",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            logger.severe("Erreur recherche: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la recherche",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createProduitsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(550);
        splitPane.setDividerSize(5);
        splitPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        splitPane.setBackground(Color.WHITE);
        
        // ===== PANEL GAUCHE - Liste des produits =====
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Titre du panneau gauche
        JPanel leftTitlePanel = new JPanel(new BorderLayout());
        leftTitlePanel.setOpaque(false);
        
        JLabel listTitle = new JLabel("📋 Liste des produits");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        listTitle.setForeground(PRIMARY_COLOR);
        
        JLabel countLabel = new JLabel("0 produit");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(new Color(150, 150, 150));
        
        leftTitlePanel.add(listTitle, BorderLayout.WEST);
        leftTitlePanel.add(countLabel, BorderLayout.EAST);
        
        leftPanel.add(leftTitlePanel, BorderLayout.NORTH);
        
        // Tableau des produits
        String[] columns = {"ID", "Nom", "Catégorie", "Prix (F CFA)", "Stock", "Seuil", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableProduits = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                
                if (column == 6) {
                    String statut = getValueAt(row, 6).toString();
                    if (statut.contains("⚠️")) {
                        comp.setBackground(new Color(241, 196, 15, 30));
                        comp.setForeground(WARNING_COLOR);
                    } else if (statut.contains("🔴")) {
                        comp.setBackground(new Color(231, 76, 60, 30));
                        comp.setForeground(DANGER_COLOR);
                    } else {
                        comp.setBackground(Color.WHITE);
                        comp.setForeground(SUCCESS_COLOR);
                    }
                } else {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        };
        
        tableProduits.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableProduits.setRowHeight(35);
        tableProduits.setShowGrid(true);
        tableProduits.setGridColor(new Color(240, 240, 240));
        tableProduits.setSelectionBackground(new Color(52, 152, 219, 50));
        tableProduits.setSelectionForeground(Color.BLACK);
        tableProduits.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                chargerProduitSelectionne();
            }
        });
        
        JTableHeader header = tableProduits.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        JScrollPane scrollPane = new JScrollPane(tableProduits);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        // ===== PANEL DROIT - Formulaire produit =====
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Titre du panneau droit
        JPanel rightTitlePanel = new JPanel(new BorderLayout());
        rightTitlePanel.setOpaque(false);
        
        JLabel formTitle = new JLabel("📝 Ajouter / Modifier un produit");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(PRIMARY_COLOR);
        
        JLabel requiredLabel = new JLabel("* Champs obligatoires");
        requiredLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        requiredLabel.setForeground(new Color(150, 150, 150));
        
        rightTitlePanel.add(formTitle, BorderLayout.WEST);
        rightTitlePanel.add(requiredLabel, BorderLayout.EAST);
        
        rightPanel.add(rightTitlePanel, BorderLayout.NORTH);
        
        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Nom du produit
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblNom = new JLabel("Nom du produit *");
        lblNom.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNom.setForeground(PRIMARY_COLOR);
        formPanel.add(lblNom, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtNom = new JTextField();
        txtNom.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtNom, gbc);
        
        // Catégorie
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblCategorie = new JLabel("Catégorie *");
        lblCategorie.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCategorie.setForeground(PRIMARY_COLOR);
        formPanel.add(lblCategorie, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        comboCategories = new JComboBox<>();
        comboCategories.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboCategories.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        comboCategories.setBackground(Color.WHITE);
        formPanel.add(comboCategories, gbc);
        
        // Prix
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblPrix = new JLabel("Prix de vente (F CFA) *");
        lblPrix.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPrix.setForeground(PRIMARY_COLOR);
        formPanel.add(lblPrix, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtPrix = new JTextField();
        txtPrix.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPrix.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtPrix, gbc);
        
        // Stock
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel lblStock = new JLabel("Stock actuel *");
        lblStock.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStock.setForeground(PRIMARY_COLOR);
        formPanel.add(lblStock, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtStock = new JTextField();
        txtStock.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtStock.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtStock, gbc);
        
        // Seuil d'alerte
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel lblSeuil = new JLabel("Seuil d'alerte *");
        lblSeuil.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSeuil.setForeground(PRIMARY_COLOR);
        formPanel.add(lblSeuil, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtSeuil = new JTextField();
        txtSeuil.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSeuil.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtSeuil, gbc);
        
        // Boutons
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 10, 10, 10);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        JButton btnSave = new JButton("💾 Enregistrer");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(SUCCESS_COLOR);
        btnSave.setForeground(Color.WHITE);
        btnSave.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> enregistrerProduit());
        
        JButton btnClear = new JButton("🔄 Vider");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnClear.setBackground(new Color(240, 240, 240));
        btnClear.setForeground(new Color(100, 100, 100));
        btnClear.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        btnClear.setFocusPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> viderFormulaire());
        
        btnSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSave.setBackground(SUCCESS_COLOR.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnSave.setBackground(SUCCESS_COLOR);
            }
        });
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClear);
        
        formPanel.add(buttonPanel, gbc);
        
        rightPanel.add(formPanel, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void rechercherProduitsDemo(String motCle) {
        // Simuler une recherche dans les données de démo
        List<Object[]> resultats = new ArrayList<>();
        Object[][] tousProduits = {
            {1, "Coca-Cola 33cl", "Boissons", 2.50, 100, 20, "✅ OK"},
            {2, "Eau minérale", "Boissons", 1.50, 150, 30, "✅ OK"},
            {3, "Pizza Margherita", "Plats principaux", 8.50, 50, 10, "✅ OK"},
            {4, "Salade César", "Entrées", 6.50, 30, 5, "✅ OK"},
            {5, "Tiramisu", "Desserts", 4.50, 40, 8, "✅ OK"},
            {6, "Frites", "Snacks", 3.00, 200, 50, "✅ OK"}
        };
        
        for (Object[] p : tousProduits) {
            String nom = p[1].toString().toLowerCase();
            String categorie = p[2].toString().toLowerCase();
            
            if (nom.contains(motCle) || categorie.contains(motCle)) {
                resultats.add(p);
            }
        }
        
        tableModel.setRowCount(0);
        
        if (resultats.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "🔍 Aucun produit trouvé pour : \"" + motCle + "\"",
                "Résultat de recherche",
                JOptionPane.INFORMATION_MESSAGE);
            chargerProduits();
            return;
        }
        
        for (Object[] p : resultats) {
            tableModel.addRow(p);
        }
        
        updateCountLabel();
        
        JOptionPane.showMessageDialog(this,
                "🔍 " + resultats.size() + " produit(s) trouvé(s) (mode démo)",
                "Résultat de recherche",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void viderFormulaire() {
        txtNom.setText("");
        txtPrix.setText("");
        txtStock.setText("");
        txtSeuil.setText("");
        comboCategories.setSelectedIndex(0);
        tableProduits.clearSelection();
    }
    
    private void chargerProduitSelectionne() {
        int selectedRow = tableProduits.getSelectedRow();
        if (selectedRow == -1) return;
        
        txtNom.setText(tableModel.getValueAt(selectedRow, 1).toString());
        txtPrix.setText(tableModel.getValueAt(selectedRow, 3).toString());
        txtStock.setText(tableModel.getValueAt(selectedRow, 4).toString());
        txtSeuil.setText(tableModel.getValueAt(selectedRow, 5).toString());
        
        // Sélectionner la catégorie
        String catLibelle = tableModel.getValueAt(selectedRow, 2).toString();
        for (int i = 0; i < comboCategories.getItemCount(); i++) {
            Categorie cat = comboCategories.getItemAt(i);
            if (cat != null && cat.getLibelle().equals(catLibelle)) {
                comboCategories.setSelectedIndex(i);
                break;
            }
        }
    }

    private void fallbackProduits() {
        tableModel.setRowCount(0);
        
        Object[][] produits = {
            {1, "Coca-Cola 33cl", "Boissons", 2.50, 100, 20, "✅ OK"},
            {2, "Eau minérale", "Boissons", 1.50, 150, 30, "✅ OK"},
            {3, "Pizza Margherita", "Plats principaux", 8.50, 50, 10, "✅ OK"},
            {4, "Salade César", "Entrées", 6.50, 30, 5, "✅ OK"},
            {5, "Tiramisu", "Desserts", 4.50, 40, 8, "✅ OK"},
            {6, "Frites", "Snacks", 3.00, 200, 50, "✅ OK"}
        };
        
        for (Object[] p : produits) {
            tableModel.addRow(p);
        }
        
        updateCountLabel();
    }
    
    private JButton createToolButton(String emoji, String text, String shortcut, Color bgColor) {
        JButton button = new JButton(emoji + " " + text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(text + " (" + shortcut + ")");
        
        return button;
    }
    
    private void chargerCategories() {
        if (categorieDAO == null) {
            // Mode démo
            comboCategories.removeAllItems();
            comboCategories.addItem(null);
            comboCategories.addItem(new Categorie(1, "Boissons"));
            comboCategories.addItem(new Categorie(2, "Plats principaux"));
            comboCategories.addItem(new Categorie(3, "Entrées"));
            comboCategories.addItem(new Categorie(4, "Desserts"));
            comboCategories.addItem(new Categorie(5, "Snacks"));
            return;
        }
        
        try {
            List<Categorie> categories = categorieDAO.findAll();
            comboCategories.removeAllItems();
            comboCategories.addItem(null); // Option vide
            
            for (Categorie cat : categories) {
                comboCategories.addItem(cat);
            }
        } catch (Exception e) {
            logger.severe("Erreur chargement catégories: " + e.getMessage());
        }
    }
    
    private void chargerProduits() {
        if (produitDAO == null) {
            fallbackProduits();
            return;
        }
        
        try {
            List<Produit> produits = produitDAO.findAll();
            tableModel.setRowCount(0);
            
            for (Produit p : produits) {
                tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getNom(),
                    p.getCategorie().getLibelle(),
                    p.getPrixVente(),
                    p.getStockActuel(),
                    p.getSeuilAlerte(),
                    p.getStatut()
                });
            }
            
            // Mettre à jour le compteur
            updateCountLabel();
            
        } catch (Exception e) {
            logger.severe("Erreur chargement produits: " + e.getMessage());
            fallbackProduits();
        }
    }

    private void updateCountLabel() {
        try {
            Component[] leftComponents = ((JPanel) ((JSplitPane) getContentPane()
                .getComponent(2)).getLeftComponent()).getComponents();
            for (Component comp : leftComponents) {
                if (comp instanceof JPanel) {
                    Component[] titleComps = ((JPanel) comp).getComponents();
                    for (Component c : titleComps) {
                        if (c instanceof JLabel && ((JLabel) c).getText().contains("produit")) {
                            ((JLabel) c).setText(tableModel.getRowCount() + " produits");
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignorer
        }
    }

    private JPanel createCategoriesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // ===== FORMULAIRE D'AJOUT =====
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblNouvelle = new JLabel("Nouvelle catégorie:");
        lblNouvelle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNouvelle.setForeground(PRIMARY_COLOR);
        
        JTextField txtNouvelleCategorie = new JTextField(20);
        txtNouvelleCategorie.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNouvelleCategorie.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        JButton btnAjouter = new JButton("➕ Ajouter");
        btnAjouter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAjouter.setBackground(SUCCESS_COLOR);
        btnAjouter.setForeground(Color.WHITE);
        btnAjouter.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAjouter.setFocusPainted(false);
        btnAjouter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAjouter.addActionListener(e -> {
            String libelle = txtNouvelleCategorie.getText().trim();
            if (!libelle.isEmpty()) {
                ajouterCategorie(libelle);
                txtNouvelleCategorie.setText("");
            }
        });
        
        formPanel.add(lblNouvelle);
        formPanel.add(txtNouvelleCategorie);
        formPanel.add(Box.createHorizontalStrut(10));
        formPanel.add(btnAjouter);
        
        // ===== TABLEAU DES CATÉGORIES =====
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel tableTitle = new JLabel("📋 Liste des catégories");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(PRIMARY_COLOR);
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Libellé", "Nb Produits", "Actions"};
        DefaultTableModel modelCategories = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        
        JTable tableCategories = new JTable(modelCategories);
        tableCategories.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableCategories.setRowHeight(40);
        tableCategories.setShowGrid(true);
        tableCategories.setGridColor(new Color(230, 230, 230));
        tableCategories.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableCategories.getTableHeader().setBackground(new Color(245, 245, 245));
        tableCategories.getTableHeader().setForeground(PRIMARY_COLOR);
        
        // Renderer pour la colonne Actions
        tableCategories.getColumn("Actions").setCellRenderer(new CategoriesActionsRenderer());
        tableCategories.getColumn("Actions").setCellEditor(new CategoriesActionsEditor(modelCategories));
        
        JScrollPane scrollPane = new JScrollPane(tableCategories);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // ===== ASSEMBLAGE =====
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        
        // Charger les catégories
        chargerCategories(modelCategories);
        
        return panel;
    }

    // ===== RENDERER POUR LES BOUTONS D'ACTION DES CATÉGORIES =====
    class CategoriesActionsRenderer extends JPanel implements TableCellRenderer {
        public CategoriesActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            
            JButton btnEdit = new JButton("✏️");
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnEdit.setBackground(ACCENT_COLOR);
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            JButton btnDelete = new JButton("🗑️");
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnDelete.setBackground(DANGER_COLOR);
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            add(btnEdit);
            add(btnDelete);
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(row % 2 == 0 ? BG_CARD : new Color(250, 250, 250));
            }
            
            return this;
        }
    }

    // ===== EDITOR POUR LES BOUTONS D'ACTION DES CATÉGORIES =====
    class CategoriesActionsEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton btnEdit;
        private JButton btnDelete;
        private int currentId;
        private String currentLibelle;
        private int currentNbProduits;
        private DefaultTableModel model;
        
        public CategoriesActionsEditor(DefaultTableModel model) {
            this.model = model;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setOpaque(true);
            
            btnEdit = new JButton("✏️");
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnEdit.setBackground(ACCENT_COLOR);
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            btnEdit.addActionListener(e -> {
                modifierCategorie(currentId, currentLibelle, model);
                fireEditingStopped();
            });
            
            btnDelete = new JButton("🗑️");
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnDelete.setBackground(DANGER_COLOR);
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            btnDelete.addActionListener(e -> {
                supprimerCategorie(currentId, currentLibelle, currentNbProduits, model);
                fireEditingStopped();
            });
            
            panel.add(btnEdit);
            panel.add(btnDelete);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentId = (int) table.getValueAt(row, 0);
            currentLibelle = (String) table.getValueAt(row, 1);
            currentNbProduits = (int) table.getValueAt(row, 2);
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    private void ajouterCategorie(String libelle) {
        try {
            // Vérifier si la catégorie existe déjà
            if (categorieDAO.findByLibelle(libelle) != null) {
                JOptionPane.showMessageDialog(this,
                    "❌ Une catégorie '" + libelle + "' existe déjà !",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Categorie nouvelle = new Categorie(libelle);
            if (categorieDAO.create(nouvelle)) {
                JOptionPane.showMessageDialog(this, "✅ Catégorie ajoutée !");
                
                // Recharger l'affichage des catégories
                JTabbedPane tabbedPane = (JTabbedPane) getContentPane().getComponent(2);
                JPanel categoriesPanel = (JPanel) tabbedPane.getComponentAt(1);
                JPanel tablePanel = (JPanel) categoriesPanel.getComponent(1);
                JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(1);
                JTable tableCategories = (JTable) scrollPane.getViewport().getView();
                chargerCategories((DefaultTableModel) tableCategories.getModel());
                
                // Recharger le comboBox des catégories pour les produits
                chargerCategories();
            }
        } catch (Exception e) {
            logger.severe("Erreur ajout catégorie: " + e.getMessage());
        }
    }

    private void modifierCategorie(int id, String ancienLibelle, DefaultTableModel model) {
        String nouveauLibelle = JOptionPane.showInputDialog(this,
            "Modifier le libellé de la catégorie :",
            ancienLibelle);
        
        if (nouveauLibelle != null && !nouveauLibelle.trim().isEmpty()) {
            try {
                // Vérifier si le nouveau libellé existe déjà
                if (!nouveauLibelle.equals(ancienLibelle) && 
                    categorieDAO.findByLibelle(nouveauLibelle) != null) {
                    JOptionPane.showMessageDialog(this,
                        "❌ Une catégorie '" + nouveauLibelle + "' existe déjà !",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Categorie cat = new Categorie(id, nouveauLibelle);
                if (categorieDAO.update(cat)) {
                    JOptionPane.showMessageDialog(this, "✅ Catégorie modifiée !");
                    chargerCategories(model);
                    chargerCategories(); // Pour le comboBox
                }
            } catch (Exception e) {
                logger.severe("Erreur modification catégorie: " + e.getMessage());
            }
        }
    }

    private void supprimerCategorie(int id, String libelle, int nbProduits, DefaultTableModel model) {
        // Gérer le cas des produits rattachés
        if (nbProduits > 0) {
            JOptionPane.showMessageDialog(this,
                "❌ Impossible de supprimer cette catégorie !\n" +
                "Elle est utilisée par " + nbProduits + " produit(s).\n" +
                "Veuillez d'abord réaffecter ou supprimer ces produits.",
                "Suppression impossible", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Supprimer la catégorie '" + libelle + "' ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (categorieDAO.delete(id)) {
                    JOptionPane.showMessageDialog(this, "✅ Catégorie supprimée !");
                    chargerCategories(model);
                    chargerCategories(); // Pour le comboBox
                }
            } catch (Exception e) {
                logger.severe("Erreur suppression catégorie: " + e.getMessage());
            }
        }
    }

    private void chargerCategories(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Categorie> categories = categorieDAO.findAll();
            for (Categorie cat : categories) {
                int nbProduits = compterProduitsParCategorie(cat.getId());
                model.addRow(new Object[]{
                    cat.getId(),
                    cat.getLibelle(),
                    nbProduits,
                    "✏️ 🗑️"
                });
            }
        } catch (Exception e) {
            logger.severe("Erreur chargement catégories: " + e.getMessage());
        }
    }

    private int compterProduitsParCategorie(int categorieId) {
        try {
            Connection conn = categorieDAO.getConnection();
            String sql = "SELECT COUNT(*) FROM produit WHERE categorie_id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, categorieId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.severe("Erreur comptage produits par catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    
    
    /**
     * Creates new form GestionProduitFrame
     */
  
    
    
    

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
        java.awt.EventQueue.invokeLater(() -> new GestionProduitFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
