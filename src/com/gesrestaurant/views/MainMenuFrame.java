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
    
    
    /**
     * Constructeur principal
     */
    public MainMenuFrame() {
        instance = this;
        initComponentsCustom();
        configurerFenetre();
        animerEntree();
        demarrerStatsTempsReel();
    }
    private void initComponentsCustom() {
        // ============================================
        // 1. CONFIGURATION FONDAMENTALE
        // ============================================
        setTitle("🍽️ Gestion Restaurant - Tableau de Bord");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(250, 250, 250));
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
        
        // Barre d'outils supérieure
        JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolsPanel.setOpaque(false);
        
        // Notification badge
        JButton notificationBtn = createToolbarButton("🔔", "3 notifications", SECONDARY_COLOR);
        notificationBtn.addActionListener(e -> showNotifications());
        
        // Utilisateur
        JButton userBtn = createToolbarButton("👨‍💼", "Administrateur", ACCENT_COLOR);
        userBtn.addActionListener(e -> showUserMenu());
        
        // Date/Heure live
        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(Color.WHITE);
        updateTimeLabel(timeLabel);
        
        // Timer pour l'heure
        Timer clockTimer = new Timer(1000, e -> updateTimeLabel(timeLabel));
        clockTimer.start();
        
        // Bouton déconnexion
        JButton logoutBtn = createToolbarButton("🚪", "Déconnexion", new Color(149, 165, 166));
        logoutBtn.addActionListener(e -> deconnexion());
        
        toolsPanel.add(timeLabel);
        toolsPanel.add(Box.createHorizontalStrut(10));
        toolsPanel.add(notificationBtn);
        toolsPanel.add(userBtn);
        toolsPanel.add(logoutBtn);
        
        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(toolsPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // ============================================
        // 3. SIDEBAR MODERNE (Navigation verticale)
        // ============================================
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(PRIMARY_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(280, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Titre navigation
        JLabel navTitle = new JLabel("  NAVIGATION PRINCIPALE");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        navTitle.setForeground(new Color(189, 195, 199));
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        navTitle.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 0));
        
        sidebarPanel.add(navTitle);
        sidebarPanel.add(Box.createVerticalStrut(10));
        
        // Boutons de navigation selon le sujet
        String[][] menuItems = {
            {"📊", "Tableau de bord", "dashboard"},
            {"📦", "Produits & Catégories", "produits"},
            {"📈", "Mouvements de Stock", "stock"},
            {"🛒", "Commandes Clients", "commandes"},
            {"💰", "Chiffre d'affaires", "ca"},
            {"📋", "Statistiques", "stats"},
            {"⚙️", "Paramètres système", "parametres"},
            {"❓", "Aide & Support", "aide"}
        };
        
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
        JLabel quickActionsTitle = new JLabel("  ACTIONS RAPIDES");
        quickActionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        quickActionsTitle.setForeground(new Color(189, 195, 199));
        quickActionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        quickActionsTitle.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 0));
        
        sidebarPanel.add(quickActionsTitle);
        
        String[][] quickActions = {
            {"➕", "Nouvelle commande"},
            {"🖨️", "Imprimer facture"},
            {"📤", "Exporter rapport"},
            {"🔍", "Recherche avancée"}
        };
        
        for (String[] action : quickActions) {
            JButton quickBtn = createQuickActionButton(action[0], action[1]);
            quickBtn.addActionListener(e -> executeQuickAction(action[1]));
            sidebarPanel.add(quickBtn);
            sidebarPanel.add(Box.createVerticalStrut(3));
        }
        
        add(sidebarPanel, BorderLayout.WEST);
        
        // ============================================
        // 4. CONTENU PRINCIPAL (Zone centrale)
        // ============================================
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(new Color(255, 255, 255));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

// GARDER UNIQUEMENT CETTE LIGNE
        JPanel dashboardPanel = createDashboardPanel();
        mainContentPanel.add(dashboardPanel, BorderLayout.CENTER);  // ← DIRECTEMENT AU CENTRE

// ... garder le reste du code (infoPanel, etc.) ...
        
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
        JLabel systemInfo = new JLabel("✅ Base de données connectée • MySQL • ");
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // En-tête du dashboard
        JPanel dashboardHeader = new JPanel(new BorderLayout());
        dashboardHeader.setOpaque(false);
        
        JLabel dashTitle = new JLabel("Tableau de bord");
        dashTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        dashTitle.setForeground(PRIMARY_COLOR);
        
        JLabel dashDate = new JLabel(new SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH).format(new Date()));
        dashDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dashDate.setForeground(new Color(150, 150, 150));
        
        dashboardHeader.add(dashTitle, BorderLayout.WEST);
        dashboardHeader.add(dashDate, BorderLayout.EAST);
        
        panel.add(dashboardHeader, BorderLayout.NORTH);
        
        // Cartes de statistiques
        JPanel statsCardsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsCardsPanel.setOpaque(false);
        statsCardsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        
        // Cartes selon le sujet
        String[][] statsData = {
            {"📦", "Produits en stock", "142", "articles", "#27AE60"},
            {"💰", "CA aujourd'hui", "2 850 €", "+12% vs hier", "#2ECC71"},
            {"🛒", "Commandes actives", "8", "en cours", "#3498DB"},
            {"📊", "Taux occupation", "74%", "Très bon", "#F1C40F"},
            {"⚠️", "Alertes stock", "3", "produits bas", "#E74C3C"},
            {"👥", "Clients servis", "42", "aujourd'hui", "#9B59B6"}
        };

// VERSION SÉCURISÉE
        for (String[] data : statsData) {
            Color color;
        try {
            color = Color.decode(data[4]);  // Convertir hexa → Color
        } catch (Exception e) {
            color = Color.BLACK;  // Couleur par défaut si erreur
        }
        }
        
        panel.add(statsCardsPanel, BorderLayout.CENTER);
        
        // Section inférieure avec commandes et alertes
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        bottomSplitPane.setDividerLocation(500);
        bottomSplitPane.setDividerSize(2);
        bottomSplitPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Commandes en cours
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                "🛒 Commandes en cours",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_COLOR
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        String[][] ordersData = {
            {"Table 5", "Dupont", "85€", "⏳ En préparation"},
            {"Table 2", "Martin", "120€", "✅ Servie"},
            {"Table 8", "Leroy", "65€", "⏳ Cuisine"},
            {"Table 3", "Dubois", "95€", "📋 En attente"}
        };
        
        JPanel ordersList = new JPanel(new GridLayout(ordersData.length, 1, 0, 10));
        ordersList.setOpaque(false);
        
        for (String[] order : ordersData) {
            ordersList.add(createOrderItem(order[0], order[1], order[2], order[3]));
        }
        
        ordersPanel.add(ordersList, BorderLayout.CENTER);
        
        // Alertes stock
        JPanel alertsPanel = new JPanel(new BorderLayout());
        alertsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                "⚠️ Alertes stock",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                SECONDARY_COLOR
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        String[][] alertsData = {
    {"🍷 Vin rouge", "3 bouteilles", "Seuil bas", "#E74C3C"},
    {"🐟 Saumon", "2 kg", "À réapprovisionner", "#E74C3C"},
    {"🍞 Pain baguette", "15 unités", "Stock OK", "#27AE60"},
    {"🥩 Filet mignon", "5 portions", "Niveau moyen", "#F1C40F"}
    };

    JPanel alertsList = new JPanel(new GridLayout(alertsData.length, 1, 0, 10));
    alertsList.setOpaque(false);

// CORRECTION ICI : String[] au lieu de Object[], et conversion avec Color.decode()
    for (String[] alert : alertsData) {
    Color color = Color.decode(alert[3]);  // Convertir "#E74C3C" en objet Color
    alertsList.add(createAlertItem(alert[0], alert[1], alert[2], color));
    }
        
        alertsPanel.add(alertsList, BorderLayout.CENTER);
        
        bottomSplitPane.setLeftComponent(ordersPanel);
        bottomSplitPane.setRightComponent(alertsPanel);
        
        panel.add(bottomSplitPane, BorderLayout.SOUTH);
        
        return panel;
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
            // OUVRE TA VRAIE FENÊTRE !
            new GestionProduitFrame().setVisible(true);
            break;
            
        case "stock":
            JOptionPane.showMessageDialog(this,
                "Module Mouvements de Stock\n\nEn développement",
                "Module Stock", JOptionPane.INFORMATION_MESSAGE);
            break;
            
        case "commandes":
            JOptionPane.showMessageDialog(this,
                "Module Commandes Clients\n\nEn développement",
                "Module Commandes", JOptionPane.INFORMATION_MESSAGE);
            break;
            
        case "ca":
        case "stats":
            JOptionPane.showMessageDialog(this,
                "Module Statistiques\n\nEn développement",
                "Statistiques", JOptionPane.INFORMATION_MESSAGE);
            break;
            
        case "parametres":
            JOptionPane.showMessageDialog(this,
                "⚙️ Paramètres système\n\nEn développement",
                "Paramètres", JOptionPane.INFORMATION_MESSAGE);
            break;
            
        case "aide":
            JOptionPane.showMessageDialog(this,
                "🍽️ GESTION RESTAURANT v1.0\n\n" +
                "Développé avec Java Swing\n" +
                "Conforme au sujet POO Java",
                "À propos", JOptionPane.INFORMATION_MESSAGE);
            break;
            
        default:
            // Dashboard - rien à faire
            break;
    }
}
     
    private void executeQuickAction(String action) {
        switch(action) {
            case "Nouvelle commande":
                JOptionPane.showMessageDialog(this,
                    "Création d'une nouvelle commande client\n\n" +
                    "Cette fonctionnalité ouvrira le formulaire\n" +
                    "de création de commande.",
                    "Nouvelle commande", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Imprimer facture":
                JOptionPane.showMessageDialog(this,
                    "Impression de facture\n\n" +
                    "Génération et impression d'une facture\n" +
                    "pour une commande sélectionnée.",
                    "Impression", JOptionPane.INFORMATION_MESSAGE);
                break;
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
    notificationDialog.setSize(400, 300);
    notificationDialog.setLocationRelativeTo(this);
    notificationDialog.getContentPane().setBackground(new Color(250, 250, 250));
    
    // En-tête
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(44, 62, 80));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
    
    JLabel titleLabel = new JLabel("🔔 NOTIFICATIONS");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    titleLabel.setForeground(Color.WHITE);
    
    JLabel countLabel = new JLabel("3 non lues");
    countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    countLabel.setForeground(new Color(200, 200, 200));
    
    headerPanel.add(titleLabel, BorderLayout.WEST);
    headerPanel.add(countLabel, BorderLayout.EAST);
    
    // Liste des notifications
    JPanel notificationsPanel = new JPanel();
    notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
    notificationsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    notificationsPanel.setBackground(Color.WHITE);
    
    String[][] notifications = {
        {"🔴", "URGENT", "Stock bas - Vin rouge", "Il reste seulement 3 bouteilles de vin rouge", "Il y a 5 min"},
        {"🟢", "INFO", "Commande validée", "Table 2 - Commande de 120€ validée", "Il y a 15 min"},
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
    // Créer une boîte de dialogue personnalisée
    JDialog logoutDialog = new JDialog(this, "🚪 Déconnexion", true);
    logoutDialog.setLayout(new BorderLayout());
    logoutDialog.setSize(400, 250);
    logoutDialog.setLocationRelativeTo(this);
    logoutDialog.getContentPane().setBackground(new Color(250, 250, 250));
    logoutDialog.setResizable(false);
    
    // En-tête
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(44, 62, 80));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
    
    JLabel titleLabel = new JLabel("🚪 Déconnexion");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setForeground(Color.WHITE);
    
    JLabel subtitleLabel = new JLabel("Confirmation requise");
    subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    subtitleLabel.setForeground(new Color(200, 200, 200));
    
    JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
    titlePanel.setOpaque(false);
    titlePanel.add(titleLabel);
    titlePanel.add(subtitleLabel);
    
    headerPanel.add(titlePanel, BorderLayout.WEST);
    
    // Icône d'avertissement
    JLabel warningIcon = new JLabel("⚠️") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(new Color(241, 196, 15, 30));
            int size = Math.min(getWidth(), getHeight());
            g2.fillOval(0, 0, size, size);
            
            g2.dispose();
            super.paintComponent(g);
        }
    };
    warningIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
    warningIcon.setHorizontalAlignment(SwingConstants.CENTER);
    warningIcon.setPreferredSize(new Dimension(60, 60));
    
    headerPanel.add(warningIcon, BorderLayout.EAST);
    
    // Contenu du message
    JPanel messagePanel = new JPanel(new BorderLayout());
    messagePanel.setBackground(Color.WHITE);
    messagePanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
    
    JLabel messageLabel = new JLabel("<html><div style='text-align: center; width: 320px;'>"
        + "<b>Êtes-vous sûr de vouloir vous déconnecter ?</b><br><br>"
        + "Toutes les données non sauvegardées seront perdues.<br>"
        + "Vous devrez vous reconnecter pour accéder au système."
        + "</div></html>");
    messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    messageLabel.setForeground(new Color(80, 80, 80));
    messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
    messagePanel.add(messageLabel, BorderLayout.CENTER);
    
    // Boutons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
    
    // Bouton Annuler
    JButton cancelButton = new JButton("Annuler") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fond gris
            if (getModel().isRollover()) {
                g2.setColor(new Color(200, 200, 200));
            } else {
                g2.setColor(new Color(220, 220, 220));
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            
            g2.dispose();
            super.paintComponent(g);
        }
    };
    
    cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
    cancelButton.setForeground(new Color(100, 100, 100));
    cancelButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
    cancelButton.setContentAreaFilled(false);
    cancelButton.setFocusPainted(false);
    cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    cancelButton.addActionListener(e -> logoutDialog.dispose());
    
    // Bouton Déconnexion
    JButton logoutButton = new JButton("Se déconnecter") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fond rouge avec dégradé
            GradientPaint gradient;
            if (getModel().isRollover()) {
                gradient = new GradientPaint(
                    0, 0, new Color(231, 76, 60).brighter(),
                    getWidth(), getHeight(), new Color(192, 57, 43).brighter()
                );
            } else {
                gradient = new GradientPaint(
                    0, 0, new Color(231, 76, 60),
                    getWidth(), getHeight(), new Color(192, 57, 43)
                );
            }
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            
            g2.dispose();
            super.paintComponent(g);
        }
    };
    
    logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
    logoutButton.setForeground(Color.WHITE);
    logoutButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
    logoutButton.setContentAreaFilled(false);
    logoutButton.setFocusPainted(false);
    logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    logoutButton.addActionListener(e -> {
        logoutDialog.dispose();
        performLogout();
    });
    
    buttonPanel.add(cancelButton);
    buttonPanel.add(logoutButton);
    
    // Pied de page avec info
    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    footerPanel.setBackground(new Color(245, 245, 245));
    footerPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
        BorderFactory.createEmptyBorder(10, 20, 10, 20)
    ));
    
    JLabel footerLabel = new JLabel("🛡️ Session sécurisée • " + 
        new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()));
    footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
    footerLabel.setForeground(new Color(150, 150, 150));
    
    footerPanel.add(footerLabel);
    
    // Assembler la boîte de dialogue
    logoutDialog.add(headerPanel, BorderLayout.NORTH);
    logoutDialog.add(messagePanel, BorderLayout.CENTER);
    logoutDialog.add(buttonPanel, BorderLayout.SOUTH);
    logoutDialog.add(footerPanel, BorderLayout.PAGE_END);
    
    // Afficher la boîte de dialogue
    logoutDialog.setVisible(true);
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
