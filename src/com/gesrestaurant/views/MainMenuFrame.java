/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.gesrestaurant.views;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.Timer;
import java.text.SimpleDateFormat;
import com.gesrestaurant.util.DatabaseConnection;
import com.gesrestaurant.model.*;
import com.gesrestaurant.dao.*;
import com.gesrestaurant.views.*;
import java.sql.Connection;
import com.gesrestaurant.model.Commande;
import java.util.List;
import com.gesrestaurant.util.Session;


/**
 *
 * @author rahim
 */
 
public class MainMenuFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainMenuFrame.class.getName());
    private MainMenuFrame instance;
    private Timer statsTimer;
    private JPanel currentContentPanel;
    private Color PRIMARY_COLOR = new Color(44, 62, 80);
    private Color SECONDARY_COLOR = new Color(231, 76, 60);
    private Color SUCCESS_COLOR = new Color(39, 174, 96);
    private Color WARNING_COLOR = new Color(241, 196, 15);
    private Color ACCENT_COLOR = new Color(52, 152, 219);
    private Connection connection;
    private ProduitDAO produitDAO;
    private CommandeDAO commandeDAO;
    private Color DANGER_COLOR = new Color(231, 76, 60);  // ← AJOUTE AVEC LES AUTRES COULEURS
    private Color BG_PRIMARY = new Color(250, 245, 240);  // Beige crème (fond)
    private Color BG_CARD = new Color(255, 255, 255);     // Blanc pur (cartes)
    private Color BORDER_LIGHT = new Color(230, 220, 210); // Taupe clair (bordures)
    private Utilisateur utilisateurConnecte; 
    
    
    /**
     * Constructeur principal
     */
    public MainMenuFrame() {
        instance = this;
        try {
        this.connection = DatabaseConnection.getConnection();
        CategorieDAO categorieDAO = new CategorieDAO(connection);
        LigneCommandeDAO ligneCommandeDAO = new LigneCommandeDAO(connection);
        
        this.produitDAO = new ProduitDAO(connection, categorieDAO);
        this.commandeDAO = new CommandeDAO(connection, ligneCommandeDAO);
        this.utilisateurConnecte = Session.getUtilisateur();
        
        logger.info("✅ Dashboard connecté à la BDD");
    } catch (Exception e) {
        logger.severe("❌ Erreur connexion dashboard: " + e.getMessage());
    }
        initComponentsCustom();
        configurerFenetre();
        animerEntree();
        demarrerStatsTempsReel();
        configurerRaccourcisClavier(); 
    }
    private void initComponentsCustom() {
    // ============================================
    // 1. CONFIGURATION FONDAMENTALE
    // ============================================
    setTitle("🍽️ Gestion Restaurant - Tableau de Bord");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    getContentPane().setBackground(BG_PRIMARY);
    setLayout(new BorderLayout(0, 0));
    
    // ============================================
    // 2. HEADER PREMIUM AVEC GRADIENT
    // ============================================
    JPanel headerPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(44, 62, 80), 
                getWidth(), 0, new Color(52, 73, 94)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    headerPanel.setLayout(new BorderLayout());
    headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    
    // Logo et titre
    JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
    logoPanel.setOpaque(false);
    
    JLabel logoIcon = new JLabel("🍽️") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(241, 196, 15, 100));
            g2.fillOval(0, 0, getWidth(), getHeight());
            super.paintComponent(g2);
            g2.dispose();
        }
    };
    logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
    logoIcon.setPreferredSize(new Dimension(60, 60));
    
    JPanel titlePanel = new JPanel(new GridLayout(2, 1));
    titlePanel.setOpaque(false);
    
    JLabel appTitle = new JLabel("GESTION RESTAURANT");
    appTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
    appTitle.setForeground(Color.WHITE);
    
    JLabel appSubtitle = new JLabel("Système complet conforme au sujet POO Java");
    appSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    appSubtitle.setForeground(new Color(200, 200, 200));
    
    titlePanel.add(appTitle);
    titlePanel.add(appSubtitle);
    
    logoPanel.add(logoIcon);
    logoPanel.add(titlePanel);
    
    // ============================================
    // INFOS RESTAURANT DANS LE HEADER
    // ============================================
    JPanel restoInfoPanel = new JPanel(new GridLayout(2, 1));
    restoInfoPanel.setOpaque(false);
    restoInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
    
    JLabel restoName = new JLabel("RESTAURANT DELICE");
    restoName.setFont(new Font("Segoe UI", Font.BOLD, 14));
    restoName.setForeground(Color.WHITE);
    
    JLabel restoAddress = new JLabel("12 Rue de la Paix, 75001 Paris • 01 23 45 67 89");
    restoAddress.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    restoAddress.setForeground(new Color(220, 220, 220));
    
    restoInfoPanel.add(restoName);
    restoInfoPanel.add(restoAddress);
    
    logoPanel.add(restoInfoPanel);
    
    // ============================================
    // BARRE D'OUTILS SUPÉRIEURE
    // ============================================
    JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    toolsPanel.setOpaque(false);
    
    // Notification badge
    // ✅ NOTIFICATION - LABEL CLIQUABLE AVEC BADGE
    JPanel notificationPanel = new JPanel(new BorderLayout());  // ✅ SIMPLE ET EFFICACE
    notificationPanel.setOpaque(false);
    notificationPanel.setPreferredSize(new Dimension(40, 40));
    notificationPanel.setOpaque(false);
    notificationPanel.setPreferredSize(new Dimension(40, 40));

    // Icône cloche
    JLabel notificationIcon = new JLabel("🔔");
    notificationIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
    notificationIcon.setForeground(Color.WHITE);
    notificationIcon.setHorizontalAlignment(SwingConstants.CENTER);
    notificationIcon.setVerticalAlignment(SwingConstants.CENTER);
    notificationIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
    notificationIcon.setToolTipText("Notifications");

    // Badge de notification
    int nbAlertes = 0;
    try {
        if (produitDAO != null) {
            nbAlertes = produitDAO.findStockBelowSeuil().size();
        }
    } catch (Exception e) {}

    JLabel notificationBadge = new JLabel(String.valueOf(nbAlertes));
    notificationBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
    notificationBadge.setForeground(Color.WHITE);
    notificationBadge.setBackground(SECONDARY_COLOR);
    notificationBadge.setOpaque(true);
    notificationBadge.setHorizontalAlignment(SwingConstants.CENTER);
    notificationBadge.setVerticalAlignment(SwingConstants.CENTER);
    notificationBadge.setPreferredSize(new Dimension(18, 18));
    notificationBadge.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    notificationBadge.setVisible(nbAlertes > 0);

    // Arrondir le badge
    notificationBadge.setUI(new javax.swing.plaf.basic.BasicLabelUI() {
    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(notificationBadge.getBackground());
        g2.fillOval(0, 0, c.getWidth()-1, c.getHeight()-1);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1));
        g2.drawOval(0, 0, c.getWidth()-1, c.getHeight()-1);
        g2.dispose();
        super.paint(g, c);
    }
});

// Positionner le badge en haut à droite
notificationPanel.setLayout(new BorderLayout());
notificationPanel.add(notificationIcon, BorderLayout.CENTER);
notificationPanel.add(notificationBadge, BorderLayout.NORTH);

// Action du clic
notificationIcon.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        showNotifications();
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        notificationIcon.setForeground(new Color(255, 255, 255, 200));
    }
    @Override
    public void mouseExited(MouseEvent e) {
        notificationIcon.setForeground(Color.WHITE);
    }
});

// Ajouter au toolsPanel
    toolsPanel.add(notificationPanel);
    
    // ✅ UTILISATEUR - DYNAMIQUE SELON CONNEXION
    // ✅ PROFIL UTILISATEUR - LABEL CLIQUABLE AVEC STICKER
JPanel profilPanel = new JPanel(new BorderLayout());
profilPanel.setOpaque(false);
profilPanel.setPreferredSize(new Dimension(50, 50));
profilPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

// Icône de profil avec sticker circulaire
JLabel profilIcon = new JLabel("👤") {
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fond circulaire avec dégradé
        GradientPaint gradient;
        if (utilisateurConnecte != null && utilisateurConnecte.isAdmin()) {
            gradient = new GradientPaint(0, 0, new Color(52, 152, 219), 
                                       getWidth(), getHeight(), new Color(41, 128, 185));
        } else {
            gradient = new GradientPaint(0, 0, new Color(46, 204, 113), 
                                       getWidth(), getHeight(), new Color(39, 174, 96));
        }
        g2.setPaint(gradient);
        g2.fillOval(0, 0, getWidth(), getHeight());
        
        // Bordure blanche
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(0, 0, getWidth()-1, getHeight()-1);
        
        g2.dispose();
        super.paintComponent(g);
    }
};
profilIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
profilIcon.setForeground(Color.WHITE);
profilIcon.setHorizontalAlignment(SwingConstants.CENTER);
profilIcon.setVerticalAlignment(SwingConstants.CENTER);
profilIcon.setPreferredSize(new Dimension(44, 44));

// Texte du rôle (optionnel)
JLabel roleLabel = new JLabel();
if (utilisateurConnecte != null) {
    roleLabel.setText(utilisateurConnecte.getRole());
    roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
    roleLabel.setForeground(utilisateurConnecte.isAdmin() ? new Color(52, 152, 219) : new Color(46, 204, 113));
} else {
    roleLabel.setText("INVITÉ");
    roleLabel.setForeground(Color.GRAY);
}
roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
roleLabel.setVerticalAlignment(SwingConstants.BOTTOM);
roleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));

profilPanel.add(profilIcon, BorderLayout.CENTER);
profilPanel.add(roleLabel, BorderLayout.SOUTH);

// Tooltip avec infos utilisateur
if (utilisateurConnecte != null) {
    profilPanel.setToolTipText("<html><b>" + utilisateurConnecte.getLogin() + "</b><br>" + 
                               utilisateurConnecte.getRole() + " connecté</html>");
} else {
    profilPanel.setToolTipText("Non connecté");
}

// Action du clic - OUVRE LE PROFIL UTILISATEUR
profilPanel.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        if (utilisateurConnecte != null) {
            new ProfilUtilisateurFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(MainMenuFrame.this,
                "❌ Aucun utilisateur connecté",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        profilIcon.setForeground(new Color(255, 255, 255, 220));
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        profilIcon.setForeground(Color.WHITE);
    }
});

// Ajouter au toolsPanel

    
    toolsPanel.add(profilPanel);
    
    // Date/Heure live
    JLabel timeLabel = new JLabel();
    timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    timeLabel.setForeground(Color.WHITE);
    updateTimeLabel(timeLabel);
    
    Timer clockTimer = new Timer(1000, e -> updateTimeLabel(timeLabel));
    clockTimer.start();
    
    // Bouton déconnexion
    // ✅ ICÔNE DE DÉCONNEXION SIMPLE ET ÉLÉGANTE
JPanel logoutPanel = new JPanel(new BorderLayout());
logoutPanel.setOpaque(false);
logoutPanel.setPreferredSize(new Dimension(44, 44));
logoutPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

JLabel logoutIcon = new JLabel("🚪") {
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fond circulaire rouge
        g2.setColor(new Color(231, 76, 60));
        g2.fillOval(0, 0, getWidth(), getHeight());
        
        // Bordure blanche
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(0, 0, getWidth()-1, getHeight()-1);
        
        g2.dispose();
        super.paintComponent(g);
    }
};
logoutIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
logoutIcon.setForeground(Color.WHITE);
logoutIcon.setHorizontalAlignment(SwingConstants.CENTER);
logoutIcon.setVerticalAlignment(SwingConstants.CENTER);

logoutPanel.add(logoutIcon, BorderLayout.CENTER);
logoutPanel.setToolTipText("Déconnexion");

logoutPanel.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        deconnexion();
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        logoutIcon.setForeground(new Color(255, 255, 255, 220));
    }
    @Override
    public void mouseExited(MouseEvent e) {
        logoutIcon.setForeground(Color.WHITE);
    }
});

toolsPanel.add(logoutPanel);
    
    toolsPanel.add(timeLabel);
    toolsPanel.add(Box.createHorizontalStrut(10));
    
    headerPanel.add(logoPanel, BorderLayout.WEST);
    headerPanel.add(toolsPanel, BorderLayout.EAST);
    
    add(headerPanel, BorderLayout.NORTH);
    
    // ============================================
    // 3. SIDEBAR MODERNE (Navigation verticale)
    // ============================================
    // ============================================
// 3. SIDEBAR MODERNE (Navigation verticale)
// ============================================
JPanel sidebarPanel = new JPanel();
sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
sidebarPanel.setBackground(PRIMARY_COLOR);
// ✅ NE PAS FIXER DE PREFERRED SIZE ICI
// sidebarPanel.setPreferredSize(new Dimension(280, getHeight())); ← À SUPPRIMER
sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

// Titre navigation
JLabel navTitle = new JLabel("  NAVIGATION PRINCIPALE");
navTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
navTitle.setForeground(new Color(189, 195, 199));
navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
navTitle.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 0));

sidebarPanel.add(navTitle);
sidebarPanel.add(Box.createVerticalStrut(10));

// Boutons de navigation
String[][] menuItems = {
    {"📊", "Tableau de bord", "dashboard"},
    {"📦", "Produits & Catégories", "produits"},
    {"📈", "Mouvements de Stock", "stock"},
    {"🛒", "Commandes Clients", "commandes"},
    {"📋", "Statistiques", "stats"},
    {"⚙️", "Paramètres système", "parametres"}
};

for (String[] item : menuItems) {
    JButton btn = createNavButton(item[0], item[1], false);
    final String action = item[2];
    btn.addActionListener(e -> showModule(action));
    sidebarPanel.add(btn);
    sidebarPanel.add(Box.createVerticalStrut(5));
}

    // Bouton Aide & Support
    // Bouton Aide & Support
    JButton btnAide = createNavButton("❓", "Aide & Support", false);
    btnAide.addActionListener(e -> showModule("aide"));  // ← AJOUTE CETTE LIGNE !
    sidebarPanel.add(btnAide);
    sidebarPanel.add(Box.createVerticalStrut(5));

    // Section des actions rapides
    JLabel lblQuickActions = new JLabel("  ACTIONS RAPIDES");
    lblQuickActions.setFont(new Font("Segoe UI", Font.BOLD, 12));
    lblQuickActions.setForeground(new Color(189, 195, 199));
    lblQuickActions.setAlignmentX(Component.LEFT_ALIGNMENT);
    lblQuickActions.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 0));

    sidebarPanel.add(lblQuickActions);

    String[][] actionsList = {
        {"🖨️", "Imprimer facture"},
        {"📤", "Exporter rapport"},
        {"🔍", "Recherche avancée"}
    };

    for (String[] action : actionsList) {
        JButton quickBtn = createQuickActionButton(action[0], action[1]);
        quickBtn.addActionListener(e -> executeQuickAction(action[1]));
        sidebarPanel.add(quickBtn);
        sidebarPanel.add(Box.createVerticalStrut(3));
    }

// ✅ WRAPPER SCROLLABLE POUR LA SIDEBAR
    JScrollPane sidebarScroll = new JScrollPane(sidebarPanel);
    sidebarScroll.setBorder(BorderFactory.createEmptyBorder());
    sidebarScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    sidebarScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    sidebarScroll.getVerticalScrollBar().setUnitIncrement(16);
    sidebarScroll.setBackground(PRIMARY_COLOR);

    // ✅ DIMENSION FIXE POUR LA SIDEBAR
    sidebarScroll.setPreferredSize(new Dimension(280, getHeight()));

    add(sidebarScroll, BorderLayout.WEST);

    JButton[] navButtons = new JButton[menuItems.length];
    
    for (int i = 0; i < menuItems.length; i++) {
        navButtons[i] = createNavButton(menuItems[i][0], menuItems[i][1], i == 0);
        final String action = menuItems[i][2];
        navButtons[i].addActionListener(e -> {
            highlightNavButton(navButtons, (JButton) e.getSource());
            showModule(action);
        });
        sidebarPanel.add(navButtons[i]);
        sidebarPanel.add(Box.createVerticalStrut(5));
    }
    
    sidebarPanel.add(Box.createVerticalGlue());
    
    // Section des actions rapides

    
    
    add(sidebarPanel, BorderLayout.WEST);
    
    // ============================================
    // 4. CONTENU PRINCIPAL (Zone centrale)
    // ============================================
    JPanel mainContentPanel = new JPanel(new BorderLayout());
    mainContentPanel.setBackground(Color.WHITE);
    mainContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    
    // Dashboard
    JPanel dashboardPanel = createDashboardPanel();
    mainContentPanel.add(dashboardPanel, BorderLayout.CENTER);
    
    // ============================================
    // 5. PANEL D'INFORMATIONS EN BAS
    // ============================================
    JPanel infoPanel = new JPanel(new BorderLayout());
    infoPanel.setBackground(new Color(240, 240, 240));
    infoPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
        BorderFactory.createEmptyBorder(10, 20, 10, 20)
    ));
    infoPanel.setPreferredSize(new Dimension(getWidth(), 60));
    
    // Informations système
    String userInfo = "✅ Base de données connectée • MySQL • ";
    if (utilisateurConnecte != null) {
        userInfo += "Utilisateur: " + utilisateurConnecte.getLogin() + " (" + utilisateurConnecte.getRole() + ")";
    } else {
        userInfo += "Mode démonstration";
    }
    
    JLabel systemInfo = new JLabel(userInfo);
    systemInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    systemInfo.setForeground(new Color(85, 85, 85));
    
    // Performance indicator
    JPanel perfPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    perfPanel.setOpaque(false);
    
    JLabel perfLabel = new JLabel("Performance: ");
    perfLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    
    JProgressBar perfBar = new JProgressBar(0, 100);
    perfBar.setValue(92);
    perfBar.setForeground(SUCCESS_COLOR);
    perfBar.setPreferredSize(new Dimension(100, 20));
    perfBar.setStringPainted(true);
    perfBar.setString("92%");
    
    JLabel versionLabel = new JLabel("v1.0 • Conforme au sujet POO Java");
    versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    versionLabel.setForeground(new Color(150, 150, 150));
    
    perfPanel.add(perfLabel);
    perfPanel.add(perfBar);
    perfPanel.add(Box.createHorizontalStrut(20));
    perfPanel.add(versionLabel);
    
    infoPanel.add(systemInfo, BorderLayout.WEST);
    infoPanel.add(perfPanel, BorderLayout.EAST);
    
    mainContentPanel.add(infoPanel, BorderLayout.SOUTH);
    
    add(mainContentPanel, BorderLayout.CENTER);
}
    
/**
 * Crée un menu déroulant pour Aide & Support avec 3 sous-menus
 */
    /**
 * Crée un bouton de sous-menu stylisé
 */


    /**
     * Creates new form MainMenuFrame
     */
    private void configurerEvenements(JMenuItem... menuItems) {
        // Gestion des produits
        menuItems[0].addActionListener(e -> {
            JOptionPane.showMessageDialog(instance,
                "📦 MODULE PRODUITS ET CATÉGORIES\n\n" +
                "Conforme à la section 2.1 du sujet :\n" +
                "• Ajouter/modifier/supprimer catégories\n" +
                "• Gérer les produits avec prix et stock\n" +
                "• Alertes de stock faible\n\n" +
                "À implémenter dans GestionProduitsFrame.java",
                "Module Produits", JOptionPane.INFORMATION_MESSAGE);
        });
        
         // Gestion du stock
        menuItems[1].addActionListener(e -> {
            JOptionPane.showMessageDialog(instance,
                "📦 MODULE MOUVEMENTS DE STOCK\n\n" +
                "Conforme à la section 2.2 du sujet :\n" +
                "• Enregistrer entrées/sorties de stock\n" +
                "• Historique des mouvements\n" +
                "• Gestion des seuils d'alerte\n\n" +
                "À implémenter dans GestionStockFrame.java",
                "Module Stock", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Gestion des commandes
        menuItems[2].addActionListener(e -> {
            JOptionPane.showMessageDialog(instance,
                "🛒 MODULE COMMANDES CLIENTS\n\n" +
                "Conforme à la section 2.3 du sujet :\n" +
                "• Créer/modifier/annuler commandes\n" +
                "• Gérer lignes de commande\n" +
                "• Validation et facturation\n\n" +
                "À implémenter dans GestionCommandesFrame.java",
                "Module Commandes", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Statistiques - CA du jour
        menuItems[3].addActionListener(e -> {
            JOptionPane.showMessageDialog(instance,
                "📊 CHIFFRE D'AFFAIRES DU JOUR\n\n" +
                "Conforme à la section 2.4 du sujet\n" +
                "À implémenter dans StatistiquesFrame.java",
                "Statistiques", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Statistiques - CA période
        menuItems[4].addActionListener(e -> {
            JOptionPane.showMessageDialog(instance,
                "📊 CA SUR PÉRIODE\n\n" +
                "Conforme à la section 2.4 du sujet\n" +
                "À implémenter dans StatistiquesFrame.java",
                "Statistiques", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Statistiques - Top produits
        menuItems[5].addActionListener(e -> {
            JOptionPane.showMessageDialog(instance,
                "📊 TOP PRODUITS VENDUS\n\n" +
                "Conforme à la section 2.4 du sujet\n" +
                "À implémenter dans StatistiquesFrame.java",
                "Statistiques", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Statistiques - Alertes stock
        menuItems[6].addActionListener(e -> {
            JOptionPane.showMessageDialog(instance,
                "⚠️ ALERTES STOCK\n\n" +
                "Conforme aux sections 2.1 et 2.2\n" +
                "À implémenter dans StatistiquesFrame.java",
                "Alertes", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Déconnexion
        menuItems[7].addActionListener(e -> deconnexion());
        
        // Quitter
        menuItems[8].addActionListener(e -> quitterApplication());
        
        // Guide utilisation
        menuItems[9].addActionListener(e -> {
            JOptionPane.showMessageDialog(instance,
                "📖 GUIDE D'UTILISATION\n\n" +
                "1. Utilisez les menus pour naviguer\n" +
                "2. Chaque module ouvre une fenêtre dédiée\n" +
                "3. Sauvegardez régulièrement vos données\n" +
                "4. Consultez les statistiques pour le suivi\n\n" +
                "Conforme au sujet POO Java - Architecture MVC",
                "Guide", JOptionPane.INFORMATION_MESSAGE);
        });
        
        menuItems[10].addActionListener(e -> {
            JOptionPane.showMessageDialog(instance,
                "🍽️ GESTION RESTAURANT v1.0\n\n" +
                "Application de gestion complète pour restaurant\n" +
                "Conforme au sujet de travaux pratiques POO Java\n\n" +
                "Fonctionnalités implémentées :\n" +
                "✅ Authentification utilisateur (2.7)\n" +
                "✅ Menu principal avec navigation (4.5)\n" +
                "✅ Architecture MVC respectée\n" +
                "✅ Connexion base de données MySQL\n\n" +
                "Développé avec NetBeans, Java Swing, MySQL",
                "À propos", JOptionPane.INFORMATION_MESSAGE);
        });
    }
        private JButton createToolbarButton(String icon, String tooltip, Color bgColor) {
        JButton button = new JButton(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond arrondi
                if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setToolTipText(tooltip);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 40));
        
        return button;
    }
    private void updateNotificationBadge(JLabel badge) {
    try {
        int nbAlertes = produitDAO != null ? produitDAO.findStockBelowSeuil().size() : 0;
        badge.setText(String.valueOf(nbAlertes));
        badge.setVisible(nbAlertes > 0);
    } catch (Exception e) {
        badge.setVisible(false);
    }
}
    
    
    private JButton createNavButton(String icon, String text, boolean active) {
        JButton button = new JButton("<html><div style='text-align: left; padding-left: 10px;'>" + 
                                    icon + "  " + text + "</div></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (active) {
                    // Bouton actif
                    g2.setColor(new Color(41, 128, 185, 150));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Indicateur gauche
                    g2.setColor(new Color(52, 152, 219));
                    g2.fillRect(0, 0, 5, getHeight());
                } else if (getModel().isRollover()) {
                    // Survol
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(active ? Color.WHITE : new Color(220, 220, 220));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 10));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(280, 50));
        
        return button;
    }
    
        private JButton createQuickActionButton(String icon, String text) {
        JButton button = new JButton(icon + "  " + text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(new Color(200, 200, 200));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 10));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(280, 40));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.WHITE);
                button.setFont(new Font("Segoe UI", Font.BOLD, 13));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(200, 200, 200));
                button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }
        });
        
        return button;
    }
        private JPanel createDashboardPanel() {
        // Panel principal avec BoxLayout vertical
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== 1. EN-TÊTE =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Tableau de bord");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel dateLabel = new JLabel(new SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH).format(new Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(150, 150, 150));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // ===== 2. SECTION COMMANDES =====
        JPanel ordersSection = new JPanel(new BorderLayout());
        ordersSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel ordersTitle = new JLabel("🛒 Commandes en cours");
        ordersTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        ordersTitle.setForeground(PRIMARY_COLOR);
        ordersTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        ordersSection.add(ordersTitle, BorderLayout.NORTH);

        // ✅ GRILLE DYNAMIQUE AVEC NOMBRE VARIABLE DE COLONNES
        JPanel ordersGrid = new JPanel();
        ordersGrid.setBackground(Color.WHITE);
        ordersGrid.setLayout(new GridLayout(0, 3, 15, 15)); // 3 colonnes, lignes illimitées

        try {
            if (commandeDAO != null) {
                List<Commande> commandes = commandeDAO.findByEtat("EN_COURS");

                if (commandes.isEmpty()) {
                    JLabel emptyLabel = new JLabel("   Aucune commande en cours");
                    emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                    emptyLabel.setForeground(new Color(150, 150, 150));
                    ordersGrid.add(emptyLabel);
                } else {
                    // ✅ Affiche TOUTES les commandes (pas de limite !)
                    for (Commande cmd : commandes) {
                        ordersGrid.add(createCommandeCard(cmd));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ordersSection.add(ordersGrid, BorderLayout.CENTER);
        contentPanel.add(ordersSection);
        contentPanel.add(Box.createVerticalStrut(10));

        // ===== 3. SECTION ALERTES =====
        JPanel alertsSection = new JPanel(new BorderLayout());
        alertsSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel alertsTitle = new JLabel("⚠️ Alertes stock");
        alertsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        alertsTitle.setForeground(SECONDARY_COLOR);
        alertsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        alertsSection.add(alertsTitle, BorderLayout.NORTH);

        // ✅ GRILLE DYNAMIQUE POUR ALERTES
        JPanel alertsGrid = new JPanel();
        alertsGrid.setBackground(Color.WHITE);
        alertsGrid.setLayout(new GridLayout(0, 2, 15, 15)); // 2 colonnes, lignes illimitées

        try {
            if (produitDAO != null) {
                List<Produit> alertes = produitDAO.findStockBelowSeuil();

                if (alertes.isEmpty()) {
                    JLabel okLabel = new JLabel("✅ Aucun produit en dessous du seuil");
                    okLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    okLabel.setForeground(SUCCESS_COLOR);
                    alertsGrid.add(okLabel);
                } else {
                    // ✅ Affiche TOUTES les alertes
                    for (Produit p : alertes) {
                        alertsGrid.add(createAlerteCard(p));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        alertsSection.add(alertsGrid, BorderLayout.CENTER);
        contentPanel.add(alertsSection);

        // ===== 4. WRAPPER SCROLLABLE =====
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Vitesse de scroll
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Panel final avec BorderLayout pour contenir le scrollPane
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        return wrapperPanel;
    }

// ===== CARTE COMMANDE PROPRE =====
private JPanel createCommandeCard(Commande cmd) {
    JPanel card = new JPanel();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBackground(BG_CARD);  // ✅ Blanc pur
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_LIGHT, 1),  // ✅ Bordure taupe
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    
    // Numéro table (simulé)
    JLabel tableLabel = new JLabel("Table " + (cmd.getId() % 10 + 1));
    tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    tableLabel.setForeground(PRIMARY_COLOR);
    tableLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    // ID commande
    JLabel idLabel = new JLabel("Commande #" + cmd.getId());
    idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    idLabel.setForeground(new Color(150, 150, 150));
    idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    // Montant
    JLabel montantLabel = new JLabel(String.format("%.2f F CFA", cmd.getTotal()));
    montantLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    montantLabel.setForeground(SUCCESS_COLOR);
    montantLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    montantLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
    
    // Statut avec emoji
    String etat = cmd.getEtat();
    String emoji = "⏳";
    if ("VALIDEE".equals(etat)) emoji = "✅";
    if ("ANNULEE".equals(etat)) emoji = "❌";
    
    JLabel etatLabel = new JLabel(emoji + " " + etat);
    etatLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    etatLabel.setForeground(new Color(100, 100, 100));
    etatLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    card.add(tableLabel);
    card.add(Box.createVerticalStrut(3));
    card.add(idLabel);
    card.add(Box.createVerticalStrut(10));
    card.add(montantLabel);
    card.add(Box.createVerticalStrut(5));
    card.add(etatLabel);
    
    return card;
}

private void configurerRaccourcisClavier() {
    // Ctrl + N : Nouvelle commande
    getRootPane().registerKeyboardAction(
        e -> new GestionCommandesFrame().setVisible(true),
        KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK),
        JComponent.WHEN_IN_FOCUSED_WINDOW
    );
    
    // Ctrl + S : Enregistrer (exemple)
    getRootPane().registerKeyboardAction(
        e -> JOptionPane.showMessageDialog(this, "Enregistrement..."),
        KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK),
        JComponent.WHEN_IN_FOCUSED_WINDOW
    );
    
    // Ctrl + F : Rechercher
    getRootPane().registerKeyboardAction(
        e -> JOptionPane.showMessageDialog(this, "Recherche..."),
        KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK),
        JComponent.WHEN_IN_FOCUSED_WINDOW
    );
    
    // Ctrl + P : Imprimer
    getRootPane().registerKeyboardAction(
        e -> JOptionPane.showMessageDialog(this, "Impression..."),
        KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK),
        JComponent.WHEN_IN_FOCUSED_WINDOW
    );
    
    // F1 : Aide
    getRootPane().registerKeyboardAction(
        e -> new AideSupportFrame().setVisible(true),
        KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW
    );
    
    // Alt + 1 : Dashboard
    getRootPane().registerKeyboardAction(
        e -> {
            // Rester sur dashboard
        },
        KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_DOWN_MASK),
        JComponent.WHEN_IN_FOCUSED_WINDOW
    );
    
    // Alt + 2 : Produits
    getRootPane().registerKeyboardAction(
        e -> showModule("produits"),
        KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_DOWN_MASK),
        JComponent.WHEN_IN_FOCUSED_WINDOW
    );
    
    // Alt + 3 : Stock
    getRootPane().registerKeyboardAction(
        e -> showModule("stock"),
        KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_DOWN_MASK),
        JComponent.WHEN_IN_FOCUSED_WINDOW
    );
    
    // Alt + 4 : Commandes
    getRootPane().registerKeyboardAction(
        e -> showModule("commandes"),
        KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.ALT_DOWN_MASK),
        JComponent.WHEN_IN_FOCUSED_WINDOW
    );
    
    // Alt + 5 : Statistiques
    getRootPane().registerKeyboardAction(
        e -> showModule("stats"),
        KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.ALT_DOWN_MASK),
        JComponent.WHEN_IN_FOCUSED_WINDOW
    );
    
    // Alt + 6 : Paramètres
    getRootPane().registerKeyboardAction(
        e -> showModule("parametres"),
        KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.ALT_DOWN_MASK),
        JComponent.WHEN_IN_FOCUSED_WINDOW
    );
    
    // Alt + F4 : Quitter (déjà géré par défaut)
}

// ===== CARTE ALERTE PROPRE =====
private JPanel createAlerteCard(Produit p) {
    JPanel card = new JPanel();
    
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    
    // Nom du produit
    JLabel nomLabel = new JLabel(p.getNom());
    nomLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    nomLabel.setForeground(PRIMARY_COLOR);
    nomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    // Quantité
    String unite = "unités";
    if (p.getCategorie().getLibelle().toLowerCase().contains("boisson")) unite = "unités";
    else if (p.getCategorie().getLibelle().toLowerCase().contains("plat")) unite = "portions";
    else if (p.getCategorie().getLibelle().toLowerCase().contains("dessert")) unite = "parts";
    
    JLabel quantiteLabel = new JLabel(p.getStockActuel() + " " + unite);
    quantiteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    quantiteLabel.setForeground(new Color(150, 150, 150));
    quantiteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    quantiteLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    
    // Statut
    String statut = p.getStockActuel() <= 0 ? "RUPTURE" : "Stock bas";
    Color couleur = p.getStockActuel() <= 0 ? DANGER_COLOR : WARNING_COLOR;
    
    JLabel statutLabel = new JLabel("⚠️ " + statut);
    statutLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    statutLabel.setForeground(couleur);
    statutLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    card.add(nomLabel);
    card.add(quantiteLabel);
    card.add(statutLabel);
    
    return card;
}

// ===== NOUVELLE MÉTHODE : CARTE DE COMMANDE =====
private JPanel createOrderCard(String table, String client, String montant, String etat) {
    JPanel card = new JPanel(new BorderLayout(10, 5));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    
    // Titre
    JLabel tableLabel = new JLabel(table);
    tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    tableLabel.setForeground(PRIMARY_COLOR);
    
    // Client
    JLabel clientLabel = new JLabel(client);
    clientLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    clientLabel.setForeground(new Color(150, 150, 150));
    
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);
    topPanel.add(tableLabel, BorderLayout.WEST);
    topPanel.add(clientLabel, BorderLayout.EAST);
    
    // Montant
    JLabel montantLabel = new JLabel(montant);
    montantLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    montantLabel.setForeground(SUCCESS_COLOR);
    
    // Statut
    JLabel etatLabel = new JLabel(getStatusEmoji(etat) + " " + etat);
    etatLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    etatLabel.setForeground(new Color(100, 100, 100));
    
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setOpaque(false);
    bottomPanel.add(montantLabel, BorderLayout.WEST);
    bottomPanel.add(etatLabel, BorderLayout.EAST);
    
    card.add(topPanel, BorderLayout.NORTH);
    card.add(bottomPanel, BorderLayout.SOUTH);
    
    return card;
}

// ===== NOUVELLE MÉTHODE : CARTE D'ALERTE =====
private JPanel createAlertCard(String produit, String quantite, String etat, Color color) {
    JPanel card = new JPanel(new BorderLayout(10, 5));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    
    JLabel produitLabel = new JLabel(produit);
    produitLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    produitLabel.setForeground(PRIMARY_COLOR);
    
    JLabel quantiteLabel = new JLabel(quantite);
    quantiteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    quantiteLabel.setForeground(new Color(150, 150, 150));
    
    JLabel etatLabel = new JLabel(etat);
    etatLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    etatLabel.setForeground(color);
    etatLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);
    topPanel.add(produitLabel, BorderLayout.WEST);
    topPanel.add(etatLabel, BorderLayout.EAST);
    
    card.add(topPanel, BorderLayout.NORTH);
    card.add(quantiteLabel, BorderLayout.SOUTH);
    
    return card;
}

// ===== MÉTHODES UTILITAIRES =====
private String getUnite(String categorie) {
    if (categorie.toLowerCase().contains("boisson")) return "unités";
    if (categorie.toLowerCase().contains("plat")) return "portions";
    if (categorie.toLowerCase().contains("dessert")) return "parts";
    return "unités";
}

private String getStatusEmoji(String etat) {
    switch(etat) {
        case "EN_COURS": return "⏳";
        case "VALIDÉE": return "✅";
        case "ANNULÉE": return "❌";
        default: return "📋";
    }
}

        

    private JPanel createOrderItem(String table, String client, String montant, String status) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));
        
        JLabel tableLabel = new JLabel("<html><b>" + table + "</b> • " + client + "</html>");
        tableLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel montantLabel = new JLabel("<html><b>" + montant + "</b><br><small>" + status + "</small></html>");
        montantLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        montantLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        item.add(tableLabel, BorderLayout.WEST);
        item.add(montantLabel, BorderLayout.EAST);
        
        return item;
    }
    
private JPanel createAlertItem(String produit, String quantite, String etat, Color color) {
    JPanel item = new JPanel(new BorderLayout(15, 0));
    item.setBackground(Color.WHITE);
    item.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
        BorderFactory.createEmptyBorder(12, 5, 12, 10)
    ));
    item.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    // Effet de survol subtil
    item.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            item.setBackground(new Color(248, 248, 248));
            item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 1, 0, color),
                BorderFactory.createEmptyBorder(12, 2, 12, 10)
            ));
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            item.setBackground(Color.WHITE);
            item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                BorderFactory.createEmptyBorder(12, 5, 12, 10)
            ));
        }
    });
    
    // Extraire l'emoji du produit (ex: "🍷 Vin rouge" → "🍷" et "Vin rouge")
    // Note: Certains emojis prennent 2 caractères, d'autres 1
    String emoji;
    String produitSansEmoji;
    
    if (produit.length() >= 2 && Character.isSurrogatePair(produit.charAt(0), produit.charAt(1))) {
        // Emoji sur 2 caractères (comme la plupart)
        emoji = produit.substring(0, 2);
        produitSansEmoji = produit.substring(2).trim();
    } else if (!produit.isEmpty()) {
        // Emoji sur 1 caractère ou pas d'emoji
        emoji = produit.substring(0, 1);
        produitSansEmoji = produit.substring(1).trim();
    } else {
        emoji = "⚠️";
        produitSansEmoji = produit;
    }
    
    // Icône avec fond circulaire (comme les autres éléments)
    JLabel iconLabel = new JLabel(emoji) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fond circulaire semi-transparent
            Color bgColorWithAlpha = new Color(color.getRed(), color.getGreen(), color.getBlue(), 25);
            g2.setColor(bgColorWithAlpha);
            
            int circleSize = Math.min(getWidth(), getHeight()) - 6;
            int x = (getWidth() - circleSize) / 2;
            int y = (getHeight() - circleSize) / 2;
            
            g2.fillOval(x, y, circleSize, circleSize);
            
            // Très fine bordure
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
            g2.setStroke(new BasicStroke(0.8f));
            g2.drawOval(x, y, circleSize, circleSize);
            
            g2.dispose();
            
            // Dessiner l'emoji par-dessus
            super.paintComponent(g);
        }
    };
    
    iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
    iconLabel.setForeground(color);
    iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
    iconLabel.setVerticalAlignment(SwingConstants.CENTER);
    iconLabel.setPreferredSize(new Dimension(42, 42));
    
    // Texte du produit
    JLabel produitLabel = new JLabel(produitSansEmoji);
    produitLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
    produitLabel.setForeground(PRIMARY_COLOR);
    
    // Panel pour la quantité et l'état
    JPanel rightPanel = new JPanel(new BorderLayout(15, 0));
    rightPanel.setOpaque(false);
    
    // Quantité avec icône petite
    JPanel quantitePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    quantitePanel.setOpaque(false);
    
    // Petite icône pour la quantité
    JLabel quantiteIcon = new JLabel("📊") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Très petit fond circulaire
            g2.setColor(new Color(200, 200, 200, 30));
            int size = Math.min(getWidth(), getHeight()) - 2;
            g2.fillOval(0, 0, size, size);
            
            g2.dispose();
            super.paintComponent(g);
        }
    };
    quantiteIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
    quantiteIcon.setForeground(new Color(150, 150, 150));
    quantiteIcon.setPreferredSize(new Dimension(18, 18));
    
    JLabel quantiteLabel = new JLabel(quantite);
    quantiteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    quantiteLabel.setForeground(new Color(120, 120, 120));
    
    quantitePanel.add(quantiteIcon);
    quantitePanel.add(quantiteLabel);
    
    // État avec badge coloré
    JLabel etatLabel = new JLabel(etat) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Badge arrondi avec fond
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 15));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            
            // Bordure subtile
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            
            g2.dispose();
            
            // Dessiner le texte par-dessus
            super.paintComponent(g);
        }
    };
    
    etatLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
    etatLabel.setForeground(color);
    etatLabel.setHorizontalAlignment(SwingConstants.CENTER);
    etatLabel.setVerticalAlignment(SwingConstants.CENTER);
    etatLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
    etatLabel.setPreferredSize(new Dimension(90, 24));
    
    rightPanel.add(quantitePanel, BorderLayout.WEST);
    rightPanel.add(etatLabel, BorderLayout.EAST);
    
    // Container pour l'icône et le texte du produit
    JPanel leftPanel = new JPanel(new BorderLayout(12, 0));
    leftPanel.setOpaque(false);
    leftPanel.add(iconLabel, BorderLayout.WEST);
    
    // Container pour le texte du produit avec alignement vertical
    JPanel produitPanel = new JPanel(new GridBagLayout());
    produitPanel.setOpaque(false);
    produitPanel.add(produitLabel);
    
    leftPanel.add(produitPanel, BorderLayout.CENTER);
    
    // Assemblage final
    item.add(leftPanel, BorderLayout.WEST);
    item.add(rightPanel, BorderLayout.EAST);
    
    return item;
}
    
    
    private JPanel createPlaceholderPanel(String title, String description) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);
        
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setForeground(new Color(100, 100, 100));
        descArea.setBackground(Color.WHITE);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        descArea.setPreferredSize(new Dimension(600, 200));
        gbc.gridy = 1;
        panel.add(descArea, gbc);
        
        JButton implementBtn = new JButton("🔧 Implémenter ce module");
        implementBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        implementBtn.setBackground(ACCENT_COLOR);
        implementBtn.setForeground(Color.WHITE);
        implementBtn.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        implementBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 2;
        panel.add(implementBtn, gbc);
        
        return panel;
    }
    
    private void highlightNavButton(JButton[] buttons, JButton activeButton) {
        for (JButton button : buttons) {
            button.setForeground(new Color(220, 220, 220));
            button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        activeButton.setForeground(Color.WHITE);
        activeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }
    
    private void showModule(String module) {
        switch(module) {
            case "produits":
                if (Session.getUtilisateur() != null) {
                    new GestionProduitFrame().setVisible(true);
                }
                break;

            case "stock":
                if (Session.isAdmin()) {
                    new GestionStockFrame().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "❌ Accès réservé aux administrateurs",
                        "Permission refusée", JOptionPane.WARNING_MESSAGE);
                }
                break;

            case "commandes":
                // ✅ TOUT LE MONDE PEUT PRENDRE DES COMMANDES
                new GestionCommandesFrame().setVisible(true);
                break;

            case "stats":
                if (Session.isAdmin()) {
                    new StatistiquesFrame().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "❌ Accès réservé aux administrateurs",
                        "Permission refusée", JOptionPane.WARNING_MESSAGE);
                }
                break;

            case "parametres":
                if (Session.isAdmin()) {
                    new GestionUtilisateursFrame().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "❌ Accès réservé aux administrateurs",
                        "Permission refusée", JOptionPane.WARNING_MESSAGE);
                }
                break;

            case "aide":  // ← NOUVEAU CASE
                // ✅ TOUT LE MONDE PEUT CONSULTER L'AIDE
                new AideSupportFrame().setVisible(true);
                break;

            default:
                // Dashboard - rien à faire
                break;
        }
    }

    private void executeQuickAction(String action) {
        switch(action) {


            default:
                JOptionPane.showMessageDialog(this,
                    "Action rapide : " + action,
                    "Action", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showNotifications() {
    // Créer un popup personnalisé au lieu de JOptionPane
    JDialog notificationDialog = new JDialog(this, "🔔 Notifications", true);
    notificationDialog.setLayout(new BorderLayout());
    notificationDialog.setSize(500, 400);
    notificationDialog.setMinimumSize(new Dimension(400, 300));
    notificationDialog.setLocationRelativeTo(this);
    notificationDialog.getContentPane().setBackground(new Color(250, 250, 250));
    notificationDialog.setResizable(true);
    
    // En-tête avec bouton plein écran
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(44, 62, 80));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
    
    JLabel titleLabel = new JLabel("🔔 NOTIFICATIONS");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    titleLabel.setForeground(Color.WHITE);
    
    // ✅ BOUTON PLEIN ÉCRAN
    JButton fullscreenBtn = new JButton("🗖");
    fullscreenBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    fullscreenBtn.setForeground(Color.WHITE);
    fullscreenBtn.setBackground(new Color(60, 60, 60));
    fullscreenBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    fullscreenBtn.setFocusPainted(false);
    fullscreenBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    fullscreenBtn.setToolTipText("Plein écran");
    fullscreenBtn.addActionListener(ev -> {
        // Basculer en plein écran
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        
        if (device.isFullScreenSupported()) {
            notificationDialog.dispose(); // Nécessaire pour changer le mode
            notificationDialog.setUndecorated(!notificationDialog.isUndecorated());
            
            if (notificationDialog.isUndecorated()) {
                device.setFullScreenWindow(notificationDialog);
            } else {
                device.setFullScreenWindow(null);
                notificationDialog.setSize(500, 400);
                notificationDialog.setLocationRelativeTo(notificationDialog.getParent());
            }
            
            notificationDialog.setVisible(true);
        }
    });
    
    JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    countPanel.setOpaque(false);
    
    JLabel countLabel = new JLabel("3 non lues");
    countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    countLabel.setForeground(new Color(200, 200, 200));
    
    countPanel.add(fullscreenBtn);
    countPanel.add(countLabel);
    
    headerPanel.add(titleLabel, BorderLayout.WEST);
    headerPanel.add(countPanel, BorderLayout.EAST);
    
    // Liste des notifications
    JPanel notificationsPanel = new JPanel();
    notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
    notificationsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    notificationsPanel.setBackground(Color.WHITE);
    
    String[][] notifications = {
        {"🔴", "URGENT", "Stock bas - Vin rouge", "Il reste seulement 3 bouteilles de vin rouge", "Il y a 5 min"},
        {"🟢", "INFO", "Commande validée", "Table 2 - Commande de 120F CFA validée", "Il y a 15 min"},
        {"🔵", "SYSTÈME", "Sauvegarde", "Sauvegarde automatique effectuée", "Il y a 30 min"}
    };
    
    for (String[] notif : notifications) {
        notificationsPanel.add(createNotificationItem(notif[0], notif[1], notif[2], notif[3], notif[4]));
        notificationsPanel.add(Box.createVerticalStrut(10));
    }
    
    // Boutons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
    
    JButton markReadBtn = new JButton("📋 Tout marquer comme lu");
    markReadBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    markReadBtn.setBackground(new Color(52, 152, 219));
    markReadBtn.setForeground(Color.WHITE);
    markReadBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    markReadBtn.addActionListener(e -> notificationDialog.dispose());
    
    JButton closeBtn = new JButton("Fermer");
    closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    closeBtn.setBackground(new Color(200, 200, 200));
    closeBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    closeBtn.addActionListener(e -> notificationDialog.dispose());
    
    buttonPanel.add(markReadBtn);
    buttonPanel.add(Box.createHorizontalStrut(10));
    buttonPanel.add(closeBtn);
    
    JScrollPane scrollPane = new JScrollPane(notificationsPanel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getViewport().setBackground(Color.WHITE);
    
    notificationDialog.add(headerPanel, BorderLayout.NORTH);
    notificationDialog.add(scrollPane, BorderLayout.CENTER);
    notificationDialog.add(buttonPanel, BorderLayout.SOUTH);
    
    notificationDialog.setVisible(true);
}
private JPanel createNotificationItem(String emoji, String type, String title, String message, String time) {
    JPanel item = new JPanel(new BorderLayout(15, 0));
    item.setBackground(Color.WHITE);
    item.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
        BorderFactory.createEmptyBorder(15, 10, 15, 10)
    ));
    item.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    // Icône
    JLabel iconLabel = new JLabel(emoji) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Cercle de fond selon le type
            Color bgColor;
            if (emoji.equals("🔴")) bgColor = new Color(231, 76, 60, 30);
            else if (emoji.equals("🟢")) bgColor = new Color(46, 204, 113, 30);
            else bgColor = new Color(52, 152, 219, 30);
            
            g2.setColor(bgColor);
            int size = Math.min(getWidth(), getHeight());
            g2.fillOval(0, 0, size, size);
            
            g2.dispose();
            super.paintComponent(g);
        }
    };
    
    iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
    iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
    iconLabel.setPreferredSize(new Dimension(40, 40));
    
    // Contenu
    JPanel contentPanel = new JPanel(new GridLayout(3, 1, 0, 5));
    contentPanel.setOpaque(false);
    
    // En-tête avec type et temps
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);
    
    JLabel typeLabel = new JLabel(type);
    typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
    if (emoji.equals("🔴")) typeLabel.setForeground(new Color(231, 76, 60));
    else if (emoji.equals("🟢")) typeLabel.setForeground(new Color(46, 204, 113));
    else typeLabel.setForeground(new Color(52, 152, 219));
    
    JLabel timeLabel = new JLabel(time);
    timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
    timeLabel.setForeground(new Color(150, 150, 150));
    
    headerPanel.add(typeLabel, BorderLayout.WEST);
    headerPanel.add(timeLabel, BorderLayout.EAST);
    
    // Titre et message
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    titleLabel.setForeground(new Color(60, 60, 60));
    
    JLabel messageLabel = new JLabel("<html><div style='width: 250px;'>" + message + "</div></html>");
    messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    messageLabel.setForeground(new Color(100, 100, 100));
    
    contentPanel.add(headerPanel);
    contentPanel.add(titleLabel);
    contentPanel.add(messageLabel);
    
    item.add(iconLabel, BorderLayout.WEST);
    item.add(contentPanel, BorderLayout.CENTER);
    
    return item;
}


    
    private void showUserMenu() {
        JPopupMenu userMenu = new JPopupMenu();
        JMenuItem profileItem = new JMenuItem("👤 Mon profil");
        JMenuItem settingsItem = new JMenuItem("⚙️ Paramètres utilisateur");
        JMenuItem logoutItem = new JMenuItem("🚪 Déconnexion");
        
        logoutItem.addActionListener(e -> deconnexion());
        
        userMenu.add(profileItem);
        userMenu.add(settingsItem);
        userMenu.addSeparator();
        userMenu.add(logoutItem);
        
        Component[] comps = ((JPanel) getContentPane().getComponent(0)).getComponents();
        JPanel toolsPanel = (JPanel) comps[1];
        Component[] tools = toolsPanel.getComponents();
        
        for (Component tool : tools) {
            if (tool instanceof JButton && ((JButton)tool).getText().contains("👨‍💼")) {
                userMenu.show(tool, 0, tool.getHeight());
                break;
            }
        }
    }
    
    
    private void updateTimeLabel(JLabel label) {
        label.setText(new SimpleDateFormat("HH:mm:ss • EEEE d MMMM", Locale.FRENCH).format(new Date()));
    }
    
    private void demarrerStatsTempsReel() {
        statsTimer = new Timer(5000, e -> {
            // Simuler des mises à jour de statistiques
            // À connecter avec la base de données réelle
        });
        statsTimer.start();
    }
    
    private void animerEntree() {
    // Pour Swing, utilise plutôt une transition de couleur de fond
        getContentPane().setBackground(new Color(255, 255, 255, 0));
    
        Timer fadeTimer = new Timer(20, e -> {
            int alpha = getContentPane().getBackground().getAlpha() + 5;
            if (alpha >= 255) {
                alpha = 255; 
                ((Timer)e.getSource()).stop();
        }
        getContentPane().setBackground(new Color(250, 250, 250, alpha));
    });
        fadeTimer.start();
    }
    
    private void configurerFenetre() {
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Essayer d'arrondir les coins (selon système)
        try {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
        } catch (Exception e) {
            // Non supporté sur tous les systèmes
        }
    }
    
    
    
    private void deconnexion() {
    // Créer une boîte de dialogue simple
    int choix = JOptionPane.showConfirmDialog(this,
        "Voulez-vous vraiment vous déconnecter ?",
        "Confirmation de déconnexion",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);
    
    if (choix == JOptionPane.YES_OPTION) {
        // ✅ VIDER LA SESSION
        Session.clear();
        
        // ✅ FERMER LA FENÊTRE PRINCIPALE
        dispose();
        
        // ✅ OUVRIR LOGINFRAME
        new com.gesrestaurant.views.auth.LoginFrame().setVisible(true);
    }
}

private void performLogout() {
    // Animation de fermeture
    Timer fadeTimer = new Timer(20, null);
    fadeTimer.addActionListener(new ActionListener() {
        float opacity = 1.0f;
        
        @Override
        public void actionPerformed(ActionEvent e) {
            opacity -= 0.05f;
            if (opacity <= 0) {
                opacity = 0;
                fadeTimer.stop();
                
                // Fermer cette fenêtre
                dispose();
                
                // Rouvrir la fenêtre de login
                SwingUtilities.invokeLater(() -> {
                    try {
                        new com.gesrestaurant.views.auth.LoginFrame().setVisible(true);
                    } catch (Exception ex) {
                        logger.severe("Erreur lors de l'ouverture de LoginFrame: " + ex.getMessage());
                        // Fallback : message d'erreur
                        JOptionPane.showMessageDialog(null,
                            "Impossible de rouvrir la fenêtre de connexion.\n" +
                            "Veuillez redémarrer l'application manuellement.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
            setOpacity(opacity);
        }
    });
    
    // Essayer l'animation, sinon fermer directement
    try {
        fadeTimer.start();
    } catch (Exception e) {
        // Si l'animation échoue, fermer directement
        dispose();
        SwingUtilities.invokeLater(() -> {
            new com.gesrestaurant.views.auth.LoginFrame().setVisible(true);
        });
    }
}
    
    //Quitte l'application
     
    private void quitterApplication() {
        int confirmation = JOptionPane.showConfirmDialog(instance,
            "Voulez-vous vraiment quitter l'application ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            System.exit(0);
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
        // Appliquer un Look and Feel moderne
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
            // Personnalisation Nimbus
            UIManager.put("nimbusBase", new Color(44, 62, 80));
            UIManager.put("nimbusBlueGrey", new Color(52, 73, 94));
            UIManager.put("control", new Color(250, 250, 250));
            
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                logger.log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        
        // Démarrer l'interface
        SwingUtilities.invokeLater(() -> {
            MainMenuFrame frame = new MainMenuFrame();
            frame.setVisible(true);
            
            // Centrer après affichage
            frame.setLocationRelativeTo(null);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
