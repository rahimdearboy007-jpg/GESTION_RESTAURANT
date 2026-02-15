/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.gesrestaurant.views;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import com.gesrestaurant.model.*;
import com.gesrestaurant.dao.*;
import com.gesrestaurant.util.DatabaseConnection;
import com.gesrestaurant.util.Session;


/**
 *
 * @author rahim
 */
public class ProfilUtilisateurFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ProfilUtilisateurFrame.class.getName());
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BG_PRIMARY = new Color(245, 245, 242);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color BORDER_LIGHT = new Color(225, 225, 220);
    
    // ===== DAO =====
    private UtilisateurDAO utilisateurDAO;
    
    // ===== UTILISATEUR CONNECTÉ =====
    private Utilisateur utilisateurConnecte;
    
    // ===== COMPOSANTS UI =====
    private JLabel lblLogin, lblRole, lblDateCreation;
    private JPasswordField txtAncienMdp, txtNouveauMdp, txtConfirmationMdp;
    private JLabel lblStatus;
    /**
     * Creates new form ProfilUtilisateurFrame
     */
    public ProfilUtilisateurFrame() {
        this.utilisateurConnecte = Session.getUtilisateur();
        
        if (utilisateurConnecte == null) {
            JOptionPane.showMessageDialog(null,
                "❌ Aucun utilisateur connecté !",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        initDAO();
        initComponentsCustom();
        setTitle("👤 Mon Profil - " + utilisateurConnecte.getLogin());
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void initDAO() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            this.utilisateurDAO = new UtilisateurDAO(conn);
            logger.info("✅ Connexion BDD établie");
        } catch (Exception e) {
            logger.severe("❌ Erreur connexion BDD: " + e.getMessage());
        }
    }
    
    private void initComponentsCustom() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_PRIMARY);
        
        // ===== HEADER =====
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // ===== CONTENU PRINCIPAL =====
        add(createMainPanel(), BorderLayout.CENTER);
        
        // ===== FOOTER =====
        add(createFooterPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("👤 MON PROFIL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        
        JButton btnRetour = new JButton("🔙 Retour");
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
    
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ===== 1. CARTE INFORMATIONS =====
        mainPanel.add(createInfoCard());
        mainPanel.add(Box.createVerticalStrut(20));
        
        // ===== 2. CARTE CHANGEMENT MOT DE PASSE =====
        mainPanel.add(createPasswordCard());
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_PRIMARY);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private JPanel createInfoCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Titre
        JLabel title = new JLabel("📋 INFORMATIONS PERSONNELLES");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(PRIMARY_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(20));
        
        // Grille d'informations
        JPanel infoGrid = new JPanel(new GridLayout(0, 2, 10, 15));
        infoGrid.setOpaque(false);
        infoGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Login
        infoGrid.add(new JLabel("Identifiant :"));
        lblLogin = new JLabel(utilisateurConnecte.getLogin());
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLogin.setForeground(PRIMARY_COLOR);
        infoGrid.add(lblLogin);
        
        // Rôle
        infoGrid.add(new JLabel("Rôle :"));
        lblRole = new JLabel(utilisateurConnecte.getRole());
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRole.setForeground(utilisateurConnecte.isAdmin() ? DANGER_COLOR : SUCCESS_COLOR);
        infoGrid.add(lblRole);
        
        // ID Utilisateur
        infoGrid.add(new JLabel("ID :"));
        JLabel lblId = new JLabel(String.valueOf(utilisateurConnecte.getId()));
        lblId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoGrid.add(lblId);
        
        // Statut du compte
        infoGrid.add(new JLabel("Statut :"));
        JLabel lblStatut = new JLabel("Actif");
        lblStatut.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatut.setForeground(SUCCESS_COLOR);
        infoGrid.add(lblStatut);
        
        card.add(infoGrid);
        
        return card;
    }
    
    private JPanel createPasswordCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Titre
        JLabel title = new JLabel("🔐 CHANGER LE MOT DE PASSE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(PRIMARY_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(20));
        
        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Ancien mot de passe
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblAncien = new JLabel("Ancien mot de passe *");
        lblAncien.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAncien.setForeground(PRIMARY_COLOR);
        formPanel.add(lblAncien, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtAncienMdp = new JPasswordField();
        txtAncienMdp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtAncienMdp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtAncienMdp, gbc);
        
        // Nouveau mot de passe
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblNouveau = new JLabel("Nouveau mot de passe *");
        lblNouveau.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNouveau.setForeground(PRIMARY_COLOR);
        formPanel.add(lblNouveau, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtNouveauMdp = new JPasswordField();
        txtNouveauMdp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNouveauMdp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtNouveauMdp, gbc);
        
        // Confirmation
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblConfirmation = new JLabel("Confirmer le mot de passe *");
        lblConfirmation.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblConfirmation.setForeground(PRIMARY_COLOR);
        formPanel.add(lblConfirmation, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtConfirmationMdp = new JPasswordField();
        txtConfirmationMdp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtConfirmationMdp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtConfirmationMdp, gbc);
        
        // Règles de mot de passe
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);
        JLabel lblRegles = new JLabel("📌 Le mot de passe doit contenir au moins 4 caractères");
        lblRegles.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblRegles.setForeground(new Color(150, 150, 150));
        formPanel.add(lblRegles, gbc);
        
        card.add(formPanel);
        card.add(Box.createVerticalStrut(20));
        
        // Bouton de validation
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton btnChanger = new JButton("✅ Changer le mot de passe");
        btnChanger.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnChanger.setBackground(SUCCESS_COLOR);
        btnChanger.setForeground(Color.WHITE);
        btnChanger.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        btnChanger.setFocusPainted(false);
        btnChanger.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChanger.addActionListener(e -> changerMotDePasse());
        
        buttonPanel.add(btnChanger);
        card.add(buttonPanel);
        
        return card;
    }
    
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(240, 240, 240));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        lblStatus = new JLabel("✅ Connecté en tant que: " + utilisateurConnecte.getLogin() + " (" + utilisateurConnecte.getRole() + ")");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(100, 100, 100));
        
        JLabel infoLabel = new JLabel("👤 Gérez vos informations personnelles");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(150, 150, 150));
        
        footer.add(lblStatus, BorderLayout.WEST);
        footer.add(infoLabel, BorderLayout.EAST);
        
        return footer;
    }
    
    private void changerMotDePasse() {
        // Récupérer les mots de passe
        String ancienMdp = new String(txtAncienMdp.getPassword());
        String nouveauMdp = new String(txtNouveauMdp.getPassword());
        String confirmation = new String(txtConfirmationMdp.getPassword());
        
        // VALIDATION 1 : Ancien mot de passe
        if (ancienMdp.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez saisir votre ancien mot de passe",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            txtAncienMdp.requestFocus();
            return;
        }
        
        // Vérifier que l'ancien mot de passe est correct
        if (!ancienMdp.equals(utilisateurConnecte.getMotDePasse())) {
            JOptionPane.showMessageDialog(this,
                "❌ Ancien mot de passe incorrect",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            txtAncienMdp.setText("");
            txtAncienMdp.requestFocus();
            return;
        }
        
        // VALIDATION 2 : Nouveau mot de passe
        if (nouveauMdp.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez saisir un nouveau mot de passe",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            txtNouveauMdp.requestFocus();
            return;
        }
        
        if (nouveauMdp.length() < 4) {
            JOptionPane.showMessageDialog(this,
                "❌ Le mot de passe doit contenir au moins 4 caractères",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            txtNouveauMdp.setText("");
            txtConfirmationMdp.setText("");
            txtNouveauMdp.requestFocus();
            return;
        }
        
        // VALIDATION 3 : Confirmation
        if (confirmation.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez confirmer votre nouveau mot de passe",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            txtConfirmationMdp.requestFocus();
            return;
        }
        
        if (!nouveauMdp.equals(confirmation)) {
            JOptionPane.showMessageDialog(this,
                "❌ La confirmation ne correspond pas au nouveau mot de passe",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            txtConfirmationMdp.setText("");
            txtConfirmationMdp.requestFocus();
            return;
        }
        
        // VALIDATION 4 : Nouveau mot de passe différent de l'ancien
        if (nouveauMdp.equals(ancienMdp)) {
            JOptionPane.showMessageDialog(this,
                "⚠️ Le nouveau mot de passe doit être différent de l'ancien",
                "Attention", JOptionPane.WARNING_MESSAGE);
            txtNouveauMdp.setText("");
            txtConfirmationMdp.setText("");
            txtNouveauMdp.requestFocus();
            return;
        }
        
        // Mise à jour dans la BDD
        try {
            utilisateurConnecte.setMotDePasse(nouveauMdp);
            
            if (utilisateurDAO.update(utilisateurConnecte)) {
                JOptionPane.showMessageDialog(this,
                    "✅ Mot de passe modifié avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                // Vider les champs
                txtAncienMdp.setText("");
                txtNouveauMdp.setText("");
                txtConfirmationMdp.setText("");
                
                logger.info("✅ Mot de passe changé pour l'utilisateur: " + utilisateurConnecte.getLogin());
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Erreur lors de la modification du mot de passe",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            logger.severe("❌ Erreur changement mot de passe: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "❌ Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
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
        java.awt.EventQueue.invokeLater(() -> new ProfilUtilisateurFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
