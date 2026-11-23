package presentation;

import domain.Character;
import domain.GoalMatch;
import domain.Log;
import domain.MatchType;
import domain.PlayerGame;
import domain.SoccerException;
import domain.SoccerGame;
import domain.TimeMatch;
import domain.CPUController;
import domain.ClumsyCPU;
import domain.DefensiveCPU;
import domain.ExpertCPU;
import domain.OffensiveCPU;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.plaf.basic.BasicProgressBarUI;


/**
 * Interfaz grafica principal de DOPOSoccer siguiendo el patron P(V+C+I) + Dominio.
 */
public class SoccerGUI extends JFrame {

    private SoccerGame game;

    // Contenedor de pantallas (Configuracion y Partido)
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private StartPanel startPanel;

    // Atributos del menu
    private JMenuItem itemNew;
    private JMenuItem itemConfig;
    private JMenuItem itemOpen;
    private JMenuItem itemSave;
    private JMenuItem itemExit;
    private static final File PROGRESS_FILE = new File("progreso.dat");
    private boolean vsMachine;

    // Pantallas de modo y de preparacion del partido
    private ModePanel modePanel;
    private SetupPanel setupPanel;
    private int characterP1;
    private int characterP2;

    // Atributos de la vista de Juego (S15)
    private FieldCanvas fieldCanvas;
    private JLabel lblScoreBoard;
    private JLabel lblMatchTimer;
    private JProgressBar barHealthP1;
    private JProgressBar barPowerP1;
    private JProgressBar barHealthP2;
    private JProgressBar barPowerP2;
    private JButton btnPause;
    private JButton btnEndMatch;
    private JButton btnBackToConfig;

    private Timer gameLoopTimer;
    private boolean leftP1;
    private boolean rightP1;
    private boolean leftP2;
    private boolean rightP2;

    /**
     * Constructor privado de la interfaz grafica.
     */
    private SoccerGUI() {
        super("DOPOSoccer 2026-2");
        initDefaultDomain();
        prepareView();
        prepareController();
        refresh();
    }

    private void initDefaultDomain() {
        this.characterP1 = 0;
        this.characterP2 = 1;
        this.vsMachine = false;
        try {
            MatchType defaultMatch = new TimeMatch(2);
            this.game = new SoccerGame(
                2,
                defaultMatch,
                Character.createSouthKorea(),
                Character.createCameroon()
            );
            if (PROGRESS_FILE.exists()) {
                this.game.loadProgress(PROGRESS_FILE);
            }
        } catch (SoccerException e) {
            Log.record(e);
        }
    }

    /**
     * Prepara todos los elementos visuales de la ventana.
     */
    private void prepareView() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screen.width * 0.80), (int) (screen.height * 0.82));
        setLocationRelativeTo(null);

        prepareViewMenu();

        this.cardLayout = new CardLayout();
        this.mainContainer = new JPanel(this.cardLayout);

        this.startPanel = new StartPanel();
        this.mainContainer.add(this.startPanel, "START");
        this.modePanel = new ModePanel();
        this.mainContainer.add(this.modePanel, "MODE");
        this.setupPanel = new SetupPanel(ImageLoader.loadHeads());
        this.mainContainer.add(this.setupPanel, "CONFIG");
        this.mainContainer.add(prepareViewGame(), "GAME");

        add(this.mainContainer, BorderLayout.CENTER);
    }

    private void prepareViewMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Partido");

        this.itemNew = new JMenuItem("Nuevo Partido");
        this.itemConfig = new JMenuItem("Configuración y Mejoras");
        this.itemOpen = new JMenuItem("Abrir Partida...");
        this.itemSave = new JMenuItem("Salvar Partida...");
        this.itemExit = new JMenuItem("Salir");

        fileMenu.add(this.itemNew);
        fileMenu.add(this.itemConfig);
        fileMenu.addSeparator();
        fileMenu.add(this.itemOpen);
        fileMenu.add(this.itemSave);
        fileMenu.addSeparator();
        fileMenu.add(this.itemExit);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private JPanel prepareViewGame() {
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(new Color(20, 25, 40));

        // HUD Superior: Vida, Poder, Marcador y Tiempo
        JPanel hudPanel = new JPanel(new GridLayout(1, 3, 20, 5));
        hudPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        hudPanel.setBackground(new Color(20, 25, 40));

        JPanel hudP1 = new JPanel(new GridLayout(2, 1, 0, 6));
        hudP1.setOpaque(false);
        this.barHealthP1 = createHudBar(190, new Color(46, 184, 92));
        this.barPowerP1 = createHudBar(100, new Color(41, 128, 185));
        hudP1.add(this.barHealthP1);
        hudP1.add(this.barPowerP1);

        JPanel hudCenter = new JPanel(new GridLayout(2, 1));
        hudCenter.setOpaque(false);
        this.lblScoreBoard = new JLabel("0 - 0", SwingConstants.CENTER);
        this.lblScoreBoard.setFont(new Font(GameStyle.FONT_NAME, Font.BOLD, 24));
        this.lblScoreBoard.setForeground(Color.WHITE);
        this.lblMatchTimer = new JLabel("Tiempo: 02:00", SwingConstants.CENTER);
        this.lblMatchTimer.setFont(new Font("SansSerif", Font.BOLD, 14));
        this.lblMatchTimer.setForeground(GameStyle.YELLOW);
        hudCenter.add(this.lblScoreBoard);
        hudCenter.add(this.lblMatchTimer);

        JPanel hudP2 = new JPanel(new GridLayout(2, 1, 0, 6));
        hudP2.setOpaque(false);
        this.barHealthP2 = createHudBar(190, new Color(46, 184, 92));
        this.barPowerP2 = createHudBar(100, new Color(41, 128, 185));
        hudP2.add(this.barHealthP2);
        hudP2.add(this.barPowerP2);

        hudPanel.add(hudP1);
        hudPanel.add(hudCenter);
        hudPanel.add(hudP2);
        gamePanel.add(hudPanel, BorderLayout.NORTH);

        // Centro: Cancha 2D ocupando todo el espacio sin marco negro
        this.fieldCanvas = new FieldCanvas(this.game);
        gamePanel.add(this.fieldCanvas, BorderLayout.CENTER);

        // Sur: Barra oscura coherente con el estadio y botones con GameStyle
        JPanel southBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        southBar.setBackground(new Color(20, 25, 40));

        this.btnPause = GameStyle.createButton("Pausar / Reanudar", GameStyle.BLUE, 175, 34);
        this.btnEndMatch = GameStyle.createButton("Terminar Partido", GameStyle.RED, 165, 34);
        this.btnBackToConfig = GameStyle.createButton("Configuración", GameStyle.GRAY, 145, 34);

        JLabel lblControls = new JLabel(
            "P1: A/D Mover · W Saltar · S Patear · E Poder   |   P2: ←/→ Mover · ↑ Saltar · ↓ Patear · ENTER Poder"
        );
        lblControls.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblControls.setForeground(GameStyle.YELLOW);

        southBar.add(this.btnPause);
        southBar.add(this.btnEndMatch);
        southBar.add(this.btnBackToConfig);
        southBar.add(lblControls);
        gamePanel.add(southBar, BorderLayout.SOUTH);

        return gamePanel;
    }

    private JProgressBar createHudBar(int max, Color fillColor) {
        JProgressBar bar = new JProgressBar(0, max);
        bar.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() {
                return Color.WHITE;
            }

            @Override
            protected Color getSelectionForeground() {
                return Color.WHITE;
            }
        });
        bar.setStringPainted(true);
        bar.setFont(new Font("SansSerif", Font.BOLD, 12));
        bar.setForeground(fillColor);
        bar.setBackground(new Color(45, 52, 72));
        bar.setBorder(BorderFactory.createLineBorder(new Color(90, 105, 135), 2));
        bar.setPreferredSize(new Dimension(220, 24));
        return bar;
    }

    private void applyGameWindowSize() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int targetW = Math.min(this.game.getField().getWidth(), screen.width - 40);
        int targetH = Math.min(this.game.getField().getHeight() + 145, screen.height - 60);
        setSize(targetW, targetH);
        setLocationRelativeTo(null);
    }

    /**
     * Registra todos los oyentes de la ventana, menu, configuracion y teclado sin metodos vacios.
     */
    private void prepareController() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                optionExit();
            }
        });
        prepareControllerStart();
        prepareControllerMode();
        prepareControllerMenu();
        prepareControllerConfig();
        prepareControllerGame();
    }

    private void prepareControllerStart() {
        this.startPanel.getPlayButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainContainer, "MODE");
            }
        });

        this.startPanel.getExitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionExit();
            }
        });
    }

    private void prepareControllerMode() {
        this.modePanel.getPlayerVsPlayerButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vsMachine = false;
                setupPanel.setMachineMode(false);
                cardLayout.show(mainContainer, "CONFIG");
            }
        });

        this.modePanel.getPlayerVsMachineButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vsMachine = true;
                setupPanel.setMachineMode(true);
                cardLayout.show(mainContainer, "CONFIG");
            }
        });

        this.modePanel.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainContainer, "START");
            }
        });
    }

    private void prepareControllerMenu() {
        this.itemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionStartMatch();
            }
        });

        this.itemConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionShowConfig();
            }
        });

        this.itemOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionOpen();
            }
        });

        this.itemSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionSave();
            }
        });

        this.itemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionExit();
            }
        });
    }

    private void prepareControllerConfig() {
        this.setupPanel.getPlayButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionStartMatch();
            }
        });

        this.setupPanel.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainContainer, "MODE");
            }
        });

        for (int side = 1; side <= 2; side++) {
            PlayerSetupPanel panel = this.setupPanel.getPlayerPanel(side);
            registerCharacterButton(panel.getPreviousButton(), side, -1);
            registerCharacterButton(panel.getNextButton(), side, 1);
            for (int i = 0; i < PlayerSetupPanel.STAT_COUNT; i++) {
                registerUpgradeButton(panel.getStatButton(i), side, PlayerSetupPanel.getStatName(i));
            }
        }
    }

    private void registerCharacterButton(JButton button, final int side, final int step) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionChangeCharacter(side, step);
            }
        });
    }

    private void registerUpgradeButton(JButton button, final int side, final String statName) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionUpgradeStat(side, statName);
            }
        });
    }

    private void prepareControllerGame() {
        this.btnPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionTogglePause();
            }
        });

        this.btnEndMatch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionEndMatchNow();
            }
        });

        this.btnBackToConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionShowConfig();
            }
        });

        // Uso de KeyAdapter para implementar solo keyPressed y keyReleased sin metodos vacios
        this.fieldCanvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyReleased(e.getKeyCode());
            }
        });

        this.gameLoopTimer = new Timer(25, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepGameLoop();
            }
        });
    }

    private void handleKeyPressed(int code) {
        PlayerGame p1 = this.game.getPlayer1();
        PlayerGame p2 = this.game.getPlayer2();
        double groundY = this.game.getField().getGroundY();

        if (code == KeyEvent.VK_A) {
            this.leftP1 = true;
        } else if (code == KeyEvent.VK_D) {
            this.rightP1 = true;
        } else if (code == KeyEvent.VK_W) {
            p1.jump(groundY);
        } else if (code == KeyEvent.VK_S) {
            this.game.playerKick(1);
        } else if (code == KeyEvent.VK_E) {
            tryActivatePower(p1);
        } else if (!this.game.isVsMachine()) {
            if (code == KeyEvent.VK_LEFT) {
                this.leftP2 = true;
            } else if (code == KeyEvent.VK_RIGHT) {
                this.rightP2 = true;
            } else if (code == KeyEvent.VK_UP) {
                p2.jump(groundY);
            } else if (code == KeyEvent.VK_DOWN) {
                this.game.playerKick(2);
            } else if (code == KeyEvent.VK_ENTER) {
                tryActivatePower(p2);
            }
        }
    }

    private void handleKeyReleased(int code) {
        if (code == KeyEvent.VK_A) {
            this.leftP1 = false;
        } else if (code == KeyEvent.VK_D) {
            this.rightP1 = false;
        } else if (code == KeyEvent.VK_LEFT) {
            this.leftP2 = false;
        } else if (code == KeyEvent.VK_RIGHT) {
            this.rightP2 = false;
        }
    }

    private void tryActivatePower(PlayerGame player) {
        try {
            player.activatePower();
        } catch (SoccerException ex) {
            // No interrumpe el partido con pop-up durante la accion en tiempo real
            Log.record(ex);
        }
    }

    private void stepGameLoop() {
        int dir1 = 0;
        if (this.leftP1 && !this.rightP1) {
            dir1 = -1;
        } else if (this.rightP1 && !this.leftP1) {
            dir1 = 1;
        }
        this.game.getPlayer1().moveHorizontal(dir1);

        if (!this.game.isVsMachine()) {
            int dir2 = 0;
            if (this.leftP2 && !this.rightP2) {
                dir2 = -1;
            } else if (this.rightP2 && !this.leftP2) {
                dir2 = 1;
            }
            this.game.getPlayer2().moveHorizontal(dir2);
        }

        this.game.update(0.025);
        refresh();

        if (this.game.isFinished()) {
            this.gameLoopTimer.stop();
            savePlayerProgress();
            announceWinner();
        }
    }

    private void optionStartMatch() {
        try {
            int resOption = this.setupPanel.getResolutionCombo().getSelectedIndex() + 1;
            int value = Integer.parseInt(this.setupPanel.getMatchValueField().getText().trim());
            MatchType matchType;
            if (this.setupPanel.getMatchTypeCombo().getSelectedIndex() == 0) {
                matchType = new TimeMatch(value);
            } else {
                matchType = new GoalMatch(value);
            }

            if (this.vsMachine) {
                this.game.setCpuController(createSelectedCPU(this.setupPanel.getCpuProfileCombo().getSelectedIndex()));
            } else {
                this.game.setCpuController(null);
            }

            this.game.getField().setResolution(resOption);
            this.game.startNewMatch(matchType);

            this.fieldCanvas.updatePreferredSize();
            applyGameWindowSize();
            this.cardLayout.show(this.mainContainer, "GAME");
            this.fieldCanvas.requestFocusInWindow();
            this.gameLoopTimer.start();
            refresh();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Ingrese un número entero válido para los minutos o goles.",
                "Configuración inválida",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (SoccerException ex) {
            Log.record(ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private CPUController createSelectedCPU(int index) {
        CPUController cpu;
        if (index == 0) {
            cpu = new ClumsyCPU();
        } else if (index == 1) {
            cpu = new OffensiveCPU();
        } else if (index == 2) {
            cpu = new DefensiveCPU();
        } else {
            cpu = new ExpertCPU();
        }
        return cpu;
    }

    private Character createSelectedCharacter(int index) {
        Character[] catalog = Character.getCatalog();
        int safeIndex = ((index % catalog.length) + catalog.length) % catalog.length;
        return catalog[safeIndex];
    }

    /**
     * Cambia el personaje de un jugador al anterior o al siguiente de la lista.
     * @param side jugador (1 o 2).
     * @param step -1 para el anterior, 1 para el siguiente.
     */
    private void optionChangeCharacter(int side, int step) {
        int total = Character.getCatalog().length;
        if (side == 1) {
            this.characterP1 = (this.characterP1 + step + total) % total;
            this.game.getPlayer1().setCharacter(createSelectedCharacter(this.characterP1));
        } else {
            this.characterP2 = (this.characterP2 + step + total) % total;
            this.game.getPlayer2().setCharacter(createSelectedCharacter(this.characterP2));
        }
        refresh();
    }

    private void optionUpgradeStat(int side, String statName) {
        try {
            PlayerGame target = (side == 1) ? this.game.getPlayer1() : this.game.getPlayer2();
            target.upgradeStat(statName);
            savePlayerProgress();
            refresh();
        } catch (SoccerException ex) {
            Log.record(ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Mejora de Estadística", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void optionTogglePause() {
        this.game.setPaused(!this.game.isPaused());
        this.fieldCanvas.requestFocusInWindow();
        refresh();
    }

    private void optionEndMatchNow() {
        this.gameLoopTimer.stop();
        this.game.finishMatch();
        savePlayerProgress();
        announceWinner();
        optionShowConfig();
    }

    private void savePlayerProgress() {
        try {
            this.game.saveProgress(PROGRESS_FILE);
        } catch (SoccerException ex) {
            Log.record(ex);
        }
    }

    private void optionShowConfig() {
        this.gameLoopTimer.stop();
        this.leftP1 = false;
        this.rightP1 = false;
        this.leftP2 = false;
        this.rightP2 = false;
        refresh();
        this.cardLayout.show(this.mainContainer, "CONFIG");
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screen.width * 0.80), (int) (screen.height * 0.82));
        setLocationRelativeTo(null);
    }

    private void announceWinner() {
        PlayerGame winner = this.game.getMatchType().getWinner(this.game);
        String msg;
        if (winner == this.game.getPlayer1()) {
            msg = "¡Ganó el Jugador 1 (" + winner.getCharacter().getCountry() + ")!\n"
                + "Jugador 1 recibe +100 puntos.\nJugador 2 recibe +25 puntos.";
        } else if (winner == this.game.getPlayer2()) {
            msg = "¡Ganó el Jugador 2 (" + winner.getCharacter().getCountry() + ")!\n"
                + "Jugador 2 recibe +100 puntos.\nJugador 1 recibe +25 puntos.";
        } else {
            msg = "El partido finalizó sin ganador.";
        }
        JOptionPane.showMessageDialog(this, msg, "Resultado del Partido", JOptionPane.INFORMATION_MESSAGE);
        refresh();
    }

    private void optionOpen() {
        boolean wasRunning = this.gameLoopTimer.isRunning();
        this.gameLoopTimer.stop();

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Partidas DOPOSoccer (*.dat)", "dat"));
        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                this.game.open(file);
                this.vsMachine = this.game.isVsMachine();
                this.setupPanel.setMachineMode(this.vsMachine);
                this.characterP1 = findCharacterIndex(this.game.getPlayer1().getCharacter().getCountry());
                this.characterP2 = findCharacterIndex(this.game.getPlayer2().getCharacter().getCountry());
                this.fieldCanvas.setGame(this.game);
                applyGameWindowSize();
                this.cardLayout.show(this.mainContainer, "GAME");
                this.fieldCanvas.requestFocusInWindow();
                refresh();
                JOptionPane.showMessageDialog(
                    this,
                    "Partida cargada correctamente desde: " + file.getName(),
                    "Abrir Partida",
                    JOptionPane.INFORMATION_MESSAGE
                );
                if (!this.game.isFinished()) {
                    this.gameLoopTimer.start();
                }
            } catch (SoccerException ex) {
                Log.record(ex);
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al abrir partida", JOptionPane.ERROR_MESSAGE);
                if (wasRunning) {
                    this.gameLoopTimer.start();
                }
            }
        } else if (wasRunning) {
            this.gameLoopTimer.start();
        }
    }

    private void optionSave() {
        boolean wasRunning = this.gameLoopTimer.isRunning();
        this.gameLoopTimer.stop();

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Partidas DOPOSoccer (*.dat)", "dat"));
        int option = chooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                this.game.saveAs(file);
                JOptionPane.showMessageDialog(
                    this,
                    "Partida guardada correctamente en: " + file.getName(),
                    "Salvar Partida",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (SoccerException ex) {
                Log.record(ex);
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al salvar partida", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (wasRunning) {
            this.gameLoopTimer.start();
            this.fieldCanvas.requestFocusInWindow();
        }
    }

    private void optionExit() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea salir de DOPOSoccer?",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION
        );
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private int findCharacterIndex(String country) {
        Character[] catalog = Character.getCatalog();
        int found = 0;
        for (int i = 0; i < catalog.length; i++) {
            if (catalog[i].getCountry().equals(country)) {
                found = i;
            }
        }
        return found;
    }

    /**
     * Sincroniza todos los componentes visuales con el estado actual de SoccerGame.
     */
    private void refresh() {
        PlayerGame p1 = this.game.getPlayer1();
        PlayerGame p2 = this.game.getPlayer2();

        this.setupPanel.getPlayerPanel(1).update(p1);
        this.setupPanel.getPlayerPanel(2).update(p2);

        this.lblScoreBoard.setText(
            p1.getCharacter().getCountry() + "  "
            + this.game.getMarker().getGoals1() + " - " + this.game.getMarker().getGoals2()
            + "  " + p2.getCharacter().getCountry()
        );

        MatchType mt = this.game.getMatchType();
        if (mt instanceof TimeMatch) {
            TimeMatch tm = (TimeMatch) mt;
            int totalSec = (int) Math.ceil(tm.getRemainingSeconds());
            int min = totalSec / 60;
            int sec = totalSec % 60;
            String stateText = tm.isGoldenGoal()
                    ? "¡MUERTE SÚBITA (Gol de Oro)!"
                    : String.format("Tiempo restante: %02d:%02d", min, sec);
            this.lblMatchTimer.setText(stateText);
        } else if (mt instanceof GoalMatch) {
            GoalMatch gm = (GoalMatch) mt;
            this.lblMatchTimer.setText("Meta para ganar: " + gm.getTargetGoals() + " goles");
        }

        this.barHealthP1.setMaximum((int) p1.getMaxHealth());
        this.barHealthP1.setValue((int) p1.getHealth());
        this.barHealthP1.setString("Vida P1: " + (int) p1.getHealth() + "/" + (int) p1.getMaxHealth());

        this.barPowerP1.setValue((int) p1.getPowerBar().getCharge());
        this.barPowerP1.setString("Poder P1: " + (int) p1.getPowerBar().getCharge() + "%");
        this.barPowerP1.setForeground(p1.getPowerBar().isFull() ? new Color(230, 160, 20) : new Color(41, 128, 185));

        this.barHealthP2.setMaximum((int) p2.getMaxHealth());
        this.barHealthP2.setValue((int) p2.getHealth());
        this.barHealthP2.setString("Vida P2: " + (int) p2.getHealth() + "/" + (int) p2.getMaxHealth());

        this.barPowerP2.setValue((int) p2.getPowerBar().getCharge());
        this.barPowerP2.setString("Poder P2: " + (int) p2.getPowerBar().getCharge() + "%");
        this.barPowerP2.setForeground(p2.getPowerBar().isFull() ? new Color(230, 160, 20) : new Color(41, 128, 185));
        
        this.fieldCanvas.repaint();
    }

    public static void main(String[] args) {
        SoccerGUI gui = new SoccerGUI();
        gui.setVisible(true);
    }
}