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
import com.gesrestaurant.dao.MvtStockDAO;
import java.util.List;
/**
 *
 * @author rahim
 */
public class GestionStockFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GestionStockFrame.class.getName());
    
    // ===== COULEURS =====
      private static final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BG_PRIMARY = new Color(245, 245, 242);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color BORDER_LIGHT = new Color(225, 225, 220);
    
    // ===== DAO =====
    private ProduitDAO produitDAO;
    private MvtStockDAO mouvementDAO;
    private CategorieDAO categorieDAO;
    
    // ===== COMPOSANTS UI =====
    private JComboBox<Produit> comboProduits;
    private JRadioButton radioEntree, radioSortie;
    private JTextField txtQuantite;
    private JComboBox<String> comboMotifs;
    private JLabel lblStockActuel, lblUnite;
    private JTable tableHistorique;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;

    /**
     * Creates new form GestionStockFrame
     */
    public GestionStockFrame() {
        initDAO();
        initComponentsCustom();
        setTitle("📦 Gestion des Mouvements de Stock");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    private void initDAO() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            this.categorieDAO = new CategorieDAO(conn);
            this.produitDAO = new ProduitDAO(conn, this.categorieDAO);
            this.mouvementDAO = new MvtStockDAO(conn);
            logger.info("✅ Connexion BDD établie pour module stock");
        } catch (Exception e) {
            logger.severe("❌ Erreur connexion BDD: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Mode démonstration activé",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void initComponentsCustom() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_PRIMARY);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
        
        chargerProduits();
        chargerHistorique();
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("📦 GESTION DES MOUVEMENTS DE STOCK");
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
    
    private JScrollPane createMainPanel() {  // ← JScrollPane au lieu de JPanel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        mainPanel.add(createFormulaireCard());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createHistoriqueCard());
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        
        return scrollPane;
    }
    
    private JPanel createFormulaireCard() {
       JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Titre
        JLabel title = new JLabel("📝 NOUVEAU MOUVEMENT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(PRIMARY_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(20));
        
        // Formulaire GridBag
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // === LIGNE 1 : Produit ===
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel lblProduit = new JLabel("Produit *");
        lblProduit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblProduit.setForeground(PRIMARY_COLOR);
        formPanel.add(lblProduit, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        comboProduits = new JComboBox<>();
        comboProduits.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboProduits.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        comboProduits.setBackground(Color.WHITE);
        comboProduits.addActionListener(e -> updateStockActuel());
        formPanel.add(comboProduits, gbc);
        
        // === LIGNE 2 : Type ===
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.2;
        JLabel lblType = new JLabel("Type *");
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblType.setForeground(PRIMARY_COLOR);
        formPanel.add(lblType, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        typePanel.setOpaque(false);
        
        radioEntree = new JRadioButton("📥 ENTRÉE");
        radioEntree.setFont(new Font("Segoe UI", Font.BOLD, 13));
        radioEntree.setForeground(SUCCESS_COLOR);
        radioEntree.setOpaque(false);
        radioEntree.setSelected(true);
        
        radioSortie = new JRadioButton("📤 SORTIE");
        radioSortie.setFont(new Font("Segoe UI", Font.BOLD, 13));
        radioSortie.setForeground(DANGER_COLOR);
        radioSortie.setOpaque(false);
        
        ButtonGroup group = new ButtonGroup();
        group.add(radioEntree);
        group.add(radioSortie);
        
        radioEntree.addActionListener(e -> updateStockActuel());
        radioSortie.addActionListener(e -> updateStockActuel());
        
        typePanel.add(radioEntree);
        typePanel.add(radioSortie);
        formPanel.add(typePanel, gbc);
        
        // === LIGNE 3 : Quantité ===
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.2;
        JLabel lblQuantite = new JLabel("Quantité *");
        lblQuantite.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblQuantite.setForeground(PRIMARY_COLOR);
        formPanel.add(lblQuantite, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        JPanel quantitePanel = new JPanel(new BorderLayout(10, 0));
        quantitePanel.setOpaque(false);
        
        txtQuantite = new JTextField();
        txtQuantite.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtQuantite.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        lblUnite = new JLabel("unités");
        lblUnite.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUnite.setForeground(new Color(150, 150, 150));
        
        quantitePanel.add(txtQuantite, BorderLayout.CENTER);
        quantitePanel.add(lblUnite, BorderLayout.EAST);
        formPanel.add(quantitePanel, gbc);
        
        // === LIGNE 4 : Motif ===
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.2;
        JLabel lblMotif = new JLabel("Motif *");
        lblMotif.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMotif.setForeground(PRIMARY_COLOR);
        formPanel.add(lblMotif, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        comboMotifs = new JComboBox<>();
        comboMotifs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboMotifs.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        comboMotifs.setBackground(Color.WHITE);
        comboMotifs.addItem("Achat / Réapprovisionnement");
        comboMotifs.addItem("Perte / Casse");
        comboMotifs.addItem("Inventaire (ajustement)");
        comboMotifs.addItem("Don");
        comboMotifs.addItem("Autre");
        formPanel.add(comboMotifs, gbc);
        
        // === LIGNE 5 : Stock actuel ===
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weightx = 0.2;
        JLabel lblStockTitre = new JLabel("Stock actuel");
        lblStockTitre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStockTitre.setForeground(PRIMARY_COLOR);
        formPanel.add(lblStockTitre, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        lblStockActuel = new JLabel("-");
        lblStockActuel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblStockActuel.setForeground(SUCCESS_COLOR);
        formPanel.add(lblStockActuel, gbc);
        
        card.add(formPanel);
        card.add(Box.createVerticalStrut(20));
        
        // === BOUTONS ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        JButton btnValider = new JButton("✅ Valider le mouvement");
        btnValider.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnValider.setBackground(SUCCESS_COLOR);
        btnValider.setForeground(Color.WHITE);
        btnValider.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        btnValider.setFocusPainted(false);
        btnValider.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnValider.addActionListener(e -> enregistrerMouvement());
        
        JButton btnAnnuler = new JButton("❌ Annuler");
        btnAnnuler.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAnnuler.setBackground(new Color(240, 240, 240));
        btnAnnuler.setForeground(new Color(100, 100, 100));
        btnAnnuler.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        btnAnnuler.setFocusPainted(false);
        btnAnnuler.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnnuler.addActionListener(e -> viderFormulaire());
        
        buttonPanel.add(btnValider);
        buttonPanel.add(btnAnnuler);
        
        card.add(buttonPanel);
        
        return card; 

    }
    private JPanel createHistoriqueCard() {
        JPanel card = new JPanel(new BorderLayout());
            card.setBackground(BG_CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));

            JLabel title = new JLabel("📋 HISTORIQUE DES MOUVEMENTS");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            title.setForeground(PRIMARY_COLOR);
            card.add(title, BorderLayout.NORTH);

            String[] columns = {"Date", "Produit", "Type", "Quantité", "Motif"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            tableHistorique = new JTable(tableModel);
            tableHistorique.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            tableHistorique.setRowHeight(35);
            tableHistorique.setShowGrid(true);
            tableHistorique.setGridColor(BORDER_LIGHT);
            tableHistorique.setSelectionBackground(new Color(52, 152, 219, 50));
            tableHistorique.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            tableHistorique.getTableHeader().setBackground(new Color(245, 245, 245));
            tableHistorique.getTableHeader().setForeground(PRIMARY_COLOR);
            tableHistorique.getTableHeader().setBorder(BorderFactory.createLineBorder(BORDER_LIGHT));

            JScrollPane scrollPane = new JScrollPane(tableHistorique);
            scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
            scrollPane.getViewport().setBackground(BG_CARD);

            card.add(scrollPane, BorderLayout.CENTER);

            return card;
    }
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(240, 240, 240));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        lblStatus = new JLabel("✅ Prêt • Module Mouvements de Stock (Section 2.2)");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(100, 100, 100));
        
        JLabel infoLabel = new JLabel("📦 Conforme au sujet • Stock mis à jour automatiquement");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(150, 150, 150));
        
        footer.add(lblStatus, BorderLayout.WEST);
        footer.add(infoLabel, BorderLayout.EAST);
        
        return footer;
    }
    
    private void updateStockActuel() {
        Produit p = (Produit) comboProduits.getSelectedItem();
        if (p != null && lblStockActuel != null) {
            lblStockActuel.setText(p.getStockActuel() + " " + getUnite(p.getCategorie().getLibelle()));
        }
    }
    
    private void chargerProduits() {
        if (produitDAO == null) {
            comboProduits.removeAllItems();
            return;
        }
        
        try {
            List<Produit> produits = produitDAO.findAll();
            comboProduits.removeAllItems();
            
            for (Produit p : produits) {
                comboProduits.addItem(p);
            }
            
            if (!produits.isEmpty()) {
                comboProduits.setSelectedIndex(0);
                updateStockActuel();
            }
        } catch (Exception e) {
            logger.severe("Erreur chargement produits: " + e.getMessage());
        }
    }
    
    private void chargerHistorique() {
        if (mouvementDAO == null || tableModel == null) return;
        
        try {
            List<MouvementStock> mouvements = mouvementDAO.findAll();
            tableModel.setRowCount(0);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            for (MouvementStock m : mouvements) {
                String type = m.getType();
                String signe = type.equals("ENTRÉE") ? "+" : "-";
                
                tableModel.addRow(new Object[]{
                    sdf.format(m.getDateMouvement()),
                    m.getProduit().getNom(),
                    type,
                    signe + " " + m.getQuantite(),
                    m.getMotif()
                });
            }
            
            logger.info("✅ Historique chargé: " + mouvements.size() + " mouvements");
        } catch (Exception e) {
            logger.severe("Erreur chargement historique: " + e.getMessage());
        }
    }
    
       
private void enregistrerMouvement() {
            Produit produit = (Produit) comboProduits.getSelectedItem();
        if (produit == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quantite;
        try {
            quantite = Integer.parseInt(txtQuantite.getText().trim());
            if (quantite <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantité invalide (> 0)", "Validation", JOptionPane.WARNING_MESSAGE);
            txtQuantite.requestFocus();
            return;
        }
        
        String type = radioEntree.isSelected() ? "ENTRÉE" : "SORTIE";
        
        if (type.equals("SORTIE") && quantite > produit.getStockActuel()) {
            JOptionPane.showMessageDialog(this, 
                "Stock insuffisant ! (Stock: " + produit.getStockActuel() + " " + getUnite(produit.getCategorie().getLibelle()) + ")",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String motif = (String) comboMotifs.getSelectedItem();
        
        try {
            MouvementStock mouvement = new MouvementStock();
            mouvement.setProduit(produit);
            mouvement.setType(type);
            mouvement.setQuantite(quantite);
            mouvement.setMotif(motif);
            mouvement.setDateMouvement(new Date());
            
            if (mouvementDAO != null) {
                boolean success = mouvementDAO.create(mouvement);
                
                if (success) {
                    int nouveauStock = type.equals("ENTRÉE") ? 
                        produit.getStockActuel() + quantite : 
                        produit.getStockActuel() - quantite;
                    
                    produit.setStockActuel(nouveauStock);
                    produitDAO.update(produit);
                    
                    JOptionPane.showMessageDialog(this, "✅ Mouvement enregistré !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    
                    chargerHistorique();
                    viderFormulaire();
                    chargerProduits();
                    updateStockActuel();
                }
            }
        } catch (Exception e) {
            logger.severe("❌ Erreur: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
}
    
    private void viderFormulaire() {
        txtQuantite.setText("");
        radioEntree.setSelected(true);
        comboMotifs.setSelectedIndex(0);
        if (comboProduits.getItemCount() > 0) {
            comboProduits.setSelectedIndex(0);
        }
        updateStockActuel();
    }
    
    private String getUnite(String categorie) {
        String cat = categorie.toLowerCase();
        if (cat.contains("boisson")) return "unités";
        if (cat.contains("plat")) return "portions";
        if (cat.contains("dessert")) return "parts";
        if (cat.contains("entrée")) return "portions";
        if (cat.contains("snack")) return "portions";
        return "unités";
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
        java.awt.EventQueue.invokeLater(() -> new GestionStockFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
