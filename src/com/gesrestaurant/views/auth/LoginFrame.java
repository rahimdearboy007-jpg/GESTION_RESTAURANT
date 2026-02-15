/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.gesrestaurant.views.auth;

/**
 *
 * @author rahim
 */
import com.gesrestaurant.controller.AuthController;
import com.gesrestaurant.views.MainMenuFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.gesrestaurant.model.Utilisateur;
import com.gesrestaurant.util.Session;

public class LoginFrame extends javax.swing.JFrame {
    private JTextField txtLogin;
    private JPasswordField txtMotDePasse;
    private JButton btnConnexion;
    private JButton btnQuitter;
    private JLabel lblMessage;
    private JLabel lblTitre;
    private AuthController authController;
    private JPasswordField txtPassword;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginFrame.class.getName());

    /**
     * Creates new form LoginFrame
     */
    public LoginFrame() {
        initComponents();
        authController = new AuthController();
        
        // Configuration de la fenêtre
        configurerFenetre();
        
        // Création des composants
        initialiserComposants();
        
        // Organisation du layout
        organiserLayout();
        
        // Gestion des événements
        configurerEvenements();
    }
    
        private void configurerFenetre() {
        setTitle("Connexion - Gestion Restaurant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Taille optimale pour un écran de connexion
        setSize(400, 350);
        
        // Centrer la fenêtre
        setLocationRelativeTo(null);
    }
            private void initialiserComposants() {
        // Titre
        lblTitre = new JLabel("GESTION RESTAURANT");
        lblTitre.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitre.setForeground(new Color(0, 102, 204));
        lblTitre.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Champ Login
        JLabel lblLogin = new JLabel("Login :");
        lblLogin.setFont(new Font("Arial", Font.PLAIN, 12));
        txtLogin = new JTextField(20);
        txtLogin.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Champ Mot de passe
        JLabel lblMotDePasse = new JLabel("Mot de passe :");
        lblMotDePasse.setFont(new Font("Arial", Font.PLAIN, 12));
        txtMotDePasse = new JPasswordField(20);
        txtMotDePasse.setFont(new Font("Arial", Font.PLAIN, 12));
        txtMotDePasse.setEchoChar('•'); // Masquage du mot de passe
        
        txtMotDePasse = new JPasswordField(20);
        txtMotDePasse.setFont(new Font("Arial", Font.PLAIN, 12));
        txtMotDePasse.setEchoChar('•');
    
    // 👇 TRÈS IMPORTANT - Les deux références pointent vers le même objet
        txtPassword = txtMotDePasse;
        
        // Boutons
        btnConnexion = new JButton("Se connecter");
        btnConnexion.setBackground(new Color(0, 102, 204));
        btnConnexion.setForeground(Color.WHITE);
        btnConnexion.setFont(new Font("Arial", Font.BOLD, 12));
        btnConnexion.setFocusPainted(false);
        
        btnQuitter = new JButton("Quitter");
        btnQuitter.setBackground(new Color(204, 0, 0));
        btnQuitter.setForeground(Color.WHITE);
        btnQuitter.setFont(new Font("Arial", Font.BOLD, 12));
        btnQuitter.setFocusPainted(false);
        
        // Message d'information/erreur
        lblMessage = new JLabel(" ");
        lblMessage.setFont(new Font("Arial", Font.ITALIC, 11));
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
    }
             private void organiserLayout() {
        // Panel principal avec BorderLayout
        setLayout(new BorderLayout(10, 10));
        
        // Panel du titre (Nord)
        JPanel panelTitre = new JPanel();
        panelTitre.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        panelTitre.add(lblTitre);
        add(panelTitre, BorderLayout.NORTH);
        
        // Panel des champs de saisie (Centre)
        JPanel panelChamps = new JPanel(new GridBagLayout());
        panelChamps.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Login
        gbc.gridx = 0; gbc.gridy = 0;
        panelChamps.add(new JLabel("Login :"), gbc);
        
        gbc.gridx = 1;
        panelChamps.add(txtLogin, gbc);
        
        // Mot de passe
        gbc.gridx = 0; gbc.gridy = 1;
        panelChamps.add(new JLabel("Mot de passe :"), gbc);
        
        gbc.gridx = 1;
        panelChamps.add(txtMotDePasse, gbc);
        
        // Espace
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panelChamps.add(Box.createVerticalStrut(10), gbc);
        
        // Boutons
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBoutons.add(btnConnexion);
        panelBoutons.add(btnQuitter);
        
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panelChamps.add(panelBoutons, gbc);
        
        // Message
        gbc.gridy = 4;
        panelChamps.add(lblMessage, gbc);
        
        add(panelChamps, BorderLayout.CENTER);
        
        // Info de test (à retirer en production)
        JPanel panelInfo = new JPanel();
        panelInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(panelInfo, BorderLayout.SOUTH);
    }
             
    private void configurerEvenements() {
    // ✅ BOUTON CONNEXION - Utilise la méthode qui récupère l'utilisateur complet
    btnConnexion.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            btnLoginActionPerformed(e);
        }
    });
    
    // ✅ TOUCHE ENTREE dans le champ mot de passe - Même comportement
    txtMotDePasse.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            btnLoginActionPerformed(e);
        }
    });
    
    // ✅ BOUTON QUITTER
    btnQuitter.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            quitterApplication();
        }
    });
    
    // ✅ FERMETURE FENETRE
    addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            quitterApplication();
        }
    });
}
   
        private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        String login = txtLogin.getText().trim();
        String password = new String(txtPassword.getPassword());

        AuthController authController = new AuthController();

        // ✅ Vérifier l'authentification ET récupérer l'utilisateur
        Utilisateur utilisateur = authController.getUtilisateurAuthentifie(login, password);

        if (utilisateur != null) {
            // ✅ STOCKER L'UTILISATEUR DANS LA SESSION
            Session.setUtilisateur(utilisateur);

            JOptionPane.showMessageDialog(this, 
                "✅ Connexion réussie !\nBienvenue " + utilisateur.getLogin() + 
                " (" + utilisateur.getRole() + ")", 
                "Succès", JOptionPane.INFORMATION_MESSAGE);

            dispose();
            new MainMenuFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "❌ Login ou mot de passe incorrect", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    /**
     * Affiche un message à l'utilisateur
     * 
     * @param message Le message à afficher
     * @param couleur La couleur du message
     */
    private void afficherMessage(String message, Color couleur) {
        lblMessage.setForeground(couleur);
        lblMessage.setText(message);
    }
    
    /**
     * Quitte l'application avec confirmation
     */
    private void quitterApplication() {
        int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous vraiment quitter l'application ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmation == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }


    

    
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
    // Dans LoginFrame.java, modifie temporairement le main() :
public static void main(String args[]) {
    // ... ton code existant ...
    
    java.awt.EventQueue.invokeLater(() -> {
        LoginFrame frame = new LoginFrame();
        frame.setVisible(true);
        
        // AUTO-TEST (optionnel)
        // Remplit automatiquement les champs pour tester
        frame.txtLogin.setText("RVH1M");
        frame.txtMotDePasse.setText("RVH1M");
        //frame.btnConnexion.doClick(); // Décommente pour auto-clic
    });
}
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
