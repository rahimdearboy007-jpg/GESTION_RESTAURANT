/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.gesrestaurant.views;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 *
 * @author rahim
 */
public class AideGuideFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AideGuideFrame.class.getName());
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    /**
     * Creates new form AideGuideFrame
     */
    public AideGuideFrame() {
        initComponentsCustom();
        setTitle("📖 Guide d'utilisation");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void initComponentsCustom() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 250, 250));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📖 GUIDE D'UTILISATION");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Contenu
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setBackground(Color.WHITE);
        textArea.setText(
            "══════════════════════════════════════════════════\n\n" +
            
            "🔐 CONNEXION\n" +
            "────────────\n" +
            "• Login    : votre identifiant (admin ou employé)\n" +
            "• Mot de passe : votre mot de passe personnel\n\n" +
            
            "📦 GESTION DES PRODUITS\n" +
            "──────────────────────\n" +
            "• [➕] Ajouter un nouveau produit\n" +
            "• [✏️] Modifier un produit existant\n" +
            "• [🗑️] Supprimer un produit\n" +
            "• [🔍] Rechercher un produit\n" +
            "• Les produits sont organisés par catégories\n\n" +
            
            "📈 GESTION DU STOCK\n" +
            "──────────────────\n" +
            "• ENTRÉE : ajouter du stock (achat, réapprovisionnement)\n" +
            "• SORTIE : retirer du stock (vente, perte, inventaire)\n" +
            "• Consultation de l'historique des mouvements\n" +
            "• Alertes automatiques quand le stock est bas\n\n" +
            
            "🛒 COMMANDES CLIENTS\n" +
            "───────────────────\n" +
            "• Créer une nouvelle commande\n" +
            "• Ajouter des produits au panier\n" +
            "• Valider la commande (met à jour le stock)\n" +
            "• Imprimer la facture client\n\n" +
            
            "👥 GESTION DES UTILISATEURS\n" +
            "──────────────────────────\n" +
            "• [ADMIN] Créer, modifier, supprimer des comptes\n" +
            "• Attribution des rôles (ADMIN / EMPLOYE)\n" +
            "• Changement de mot de passe\n\n" +
            
            "📊 STATISTIQUES\n" +
            "──────────────\n" +
            "• Chiffre d'affaires (jour, semaine, mois)\n" +
            "• Top produits les plus vendus\n" +
            "• Alertes stock\n\n" +
            
            "══════════════════════════════════════════════════"
        );
        textArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        
        // Bouton Fermer
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        
        JButton closeBtn = new JButton("Fermer");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setBackground(ACCENT_COLOR);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(closeBtn);
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
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
        java.awt.EventQueue.invokeLater(() -> new AideGuideFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
