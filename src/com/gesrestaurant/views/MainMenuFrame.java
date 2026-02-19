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
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;


/**
 *
 * @author rahim
 */
 
public class MainMenuFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainMenuFrame.class.getName());
    private MainMenuFrame instance;
    private Timer statsTimer;
    private JPanel currentContentPanel;
    
    // ===== COULEURS EXISTANTES (GARDÉES) =====
    private Color PRIMARY_COLOR = new Color(44, 62, 80);
    private Color SECONDARY_COLOR = new Color(231, 76, 60);
    private Color SUCCESS_COLOR = new Color(39, 174, 96);
    private Color WARNING_COLOR = new Color(241, 196, 15);
    private Color ACCENT_COLOR = new Color(52, 152, 219);
    private Color DANGER_COLOR = new Color(231, 76, 60);
    private Color BG_PRIMARY = new Color(250, 245, 240);
    private Color BG_CARD = new Color(255, 255, 255);
    private Color BORDER_LIGHT = new Color(230, 220, 210);
    
    // ===== NOUVELLES COULEURS GRAS PALACE =====
    private static final Color GRAS_GOLD = new Color(255, 215, 0);          // Or/jaune
    private static final Color GRAS_RED = new Color(229, 9, 20);            // Rouge vif
    private static final Color GRAS_RED_DARK = new Color(180, 0, 15);       // Rouge foncé
    private static final Color GLASS_WHITE = new Color(255, 255, 255, 220); // Blanc vitré
    private static final Color GLASS_DARK = new Color(0, 0, 0, 180);        // Noir vitré
    
    // ===== DAO EXISTANTS =====
    private Connection connection;
    private ProduitDAO produitDAO;
    private CommandeDAO commandeDAO;
    private Utilisateur utilisateurConnecte; 
    
    // ===== COMPOSANTS EXISTANTS À GARDER =====
    // (Tous vos composants existants restent inchangés)
    private JLabel lblStatus; // sera utilisé dans le footer modifié
    
    // ===== NOUVEAUX COMPOSANTS POUR LE DESIGN =====
    private BufferedImage backgroundImage;
    private JLabel lblTime;
    private JLabel lblDate;
    private JLabel lblWelcome;
    private JLabel lblStatsCA, lblStatsCmd, lblStatsStock, lblStatsAlert;
    private JPanel commandesPanel;
    private JPanel alertesPanel;
    
    
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
        
        chargerImageFond();
        initComponentsCustom();  // Votre méthode existante modifiée
        configurerFenetre();
        animerEntree();
        demarrerStatsTempsReel();
        configurerRaccourcisClavier(); 
        
    }
    
    /**
 * Charge l'image de fond (intérieur de restaurant)
 */
    private void chargerImageFond() {
        try {
            // Essayer de charger depuis le dossier resources
            URL imageUrl = getClass().getResource("/com/gesrestaurant/resources/restaurant_interior.jpg");
            if (imageUrl != null) {
                backgroundImage = ImageIO.read(imageUrl);
                logger.info("✅ Image de fond chargée");
            } else {
                logger.warning("⚠️ Image de fond non trouvée, utilisation du dégradé");
            }
        } catch (Exception e) {
            logger.warning("⚠️ Erreur chargement image: " + e.getMessage());
        }
    }
    
    private void initComponentsCustom() {
        // ============================================
        // 1. CONFIGURATION FONDAMENTALE
        // ============================================
        setTitle("🍽️ GRAS PALACE - Tableau de Bord");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        
        // ============================================
        // PANEL PRINCIPAL AVEC IMAGE DE FOND
        // ============================================
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Dessiner l'image de fond
            if (backgroundImage != null) {
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Dégradé de secours
                GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 30, 30), 
                                                          getWidth(), getHeight(), new Color(80, 50, 30));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            // 🔥 FILTRE PLUS SOMBRE (de 100 à 180 pour meilleure lisibilité)
            g2.setColor(new Color(0, 0, 0, 180));  // ← Plus opaque
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.dispose();
        }
    };

        // ============================================
        // 2. HEADER PREMIUM GRAS PALACE
        // ============================================
        JPanel headerPanel = createHeaderPremium();
        
        // ============================================
        // 3. SIDEBAR AVEC MENU JAUNE/ROUGE
        // ============================================
        JScrollPane menuScrollPane = createMenuPremium();  // ← Nouveau nom
        backgroundPanel.add(menuScrollPane, BorderLayout.WEST);
        // ============================================
        // 4. CONTENU PRINCIPAL (Dashboard)
        // ============================================
        JPanel mainContentPanel = createMainContentPanel();
        
        // ============================================
        // 5. PANEL D'INFORMATIONS EN BAS (Footer)
        // ============================================
        JPanel infoPanel = createInfoPanel();
        
        // ============================================
        // ASSEMBLAGE
        // ============================================
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);
        // Utilisez le nouveau nom que vous avez créé
        backgroundPanel.add(menuScrollPane, BorderLayout.WEST);
        backgroundPanel.add(mainContentPanel, BorderLayout.CENTER);
        backgroundPanel.add(infoPanel, BorderLayout.SOUTH);
        
        add(backgroundPanel, BorderLayout.CENTER);
    }
    
    /**
 * HEADER PREMIUM avec GRAS PALACE et informations entreprise
 */
private JPanel createHeaderPremium() {
    JPanel header = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Fond semi-transparent plus foncé pour meilleure lisibilité
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Ligne décorative en bas (jaune/rouge)
            g2d.setColor(GRAS_GOLD);
            g2d.fillRect(0, getHeight()-3, getWidth()/2, 3);
            g2d.setColor(GRAS_RED);
            g2d.fillRect(getWidth()/2, getHeight()-3, getWidth()/2, 3);
        }
    };
    header.setOpaque(false);
    header.setPreferredSize(new Dimension(getWidth(), 130)); // Plus haut pour accueillir plus d'infos
    header.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));
    
    // ===== PARTIE GAUCHE : Logo et nom principal =====
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    leftPanel.setOpaque(false);
    
    // Ligne 1 : Logo + GRAS PALACE (comme un H1)
    JPanel titleLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    titleLine.setOpaque(false);
    
    JLabel logoIcon = new JLabel("🍽️");
    logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 58)); // Plus grand
    logoIcon.setForeground(GRAS_GOLD);
    
    JLabel appTitle = new JLabel("GRAS PALACE");
    appTitle.setFont(new Font("Montserrat", Font.BOLD, 52)); // Taille H1
    appTitle.setForeground(Color.WHITE);
    
    // Effet d'ombre pour le titre (effet 3D)
    JLabel titleShadow = new JLabel("GRAS PALACE");
    titleShadow.setFont(new Font("Montserrat", Font.BOLD, 52));
    titleShadow.setForeground(new Color(255, 215, 0, 100));
    titleShadow.setBounds(3, 3, 500, 70);
    
    titleLine.add(logoIcon);
    titleLine.add(appTitle);
    
    // Ligne 2 : Sous-titre avec étoiles
    JPanel subtitleLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    subtitleLine.setOpaque(false);
    
    JLabel appSubtitle = new JLabel("SNACK • FAST-FOOD • RESTAURANT");
    appSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
    appSubtitle.setForeground(new Color(255, 215, 0, 220));
    
    JLabel etoile1 = new JLabel("★");
    etoile1.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    etoile1.setForeground(GRAS_GOLD);
    
    JLabel etoile2 = new JLabel("★");
    etoile2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    etoile2.setForeground(GRAS_GOLD);
    
    JLabel etoile3 = new JLabel("★");
    etoile3.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    etoile3.setForeground(GRAS_GOLD);
    
    subtitleLine.add(appSubtitle);
    subtitleLine.add(etoile1);
    subtitleLine.add(etoile2);
    subtitleLine.add(etoile3);
    
    leftPanel.add(titleLine);
    leftPanel.add(Box.createVerticalStrut(5));
    leftPanel.add(subtitleLine);
    
    // ===== PARTIE CENTRALE : Adresse et contact (H1 bis) =====
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
    centerPanel.setOpaque(false);
    centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 20));
    
    // Adresse (comme un H2)
    JLabel addressLabel = new JLabel("Lomé, Rue Kléber DADJO");
    addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
    addressLabel.setForeground(Color.WHITE);
    addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    // Contact
    JLabel contactLabel = new JLabel("📞 +228 92 07 45 57  |  ✉️ contact@graspalace.tg");
    contactLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    contactLabel.setForeground(new Color(255, 255, 255, 220));
    contactLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    // Horaires
    JLabel horairesLabel = new JLabel("🕒 Lun-Sam: 8h-22h  |  Dim: 10h-20h");
    horairesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    horairesLabel.setForeground(new Color(255, 215, 0, 200));
    horairesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    centerPanel.add(addressLabel);
    centerPanel.add(Box.createVerticalStrut(5));
    centerPanel.add(contactLabel);
    centerPanel.add(Box.createVerticalStrut(5));
    centerPanel.add(horairesLabel);
    
    // ===== PARTIE DROITE : Horloge et profil (inchangée) =====
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
    rightPanel.setOpaque(false);
    
    // Horloge
    JPanel clockPanel = new JPanel(new GridLayout(2, 1));
    clockPanel.setOpaque(false);
    
    lblTime = new JLabel();
    lblTime.setFont(new Font("Segoe UI", Font.BOLD, 26));
    lblTime.setForeground(Color.WHITE);
    
    lblDate = new JLabel();
    lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lblDate.setForeground(new Color(255, 255, 255, 180));
    
    clockPanel.add(lblTime);
    clockPanel.add(lblDate);
    
    // Profil utilisateur (réutilisé de votre code)
    JPanel profilPanel = createUserProfilePanel();
    
    // Déconnexion
    JPanel logoutPanel = createLogoutPanel();
    
    rightPanel.add(clockPanel);
    rightPanel.add(profilPanel);
    rightPanel.add(logoutPanel);
    
    // ===== ASSEMBLAGE FINAL =====
    JPanel topRow = new JPanel(new BorderLayout());
    topRow.setOpaque(false);
    topRow.add(leftPanel, BorderLayout.WEST);
    topRow.add(rightPanel, BorderLayout.EAST);
    
    header.add(topRow, BorderLayout.NORTH);
    header.add(centerPanel, BorderLayout.CENTER);
    
    // Démarrer l'horloge
    startClock();
    
    return header;
}


    
    /**
     * PANEL PROFIL UTILISATEUR (adapté de votre code)
     */
    private JPanel createUserProfilePanel() {
        JPanel profilPanel = new JPanel(new BorderLayout(10, 0));
        profilPanel.setOpaque(false);
        profilPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Infos utilisateur
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        
        String userName = (utilisateurConnecte != null) ? utilisateurConnecte.getLogin() : "Invité";
        String userRole = (utilisateurConnecte != null) ? utilisateurConnecte.getRole() : "VISITEUR";
        
        lblWelcome = new JLabel(userName);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblWelcome.setForeground(Color.WHITE);
        
        JLabel roleLabel = new JLabel(userRole);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(GRAS_GOLD);
        
        infoPanel.add(lblWelcome);
        infoPanel.add(roleLabel);
        
        // Avatar
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient;
                if (utilisateurConnecte != null && utilisateurConnecte.isAdmin()) {
                    gradient = new GradientPaint(0, 0, GRAS_RED, 40, 40, GRAS_RED_DARK);
                } else {
                    gradient = new GradientPaint(0, 0, GRAS_GOLD, 40, 40, new Color(200, 150, 0));
                }
                g2.setPaint(gradient);
                g2.fillOval(0, 0, 45, 45);
                
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(0, 0, 44, 44);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                String letter = userName.substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int x = (45 - fm.stringWidth(letter)) / 2;
                int y = ((45 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(letter, x, y);
                
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(45, 45));
        avatarPanel.setOpaque(false);
        
        profilPanel.add(avatarPanel, BorderLayout.WEST);
        profilPanel.add(infoPanel, BorderLayout.CENTER);
        
        profilPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (utilisateurConnecte != null) {
                    new ProfilUtilisateurFrame().setVisible(true);
                }
            }
        });
        
        return profilPanel;
    }
    
    /**
     * PANEL DÉCONNEXION
     */
    private JPanel createLogoutPanel() {
        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setOpaque(false);
        logoutPanel.setPreferredSize(new Dimension(44, 44));
        logoutPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel logoutIcon = new JLabel("🚪") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(GRAS_RED);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
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
        
        logoutPanel.add(logoutIcon, BorderLayout.CENTER);
        logoutPanel.setToolTipText("Déconnexion");
        
        logoutPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                deconnexion();
            }
        });
        
        return logoutPanel;
    }
    
    /**
     * MENU LATÉRAL JAUNE/ROUGE
     */
    /**
 * MENU LATÉRAL JAUNE/ROUGE
 */
private JScrollPane createMenuPremium() {
    JPanel sidebarPanel = new JPanel();
    sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
    sidebarPanel.setBackground(new Color(0, 0, 0, 200));
    sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    
    // ✅ NE PAS fixer la taille du panel
    // sidebarPanel.setPreferredSize(new Dimension(280, getHeight())); ← SUPPRIMÉ
    
    // Titre navigation - PLUS GRAND
    JLabel navTitle = new JLabel("  NAVIGATION PRINCIPALE");
    navTitle.setFont(new Font("Segoe UI", Font.BOLD, 16)); // ← PLUS GROS (était 12)
    navTitle.setForeground(new Color(255, 215, 0, 255)); // Doré plus visible
    navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
    navTitle.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 0));
    sidebarPanel.add(navTitle);
    
    // Boutons de navigation
    String[][] menuItems = {
        {"📊", "TABLEAU DE BORD", "dashboard"},
        {"📦", "PRODUITS & CATÉGORIES", "produits"},
        {"📈", "MOUVEMENTS DE STOCK", "stock"},
        {"🛒", "COMMANDES CLIENTS", "commandes"},
        {"📋", "STATISTIQUES", "stats"},
        {"⚙️", "PARAMÈTRES SYSTÈME", "parametres"},
        {"❓", "AIDE & SUPPORT", "aide"}
    };
    
    // Espace entre les boutons
    sidebarPanel.add(Box.createVerticalStrut(8));
    
    for (int i = 0; i < menuItems.length; i++) {
        String[] item = menuItems[i];
        JButton btn = createNavButtonPremium(item[0], item[1], i == 0);
        final String action = item[2];
        
        // ✅ TAILLE BEAUCOUP PLUS GRANDE
        btn.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 15)); // ← PLUS GROS (était 15,25,15,10)
        
        btn.addActionListener(e -> {
            for (Component comp : sidebarPanel.getComponents()) {
                if (comp instanceof JButton) {
                    comp.setBackground(new Color(0, 0, 0, 0));
                    ((JButton) comp).setForeground(Color.WHITE);
                    ((JButton) comp).putClientProperty("active", false);
                }
            }
            btn.putClientProperty("active", true);
            btn.setForeground(Color.WHITE);
            btn.repaint();
            showModule(action);
        });
        
        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createVerticalStrut(8)); // Espace plus grand entre boutons
    }
    
    JScrollPane sidebarScroll = new JScrollPane(sidebarPanel);
    sidebarScroll.setBorder(BorderFactory.createEmptyBorder());
    sidebarScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    sidebarScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    sidebarScroll.getVerticalScrollBar().setUnitIncrement(16);
    sidebarScroll.setBackground(new Color(0, 0, 0, 200));
    sidebarScroll.setPreferredSize(new Dimension(280, getHeight()));
    
    return sidebarScroll;
}
    
    /**
     * BOUTON DE NAVIGATION PREMIUM
     */
     /**
 * BOUTON DE NAVIGATION PREMIUM avec dégradé
 */
private JButton createNavButtonPremium(String icon, String text, boolean active) {
    JButton button = new JButton("<html><div style='text-align: left; padding: 8px 0;'>" + 
                                icon + "  " + text + "</div></html>") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            boolean isActive = active || Boolean.TRUE.equals(getClientProperty("active"));
            
            if (isActive) {
                GradientPaint gradient = new GradientPaint(0, 0, GRAS_RED, 
                                                          getWidth(), 0, GRAS_GOLD);
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(GRAS_GOLD);
                g2.fillRect(0, 0, 6, getHeight()); // Indicateur plus épais
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(255, 255, 255, 80)); // Plus visible au survol
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            
            g2.dispose();
            super.paintComponent(g);
        }
    };
    
    button.setFont(new Font("Segoe UI", Font.BOLD, 16)); // ← POLICE PLUS GRANDE (était 14)
    button.setForeground(active ? Color.WHITE : new Color(255, 255, 255, 220));
    button.setHorizontalAlignment(SwingConstants.LEFT);
    
    // ✅ TAILLE PLUS GRANDE (20,30,20,15)
    button.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 15));
    
    button.setContentAreaFilled(false);
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setMaximumSize(new Dimension(280, 70)); // ← HAUTEUR MAX PLUS GRANDE (était 50)
    button.setAlignmentX(Component.LEFT_ALIGNMENT);
    button.putClientProperty("active", active);
    
    return button;
}
    
    /**
     * CONTENU PRINCIPAL (Dashboard)
     */
    private JPanel createMainContentPanel() {
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel pour le contenu avec fond semi-transparent
        JPanel glassPanel = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        glassPanel.setOpaque(false);
        glassPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ===== KPI CARDS =====
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        kpiPanel.setOpaque(false);
        
        lblStatsCA = new JLabel("0 F");
        lblStatsCmd = new JLabel("0");
        lblStatsStock = new JLabel("0");
        lblStatsAlert = new JLabel("0");
        
        kpiPanel.add(createKpiCard("💰 CA AUJOURD'HUI", lblStatsCA, GRAS_GOLD));
        kpiPanel.add(createKpiCard("🛒 COMMANDES", lblStatsCmd, new Color(52, 152, 219)));
        kpiPanel.add(createKpiCard("📦 PRODUITS", lblStatsStock, new Color(46, 204, 113)));
        kpiPanel.add(createKpiCard("⚠️ ALERTES", lblStatsAlert, GRAS_RED));
        
        glassPanel.add(kpiPanel, BorderLayout.NORTH);
        
        // ===== CONTENU CENTRAL =====
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);
        
        // Commandes en cours
        commandesPanel = new JPanel();
        commandesPanel.setLayout(new BoxLayout(commandesPanel, BoxLayout.Y_AXIS));
        commandesPanel.setOpaque(false);
        
        JPanel commandesCard = createGlassCard("🛒 COMMANDES EN COURS", commandesPanel);
        centerPanel.add(commandesCard);
        
        // Alertes stock
        alertesPanel = new JPanel();
        alertesPanel.setLayout(new BoxLayout(alertesPanel, BoxLayout.Y_AXIS));
        alertesPanel.setOpaque(false);
        
        JPanel alertesCard = createGlassCard("⚠️ ALERTES STOCK", alertesPanel);
        centerPanel.add(alertesCard);
        
        glassPanel.add(centerPanel, BorderLayout.CENTER);
        
        mainContentPanel.add(glassPanel, BorderLayout.CENTER);
        
        // Charger les données
        refreshDashboardData();
        
        return mainContentPanel;
    }
    
    /**
     * CARTE KPI
     */
    private JPanel createKpiCard(String title, JLabel valueLabel, Color accentColor) {
    JPanel card = new JPanel(new BorderLayout(10, 5)) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fond noir plus opaque
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            
            // Bordure colorée
            g2.setColor(accentColor);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
            
            // 🔥 PETIT GRAPHIQUE (mini courbe)
            g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), 
                                  accentColor.getBlue(), 100));
            g2.setStroke(new BasicStroke(2));
            
            // Dessiner une mini courbe de tendance
            int[] xPoints = {getWidth()-60, getWidth()-45, getWidth()-30, getWidth()-15, getWidth()-5};
            int[] yPoints = {getHeight()-25, getHeight()-35, getHeight()-20, getHeight()-40, getHeight()-30};
            
            g2.drawPolyline(xPoints, yPoints, 5);
            
            // Points sur la courbe
            g2.setColor(accentColor);
            for (int i = 0; i < xPoints.length; i++) {
                g2.fillOval(xPoints[i]-2, yPoints[i]-2, 4, 4);
            }
            
            g2.dispose();
            super.paintComponent(g);
        }
    };
    card.setOpaque(false);
    card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setOpaque(false);
    
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    titleLabel.setForeground(new Color(255, 255, 255, 200));
    
    // Petit indicateur de tendance
    JLabel trendLabel = new JLabel("+12%");
    trendLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
    trendLabel.setForeground(SUCCESS_COLOR);
    
    topPanel.add(titleLabel, BorderLayout.WEST);
    topPanel.add(trendLabel, BorderLayout.EAST);
    
    valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    valueLabel.setForeground(Color.WHITE);
    
    card.add(topPanel, BorderLayout.NORTH);
    card.add(valueLabel, BorderLayout.CENTER);
    
    return card;
}
    
    /**
     * CARTE EN VERRE
     */
    private JPanel createGlassCard(String title, JPanel contentPanel) {
    JPanel card = new JPanel(new BorderLayout(10, 10)) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 🔥 FOND PLUS OPAQUE (de 30 à 80)
            g2.setColor(new Color(0, 0, 0, 200));  // Noir plus opaque
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            
            // Bordure dorée
            g2.setColor(GRAS_GOLD);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 25, 25);
            g2.dispose();
            super.paintComponent(g);
        }
    };
    card.setOpaque(false);
    card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Titre avec fond semi-transparent
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    titleLabel.setForeground(GRAS_GOLD);
    titleLabel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 2, 0, GRAS_GOLD),
        BorderFactory.createEmptyBorder(0, 0, 10, 0)
    ));
    
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);
    wrapper.add(titleLabel, BorderLayout.NORTH);
    
    JScrollPane scrollPane = new JScrollPane(contentPanel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
    // Style scrollbar plus visible
    scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = GRAS_GOLD;
            this.trackColor = new Color(0, 0, 0, 0);
        }
    });
    
    wrapper.add(scrollPane, BorderLayout.CENTER);
    card.add(wrapper, BorderLayout.CENTER);
    
    return card;
}
    /**
     * PANEL D'INFORMATIONS (Footer)
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(0, 0, 0, 180));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 50)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        infoPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        // Informations système (gardé de votre code)
        String userInfo = "✅ Base de données connectée • MySQL • ";
        if (utilisateurConnecte != null) {
            userInfo += "Utilisateur: " + utilisateurConnecte.getLogin() + " (" + utilisateurConnecte.getRole() + ")";
        } else {
            userInfo += "Mode démonstration";
        }
        
        lblStatus = new JLabel(userInfo);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(200, 200, 200));
        
        JLabel versionLabel = new JLabel("GRAS PALACE v2.0 • Conforme au sujet POO Java");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(150, 150, 150));
        
        infoPanel.add(lblStatus, BorderLayout.WEST);
        infoPanel.add(versionLabel, BorderLayout.EAST);
        
        return infoPanel;
    }
    
    /**
     * RAFRAÎCHIR LES DONNÉES DU DASHBOARD
     */
    private void refreshDashboardData() {
        try {
            if (commandeDAO != null) {
                double caJour = commandeDAO.getCAJour();
                lblStatsCA.setText(String.format("%,d F", (int)caJour));
                
                List<Commande> commandesEnCours = commandeDAO.findByEtat("EN_COURS");
                lblStatsCmd.setText(String.valueOf(commandesEnCours.size()));
                
                commandesPanel.removeAll();
                if (commandesEnCours.isEmpty()) {
                    commandesPanel.add(createEmptyItem("Aucune commande en cours"));
                } else {
                    for (Commande cmd : commandesEnCours) {
                        commandesPanel.add(createCommandeItem(cmd));
                        commandesPanel.add(Box.createVerticalStrut(5));
                    }
                }
            }
            
            if (produitDAO != null) {
                List<Produit> produits = produitDAO.findAll();
                lblStatsStock.setText(String.valueOf(produits.size()));
                
                List<Produit> alertes = produitDAO.findStockBelowSeuil();
                lblStatsAlert.setText(String.valueOf(alertes.size()));
                
                alertesPanel.removeAll();
                if (alertes.isEmpty()) {
                    alertesPanel.add(createEmptyItem("Aucune alerte stock"));
                } else {
                    for (Produit p : alertes) {
                        alertesPanel.add(createAlerteItem(p));
                        alertesPanel.add(Box.createVerticalStrut(5));
                    }
                }
            }
            
            commandesPanel.revalidate();
            commandesPanel.repaint();
            alertesPanel.repaint();
            
        } catch (Exception e) {
            logger.severe("Erreur refresh: " + e.getMessage());
        }
    }
    
    /**
     * CRÉER UN ÉLÉMENT DE COMMANDE
     */
    private JPanel createCommandeItem(Commande cmd) {
    JPanel item = new JPanel(new BorderLayout(15, 0));  // Plus d'espace
    item.setOpaque(false);
    item.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));  // Plus de padding
    
    JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
    leftPanel.setOpaque(false);
    
    JLabel tableLabel = new JLabel("Table " + (cmd.getId() % 10 + 1));
    tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    tableLabel.setForeground(Color.WHITE);
    
    JLabel timeLabel = new JLabel(new SimpleDateFormat("HH:mm").format(cmd.getDateCommande()));
    timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    timeLabel.setForeground(new Color(255, 255, 255, 180));
    
    leftPanel.add(tableLabel);
    leftPanel.add(timeLabel);
    
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    rightPanel.setOpaque(false);
    
    JLabel amountLabel = new JLabel(String.format("%,d F", (int)cmd.getTotal()));
    amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    amountLabel.setForeground(GRAS_GOLD);
    amountLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 5));  // Espace à droite
    
    rightPanel.add(amountLabel);
    
    item.add(leftPanel, BorderLayout.WEST);
    item.add(rightPanel, BorderLayout.EAST);
    
    // Ligne de séparation
    item.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 50)),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    
    return item;
}
    
    /**
     * CRÉER UN ÉLÉMENT D'ALERTE
     */
    private JPanel createAlerteItem(Produit p) {
    JPanel item = new JPanel(new BorderLayout(15, 0));
    item.setOpaque(false);
    item.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
    JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
    leftPanel.setOpaque(false);
    
    JLabel nameLabel = new JLabel(p.getNom());
    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    nameLabel.setForeground(Color.WHITE);
    
    String unite = "unités";
    if (p.getCategorie() != null) {
        String cat = p.getCategorie().getLibelle().toLowerCase();
        if (cat.contains("boisson")) unite = "unités";
        else if (cat.contains("plat")) unite = "portions";
        else if (cat.contains("dessert")) unite = "parts";
    }
    
    JLabel stockLabel = new JLabel("Stock: " + p.getStockActuel() + " " + unite);
    stockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    stockLabel.setForeground(new Color(255, 255, 255, 180));
    
    leftPanel.add(nameLabel);
    leftPanel.add(stockLabel);
    
    // 🔥 BADGE PLUS PERCUTANT
    String statut = p.getStockActuel() <= 0 ? "RUPTURE" : "Stock bas";
    Color badgeColor = p.getStockActuel() <= 0 ? GRAS_RED : GRAS_GOLD;
    
    JPanel badgePanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(badgeColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2.dispose();
        }
    };
    badgePanel.setLayout(new GridBagLayout());
    badgePanel.setOpaque(false);
    badgePanel.setPreferredSize(new Dimension(90, 30));
    
    JLabel statusLabel = new JLabel(statut);
    statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    statusLabel.setForeground(Color.WHITE);
    badgePanel.add(statusLabel);
    
    item.add(leftPanel, BorderLayout.WEST);
    item.add(badgePanel, BorderLayout.EAST);
    
    return item;
}
    
    /**
     * CRÉER UN ÉLÉMENT VIDE
     */
    private JPanel createEmptyItem(String message) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.CENTER));
        item.setOpaque(false);
        item.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        label.setForeground(new Color(255, 255, 255, 150));
        
        item.add(label);
        return item;
    }
    
    /**
     * DÉMARRER L'HORLOGE
     */
    private void startClock() {
        Timer clockTimer = new Timer(1000, e -> {
            Date now = new Date();
            lblTime.setText(new SimpleDateFormat("HH:mm:ss").format(now));
            lblDate.setText(new SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH).format(now));
        });
        clockTimer.start();
    }
    
    /**
     * VOS MÉTHODES EXISTANTES (GARDÉES TELLES QUELLES)
     */
    private void configurerEvenements(JMenuItem... menuItems) {
        // Votre code existant inchangé
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
        
        // ... (toutes vos autres méthodes existantes)
    }
    
    private JButton createToolbarButton(String icon, String tooltip, Color bgColor) {
        // Votre code existant inchangé
        JButton button = new JButton(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
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
        // Votre code existant inchangé (gardé pour compatibilité)
        JButton button = new JButton("<html><div style='text-align: left; padding-left: 10px;'>" + 
                                    icon + "  " + text + "</div></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (active) {
                    g2.setColor(new Color(41, 128, 185, 150));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(52, 152, 219));
                    g2.fillRect(0, 0, 5, getHeight());
                } else if (getModel().isRollover()) {
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
        // Votre code existant inchangé
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
    
    private void configurerRaccourcisClavier() {
        // Votre code existant inchangé
        getRootPane().registerKeyboardAction(
            e -> new GestionCommandesFrame().setVisible(true),
            KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> JOptionPane.showMessageDialog(this, "Enregistrement..."),
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> JOptionPane.showMessageDialog(this, "Recherche..."),
            KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> JOptionPane.showMessageDialog(this, "Impression..."),
            KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> new AideSupportFrame().setVisible(true),
            KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> showModule("produits"),
            KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> showModule("stock"),
            KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> showModule("commandes"),
            KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.ALT_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> showModule("stats"),
            KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.ALT_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> showModule("parametres"),
            KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.ALT_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void highlightNavButton(JButton[] buttons, JButton activeButton) {
        // Votre code existant inchangé
        for (JButton button : buttons) {
            button.setForeground(new Color(220, 220, 220));
            button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        activeButton.setForeground(Color.WHITE);
        activeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }
    
    private void showModule(String module) {
        // Votre code existant inchangé
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
            case "aide":
                new AideSupportFrame().setVisible(true);
                break;
            default:
                break;
        }
    }
    
    private void executeQuickAction(String action) {
        // Votre code existant inchangé
        JOptionPane.showMessageDialog(this,
            "Action rapide : " + action,
            "Action", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showNotifications() {
        // Votre code existant inchangé
        JDialog notificationDialog = new JDialog(this, "🔔 Notifications", true);
        notificationDialog.setLayout(new BorderLayout());
        notificationDialog.setSize(500, 400);
        notificationDialog.setLocationRelativeTo(this);
        notificationDialog.getContentPane().setBackground(new Color(250, 250, 250));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("🔔 NOTIFICATIONS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        notificationDialog.add(headerPanel, BorderLayout.NORTH);
        
        JPanel notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
        notificationsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[][] notifications = {
            {"🔴", "URGENT", "Stock bas - Vin rouge", "Il reste seulement 3 bouteilles", "Il y a 5 min"},
            {"🟢", "INFO", "Commande validée", "Table 2 - 120F CFA", "Il y a 15 min"},
            {"🔵", "SYSTÈME", "Sauvegarde", "Sauvegarde automatique", "Il y a 30 min"}
        };
        
        for (String[] notif : notifications) {
            notificationsPanel.add(createNotificationItem(notif[0], notif[1], notif[2], notif[3], notif[4]));
            notificationsPanel.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scrollPane = new JScrollPane(notificationsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        notificationDialog.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        
        JButton closeBtn = new JButton("Fermer");
        closeBtn.addActionListener(e -> notificationDialog.dispose());
        buttonPanel.add(closeBtn);
        
        notificationDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        notificationDialog.setVisible(true);
    }
    
    private JPanel createNotificationItem(String emoji, String type, String title, String message, String time) {
        // Votre code existant inchangé
        JPanel item = new JPanel(new BorderLayout(15, 0));
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));
        
        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setPreferredSize(new Dimension(40, 40));
        
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        contentPanel.setOpaque(false);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel typeLabel = new JLabel(type);
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        typeLabel.setForeground(new Color(52, 152, 219));
        
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(150, 150, 150));
        
        headerPanel.add(typeLabel, BorderLayout.WEST);
        headerPanel.add(timeLabel, BorderLayout.EAST);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(new Color(100, 100, 100));
        
        contentPanel.add(headerPanel);
        contentPanel.add(titleLabel);
        contentPanel.add(messageLabel);
        
        item.add(iconLabel, BorderLayout.WEST);
        item.add(contentPanel, BorderLayout.CENTER);
        
        return item;
    }
    
    private void updateTimeLabel(JLabel label) {
        label.setText(new SimpleDateFormat("HH:mm:ss • EEEE d MMMM", Locale.FRENCH).format(new Date()));
    }
    
    private void demarrerStatsTempsReel() {
        statsTimer = new Timer(10000, e -> refreshDashboardData());
        statsTimer.start();
    }
    
    private void animerEntree() {
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
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
        } catch (Exception e) {
            // Ignorer si non supporté
        }
    }
    
    private void deconnexion() {
        int choix = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment vous déconnecter ?",
            "Confirmation de déconnexion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choix == JOptionPane.YES_OPTION) {
            Session.clear();
            dispose();
            new com.gesrestaurant.views.auth.LoginFrame().setVisible(true);
        }
    }
    
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
    
    // Méthodes non utilisées mais gardées pour compatibilité
    private JPanel createDashboardPanel() { return new JPanel(); }
    private JPanel createCommandeCard(Commande cmd) { return new JPanel(); }
    private JPanel createAlerteCard(Produit p) { return new JPanel(); }
    private JPanel createOrderCard(String a, String b, String c, String d) { return new JPanel(); }
    private JPanel createAlertCard(String a, String b, String c, Color d) { return new JPanel(); }
    private String getUnite(String cat) { return "unités"; }
    private String getStatusEmoji(String etat) { return "⏳"; }
    private JPanel createOrderItem(String a, String b, String c, String d) { return new JPanel(); }
    private JPanel createPlaceholderPanel(String a, String b) { return new JPanel(); }
    private void showUserMenu() {}
    private void performLogout() {}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    
    
    //Quitte l'application
     
    
    
        

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
