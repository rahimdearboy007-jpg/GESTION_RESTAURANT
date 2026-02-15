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
import java.sql.Connection;
import com.gesrestaurant.model.*;
import com.gesrestaurant.dao.*;
import com.gesrestaurant.util.DatabaseConnection;
import com.gesrestaurant.util.Session;
import java.util.List;  // ← MANQUANT !
import com.gesrestaurant.dao.UtilisateurDAO;


/**
 *
 * @author rahim
 */
public class GestionUtilisateursFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GestionUtilisateursFrame.class.getName());
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BG_PRIMARY = new Color(245, 245, 242);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color BORDER_LIGHT = new Color(225, 225, 220);
    
    // ===== DAO =====
    private UtilisateurDAO utilisateurDAO;
    
    // ===== COMPOSANTS UI =====
    private JTable tableUtilisateurs;
    private DefaultTableModel tableModel;
    private JTextField txtLogin, txtMotDePasse;
    private JComboBox<String> comboRole;
    private JLabel lblStatus;
    
    // ===== UTILISATEUR CONNECTÉ =====
    private Utilisateur utilisateurConnecte;
    /**
     * Creates new form GestionUtilisateursFrame
     */
    public GestionUtilisateursFrame() {
        // Vérifier que l'utilisateur est ADMIN
        this.utilisateurConnecte = Session.getUtilisateur();
        if (utilisateurConnecte == null || !utilisateurConnecte.isAdmin()) {
            JOptionPane.showMessageDialog(null,
                "❌ Accès non autorisé !\nCette interface est réservée aux administrateurs.",
                "Permission refusée", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        initDAO();
        initComponentsCustom();
        setTitle("👥 Gestion des Utilisateurs");
        setSize(1000, 600);
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
    
    // ===== EDITOR POUR LES BOUTONS D'ACTION =====
    // ===== EDITOR POUR LES BOUTONS D'ACTION =====
class ActionsEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel;
    private JButton btnEdit;
    private JButton btnDelete;
    private int currentId;
    private String currentLogin;
    private String currentRole;
    
    public ActionsEditor() {
        panel = new JPanel(new GridLayout(1, 2, 3, 0));  // ← Espace réduit
        panel.setOpaque(true);
        
        // ✅ BOUTON MODIFIER - COMPACT
        btnEdit = new JButton("Modif");
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnEdit.setBackground(ACCENT_COLOR);
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        btnEdit.setFocusPainted(false);
        btnEdit.setMargin(new Insets(2, 4, 2, 4));
        btnEdit.addActionListener(e -> {
            modifierUtilisateur(currentId, currentLogin, currentRole);
            fireEditingStopped();
        });
        
        // ✅ BOUTON SUPPRIMER - COMPACT
        btnDelete = new JButton("Suppr");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnDelete.setBackground(DANGER_COLOR);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        btnDelete.setFocusPainted(false);
        btnDelete.setMargin(new Insets(2, 4, 2, 4));
        btnDelete.addActionListener(e -> {
            supprimerUtilisateur(currentId, currentLogin, currentRole);
            fireEditingStopped();
        });
        
        panel.add(btnEdit);
        panel.add(btnDelete);
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        currentId = (int) table.getValueAt(row, 0);
        currentLogin = (String) table.getValueAt(row, 1);
        currentRole = (String) table.getValueAt(row, 2);
        
        panel.setBackground(table.getSelectionBackground());
        return panel;
    }
    
    @Override
    public Object getCellEditorValue() {
        return "Modif | Suppr";
    }
}
    
    // ===== RENDERER POUR LES BOUTONS D'ACTION =====
    // ===== RENDERER POUR LES BOUTONS D'ACTION =====
    // ===== RENDERER POUR LES BOUTONS D'ACTION =====
class ActionsRenderer extends JPanel implements TableCellRenderer {
    
    private JButton btnEdit;
    private JButton btnDelete;
    
    public ActionsRenderer() {
        setLayout(new GridLayout(1, 2, 3, 0));  // ← Espace réduit à 3px
        setOpaque(true);
        
        // ✅ BOUTON MODIFIER - COMPACT
        btnEdit = new JButton("Modif");
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 10));  // ← Police 10px
        btnEdit.setBackground(ACCENT_COLOR);
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));  // ← Padding réduit
        btnEdit.setFocusPainted(false);
        btnEdit.setMargin(new Insets(2, 4, 2, 4));  // ← Marge interne réduite
        
        // ✅ BOUTON SUPPRIMER - COMPACT
        btnDelete = new JButton("Suppr");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 10));  // ← Police 10px
        btnDelete.setBackground(DANGER_COLOR);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));  // ← Padding réduit
        btnDelete.setFocusPainted(false);
        btnDelete.setMargin(new Insets(2, 4, 2, 4));  // ← Marge interne réduite
        
        add(btnEdit);
        add(btnDelete);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(row % 2 == 0 ? BG_CARD : new Color(250, 250, 250));
        }
        
        return this;
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
        
        // ===== CHARGEMENT DONNÉES =====
        chargerUtilisateurs();
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("👥 GESTION DES UTILISATEURS");
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
    
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BG_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ===== PANEL GAUCHE - Liste des utilisateurs =====
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(BG_CARD);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel listTitle = new JLabel("📋 Liste des utilisateurs");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        listTitle.setForeground(PRIMARY_COLOR);
        leftPanel.add(listTitle, BorderLayout.NORTH);
        
        // Tableau des utilisateurs
        String[] columns = {"ID", "Login", "Rôle", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Seule la colonne Actions est éditable
            }
        };
        
        tableUtilisateurs = new JTable(tableModel);
        tableUtilisateurs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableUtilisateurs.setRowHeight(40);
        tableUtilisateurs.setShowGrid(true);
        tableUtilisateurs.setGridColor(BORDER_LIGHT);
        tableUtilisateurs.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableUtilisateurs.getTableHeader().setBackground(new Color(245, 245, 245));
        tableUtilisateurs.getTableHeader().setForeground(PRIMARY_COLOR);
        
        // Renderer pour la colonne Actions
        tableUtilisateurs.getColumn("Actions").setCellRenderer(new ActionsRenderer());
        tableUtilisateurs.getColumn("Actions").setCellEditor(new ActionsEditor());
        
        JScrollPane scrollPane = new JScrollPane(tableUtilisateurs);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        // ===== PANEL DROIT - Formulaire =====
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setBackground(BG_CARD);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        rightPanel.setPreferredSize(new Dimension(350, 0));
        
        JLabel formTitle = new JLabel("➕ AJOUTER UN UTILISATEUR");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(PRIMARY_COLOR);
        rightPanel.add(formTitle, BorderLayout.NORTH);
        
        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Login
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblLogin = new JLabel("Login *");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLogin.setForeground(PRIMARY_COLOR);
        formPanel.add(lblLogin, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtLogin = new JTextField();
        txtLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLogin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtLogin, gbc);
        
        // Mot de passe
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblMotDePasse = new JLabel("Mot de passe *");
        lblMotDePasse.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMotDePasse.setForeground(PRIMARY_COLOR);
        formPanel.add(lblMotDePasse, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtMotDePasse = new JTextField();
        txtMotDePasse.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMotDePasse.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtMotDePasse, gbc);
        
        // Rôle
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblRole = new JLabel("Rôle *");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRole.setForeground(PRIMARY_COLOR);
        formPanel.add(lblRole, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        comboRole = new JComboBox<>();
        comboRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboRole.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        comboRole.addItem("EMPLOYE");
        comboRole.addItem("ADMIN");
        formPanel.add(comboRole, gbc);
        
        // Bouton Ajouter
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        
        JButton btnAjouter = new JButton("➕ Ajouter l'utilisateur");
        btnAjouter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAjouter.setBackground(SUCCESS_COLOR);
        btnAjouter.setForeground(Color.WHITE);
        btnAjouter.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnAjouter.setFocusPainted(false);
        btnAjouter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAjouter.addActionListener(e -> ajouterUtilisateur());
        formPanel.add(btnAjouter, gbc);
        
        rightPanel.add(formPanel, BorderLayout.CENTER);
        
        // SplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(550);
        splitPane.setDividerSize(5);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setBackground(BG_PRIMARY);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(240, 240, 240));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        lblStatus = new JLabel("✅ Connecté en tant que: " + utilisateurConnecte.getLogin() + " (ADMIN)");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(100, 100, 100));
        
        JLabel infoLabel = new JLabel("👥 Gestion des utilisateurs • Section 2.7 du sujet");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(150, 150, 150));
        
        footer.add(lblStatus, BorderLayout.WEST);
        footer.add(infoLabel, BorderLayout.EAST);
        
        return footer;
    }
    
    private void chargerUtilisateurs() {
    if (utilisateurDAO == null) return;
    
    try {
        List<Utilisateur> utilisateurs = utilisateurDAO.findAll();
        tableModel.setRowCount(0);
        
        for (Utilisateur u : utilisateurs) {
            tableModel.addRow(new Object[]{
                u.getId(),
                u.getLogin(),
                u.getRole(),
                "✏️ Modifier | 🗑️ Supprimer"  // ← GARDE ÇA, C'EST CORRECT !
            });
        }
    } catch (Exception e) {
        logger.severe("Erreur chargement utilisateurs: " + e.getMessage());
    }
}
    
    private void ajouterUtilisateur() {
        String login = txtLogin.getText().trim();
        String password = txtMotDePasse.getText().trim();
        String role = (String) comboRole.getSelectedItem();
        
        // Validation
        if (login.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le login est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le mot de passe est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "Le mot de passe doit contenir au moins 4 caractères", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Vérifier si le login existe déjà
            if (utilisateurDAO.loginExists(login)) {
                JOptionPane.showMessageDialog(this, "Ce login est déjà utilisé", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Créer l'utilisateur
            Utilisateur nouvelUtilisateur = new Utilisateur();
            nouvelUtilisateur.setLogin(login);
            nouvelUtilisateur.setMotDePasse(password);
            nouvelUtilisateur.setRole(role);
            
            if (utilisateurDAO.create(nouvelUtilisateur)) {
                JOptionPane.showMessageDialog(this, "✅ Utilisateur ajouté avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerUtilisateurs();
                viderFormulaire();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Erreur lors de l'ajout", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            logger.severe("Erreur ajout: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void modifierUtilisateur(int id, String login, String role) {
        // Boîte de dialogue de modification
        JTextField txtNewLogin = new JTextField(login);
        JPasswordField txtNewPassword = new JPasswordField();
        JComboBox<String> comboNewRole = new JComboBox<>(new String[]{"EMPLOYE", "ADMIN"});
        comboNewRole.setSelectedItem(role);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Login:"));
        panel.add(txtNewLogin);
        panel.add(new JLabel("Nouveau mot de passe:"));
        panel.add(txtNewPassword);
        panel.add(new JLabel("Rôle:"));
        panel.add(comboNewRole);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "✏️ Modifier l'utilisateur", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Utilisateur utilisateur = utilisateurDAO.read(id);
                if (utilisateur != null) {
                    utilisateur.setLogin(txtNewLogin.getText().trim());
                    
                    String newPassword = new String(txtNewPassword.getPassword());
                    if (!newPassword.isEmpty()) {
                        if (newPassword.length() < 4) {
                            JOptionPane.showMessageDialog(this, "Le mot de passe doit contenir au moins 4 caractères", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        utilisateur.setMotDePasse(newPassword);
                    }
                    
                    utilisateur.setRole((String) comboNewRole.getSelectedItem());
                    
                    if (utilisateurDAO.update(utilisateur)) {
                        JOptionPane.showMessageDialog(this, "✅ Utilisateur modifié !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        chargerUtilisateurs();
                    }
                }
            } catch (Exception e) {
                logger.severe("Erreur modification: " + e.getMessage());
            }
        }
    }
    
    private void supprimerUtilisateur(int id, String login, String role) {
        // Empêcher la suppression de soi-même
        if (utilisateurConnecte != null && utilisateurConnecte.getId() == id) {
            JOptionPane.showMessageDialog(this, 
                "❌ Vous ne pouvez pas supprimer votre propre compte !", 
                "Action impossible", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Empêcher la suppression du dernier ADMIN
        if ("ADMIN".equals(role)) {
            try {
                List<Utilisateur> admins = utilisateurDAO.findByRole("ADMIN");
                if (admins.size() <= 1) {
                    JOptionPane.showMessageDialog(this, 
                        "❌ Impossible de supprimer le dernier administrateur !", 
                        "Action impossible", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                logger.severe("Erreur vérification admin: " + e.getMessage());
            }
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment supprimer l'utilisateur '" + login + "' ?",
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (utilisateurDAO.delete(id)) {
                    JOptionPane.showMessageDialog(this, "✅ Utilisateur supprimé !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    chargerUtilisateurs();
                }
            } catch (Exception e) {
                logger.severe("Erreur suppression: " + e.getMessage());
            }
        }
    }
    
    private void viderFormulaire() {
        txtLogin.setText("");
        txtMotDePasse.setText("");
        comboRole.setSelectedIndex(0);
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
        java.awt.EventQueue.invokeLater(() -> new GestionUtilisateursFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
