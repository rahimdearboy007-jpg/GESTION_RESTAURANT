/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.gesrestaurant.views;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import com.gesrestaurant.util.Session;
import com.gesrestaurant.model.Utilisateur;

/**
 *
 * @author rahim
 */
public class AideSupportFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AideSupportFrame.class.getName());
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color BG_PRIMARY = new Color(250, 245, 240);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color BORDER_LIGHT = new Color(225, 225, 220);
    
    private Utilisateur utilisateurConnecte;
    private JLabel lblStatus;
    
    /**
     * Creates new form AideSupportFrame
     */
    public AideSupportFrame() {
        this.utilisateurConnecte = Session.getUtilisateur();
        initComponentsCustom();
        setTitle("❓ Aide & Support");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        
        // Onglet 1 : Guide d'utilisation
        tabbedPane.addTab("📖 Guide d'utilisation", createGuidePanel());
        
        // Onglet 2 : Raccourcis clavier
        tabbedPane.addTab("⌨️ Raccourcis clavier", createRaccourcisPanel());
        
        // Onglet 3 : Contact & Support
        tabbedPane.addTab("📞 Contact & Support", createContactPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // ===== FOOTER =====
        add(createFooterPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("❓ AIDE & SUPPORT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        
        JButton btnFermer = new JButton("✖ Fermer");
        btnFermer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnFermer.setBackground(new Color(255, 255, 255, 30));
        btnFermer.setForeground(Color.WHITE);
        btnFermer.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnFermer.setFocusPainted(false);
        btnFermer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFermer.addActionListener(e -> dispose());
        
        header.add(title, BorderLayout.WEST);
        header.add(btnFermer, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createGuidePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setBackground(BG_CARD);
        textArea.setText(
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            
            "🔐 CONNEXION\n" +
            "────────────\n" +
            "• Login    : votre identifiant (admin ou employé)\n" +
            "• Mot de passe : votre mot de passe personnel\n\n" +
            
            "📦 GESTION DES PRODUITS\n" +
            "──────────────────────\n" +
            "• [➕] Ajouter un nouveau produit\n" +
            "• [✏️] Modifier un produit existant\n" +
            "• [🗑️] Supprimer un produit\n" +
            "• [🔍] Rechercher un produit\n\n" +
            
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
            "• Consulter l'historique des commandes\n\n" +
            
            "👥 GESTION DES UTILISATEURS\n" +
            "──────────────────────────\n" +
            "• [ADMIN] Créer, modifier, supprimer des comptes\n" +
            "• Attribution des rôles (ADMIN / EMPLOYE)\n" +
            "• Changement de mot de passe\n\n" +
            
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRaccourcisPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        
        // Titre
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("⌨️ RACCOURCIS CLAVIER");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(PRIMARY_COLOR);
        card.add(title, gbc);
        
        // Séparateur
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 15, 20, 15);
        card.add(new JSeparator(), gbc);
        
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.gridwidth = 1;
        
        String[][] raccourcis = {
            {"Ctrl + N", "Nouvelle commande"},
            {"Ctrl + S", "Enregistrer"},
            {"Ctrl + F", "Rechercher"},
            {"Ctrl + P", "Imprimer facture"},
            {"F1", "Aide"},
            {"Alt + 1", "Dashboard"},
            {"Alt + 2", "Produits & Catégories"},
            {"Alt + 3", "Mouvements de Stock"},
            {"Alt + 4", "Commandes Clients"},
            {"Alt + 5", "Statistiques"},
            {"Alt + 6", "Paramètres système"},
            {"Alt + F4", "Quitter l'application"}
        };
        
        for (int i = 0; i < raccourcis.length; i++) {
            gbc.gridy = i + 2;
            gbc.gridx = 0;
            
            JLabel toucheLabel = new JLabel(raccourcis[i][0]);
            toucheLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            toucheLabel.setForeground(ACCENT_COLOR);
            card.add(toucheLabel, gbc);
            
            gbc.gridx = 1;
            JLabel descLabel = new JLabel(raccourcis[i][1]);
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            card.add(descLabel, gbc);
        }
        
        panel.add(card, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createContactPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        
        // Titre restaurant
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel restoTitle = new JLabel("🍽️ RESTAURANT DELICE");
        restoTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        restoTitle.setForeground(PRIMARY_COLOR);
        card.add(restoTitle, gbc);
        
        // Adresse
        gbc.gridy = 1;
        JLabel address = new JLabel("12 Rue de la Paix, Lomé, Togo");
        address.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(address, gbc);
        
        // Séparateur
        gbc.gridy = 2;
        gbc.insets = new Insets(25, 15, 25, 15);
        card.add(new JSeparator(), gbc);
        
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridwidth = 1;
        
        // Email
        gbc.gridy = 3; gbc.gridx = 0;
        JLabel emailIcon = new JLabel("📧");
        emailIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        card.add(emailIcon, gbc);
        
        gbc.gridx = 1;
        JLabel email = new JLabel("support@restaurant.tg");
        email.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(email, gbc);
        
        // Téléphone
        gbc.gridy = 4; gbc.gridx = 0;
        JLabel telIcon = new JLabel("📞");
        telIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        card.add(telIcon, gbc);
        
        gbc.gridx = 1;
        JLabel tel = new JLabel("+228 90 12 34 56");
        tel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(tel, gbc);
        
        // WhatsApp
        gbc.gridy = 5; gbc.gridx = 0;
        JLabel wpIcon = new JLabel("💬");
        wpIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        card.add(wpIcon, gbc);
        
        gbc.gridx = 1;
        JLabel wp = new JLabel("+228 90 12 34 56 (WhatsApp)");
        wp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(wp, gbc);
        
        // Séparateur
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 15, 25, 15);
        card.add(new JSeparator(), gbc);
        
        // Horaires
        gbc.insets = new Insets(15, 15, 5, 15);
        gbc.gridy = 7;
        JLabel horaires = new JLabel("🕒 Horaires d'ouverture");
        horaires.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(horaires, gbc);
        
        gbc.gridy = 8;
        gbc.insets = new Insets(5, 15, 5, 15);
        JLabel horairesDetail = new JLabel("Lundi - Vendredi : 8h00 - 20h00");
        horairesDetail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        card.add(horairesDetail, gbc);
        
        gbc.gridy = 9;
        JLabel horairesWeekend = new JLabel("Samedi - Dimanche : 10h00 - 22h00");
        horairesWeekend.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        card.add(horairesWeekend, gbc);
        
        gbc.gridy = 10;
        gbc.insets = new Insets(15, 15, 15, 15);
        JLabel delai = new JLabel("⏱️ Délai de réponse : < 24h");
        delai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        delai.setForeground(SUCCESS_COLOR);
        card.add(delai, gbc);
        
        panel.add(card, BorderLayout.CENTER);
        
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
        
        JLabel infoLabel = new JLabel("❓ Aide & Support • v1.0");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(150, 150, 150));
        
        footer.add(lblStatus, BorderLayout.WEST);
        footer.add(infoLabel, BorderLayout.EAST);
        
        return footer;
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
        java.awt.EventQueue.invokeLater(() -> new AideSupportFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
