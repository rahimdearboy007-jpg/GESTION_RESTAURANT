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
public class AideContactFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AideContactFrame.class.getName());
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color BORDER_LIGHT = new Color(225, 225, 220);
    /**
     * Creates new form AideContactFrame
     */
    public AideContactFrame() {
        initComponentsCustom();
        setTitle("📞 Contact & Support");
        setSize(500, 450);
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
        
        JLabel titleLabel = new JLabel("📞 CONTACT & SUPPORT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Contenu
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        
        // Titre restaurant
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel restoTitle = new JLabel("🍽️ RESTAURANT DELICE");
        restoTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        restoTitle.setForeground(PRIMARY_COLOR);
        card.add(restoTitle, gbc);
        
        // Adresse
        gbc.gridy = 1;
        JLabel address = new JLabel("12 Rue de la Paix, Lomé, Togo");
        address.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(address, gbc);
        
        // Séparateur
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 20, 10);
        card.add(new JSeparator(), gbc);
        
        // Email
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.gridwidth = 1;
        gbc.gridy = 3; gbc.gridx = 0;
        JLabel emailIcon = new JLabel("📧");
        emailIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        card.add(emailIcon, gbc);
        
        gbc.gridx = 1;
        JLabel email = new JLabel("support@restaurant.tg");
        email.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(email, gbc);
        
        // Téléphone
        gbc.gridy = 4; gbc.gridx = 0;
        JLabel telIcon = new JLabel("📞");
        telIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        card.add(telIcon, gbc);
        
        gbc.gridx = 1;
        JLabel tel = new JLabel("+228 90 12 34 56");
        tel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(tel, gbc);
        
        // WhatsApp
        gbc.gridy = 5; gbc.gridx = 0;
        JLabel wpIcon = new JLabel("💬");
        wpIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        card.add(wpIcon, gbc);
        
        gbc.gridx = 1;
        JLabel wp = new JLabel("+228 90 12 34 56 (WhatsApp)");
        wp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(wp, gbc);
        
        // Séparateur
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 20, 10);
        card.add(new JSeparator(), gbc);
        
        // Horaires
        gbc.gridy = 7;
        gbc.insets = new Insets(12, 10, 5, 10);
        JLabel horaires = new JLabel("🕒 Horaires d'ouverture");
        horaires.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(horaires, gbc);
        
        gbc.gridy = 8;
        gbc.insets = new Insets(5, 10, 5, 10);
        JLabel horairesDetail = new JLabel("Lundi - Vendredi : 8h - 20h");
        horairesDetail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        card.add(horairesDetail, gbc);
        
        gbc.gridy = 9;
        JLabel horairesWeekend = new JLabel("Samedi - Dimanche : 10h - 22h");
        horairesWeekend.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        card.add(horairesWeekend, gbc);
        
        gbc.gridy = 10;
        JLabel delai = new JLabel("⏱️ Délai de réponse : < 24h");
        delai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        delai.setForeground(SUCCESS_COLOR);
        card.add(delai, gbc);
        
        contentPanel.add(card, BorderLayout.CENTER);
        
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
        add(contentPanel, BorderLayout.CENTER);
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
        java.awt.EventQueue.invokeLater(() -> new AideContactFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
