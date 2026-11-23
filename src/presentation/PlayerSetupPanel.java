package presentation;

import domain.Character;
import domain.PlayerGame;
import domain.Power;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * Tarjeta de un jugador: personaje elegido (con flechas para cambiarlo),
 * su tiro de poder, sus puntos y las barras de estadisticas con botones de mejora.
 */
public class PlayerSetupPanel extends RoundedPanel {
    public static final int STAT_COUNT = 4;
    private static final String[] STAT_NAMES = {"SPEED", "JUMP", "KICK", "SURVIVAL"};
    private static final String[] STAT_LABELS = {"Velocidad", "Salto", "Patada", "Vida"};
    private static final int HEAD_SIZE = 150;
    private static final int MAX_LEVEL = 10;

    private HashMap<String, ImageIcon> headIcons;
    private JLabel lblHead;
    private JLabel lblCountry;
    private JLabel lblPower;
    private JLabel lblPoints;
    private JButton btnPrevious;
    private JButton btnNext;
    private JProgressBar[] statBars;
    private JButton[] statButtons;
    private JLabel lblTitle;

    /**
     * @param title titulo de la tarjeta (por ejemplo "JUGADOR 1").
     * @param heads imagen de la cabeza de cada pais.
     */
    public PlayerSetupPanel(String title, HashMap<String, Image> heads) {
        super(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(15, 18, 18, 18));
        prepareHeadIcons(heads);
        prepareView(title);
    }

    /**
     * Escala una sola vez cada cabeza al tamano de la tarjeta.
     */
    private void prepareHeadIcons(HashMap<String, Image> heads) {
        this.headIcons = new HashMap<>();
        for (Map.Entry<String, Image> entry : heads.entrySet()) {
            if (entry.getValue() != null) {
                Image scaled = entry.getValue().getScaledInstance(HEAD_SIZE, HEAD_SIZE, Image.SCALE_SMOOTH);
                this.headIcons.put(entry.getKey(), new ImageIcon(scaled));
            }
        }
    }

    private void prepareView(String title) {
        this.lblTitle = GameStyle.createLabel(title, 24);
        add(this.lblTitle, BorderLayout.NORTH);

        JPanel character = new JPanel(new BorderLayout(6, 4));
        character.setOpaque(false);
        this.btnPrevious = GameStyle.createButton("<", GameStyle.BLUE, 50, 50);
        this.btnNext = GameStyle.createButton(">", GameStyle.BLUE, 50, 50);
        this.lblHead = new JLabel("", JLabel.CENTER);
        this.lblHead.setPreferredSize(new Dimension(HEAD_SIZE, HEAD_SIZE));
        character.add(wrapCentered(this.btnPrevious), BorderLayout.WEST);
        character.add(this.lblHead, BorderLayout.CENTER);
        character.add(wrapCentered(this.btnNext), BorderLayout.EAST);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        this.lblCountry = GameStyle.createLabel("", 20);
        this.lblCountry.setForeground(GameStyle.YELLOW);
        this.lblPower = GameStyle.createLabel("", 12);
        info.add(this.lblCountry);
        info.add(this.lblPower);
        character.add(info, BorderLayout.SOUTH);
        add(character, BorderLayout.CENTER);

        add(prepareStats(), BorderLayout.SOUTH);
    }

    public void setTitle(String title) {
        this.lblTitle.setText(title);
    }

    /**
     * Envuelve un componente para que conserve su tamano y quede centrado.
     */
    private JPanel wrapCentered(JButton button) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(button);
        return wrapper;
    }

    private JPanel prepareStats() {
        JPanel stats = new JPanel(new GridLayout(STAT_COUNT + 1, 1, 0, 6));
        stats.setOpaque(false);
        this.lblPoints = GameStyle.createLabel("", 12);
        stats.add(this.lblPoints);

        this.statBars = new JProgressBar[STAT_COUNT];
        this.statButtons = new JButton[STAT_COUNT];
        for (int i = 0; i < STAT_COUNT; i++) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            JLabel name = GameStyle.createLabel(STAT_LABELS[i], 12);
            name.setPreferredSize(new Dimension(85, 26));
            this.statBars[i] = new JProgressBar(0, MAX_LEVEL);
            this.statBars[i].setStringPainted(true);
            this.statBars[i].setForeground(new Color(240, 150, 30));
            this.statButtons[i] = GameStyle.createButton("+", GameStyle.GREEN, 40, 30);
            row.add(name, BorderLayout.WEST);
            row.add(this.statBars[i], BorderLayout.CENTER);
            row.add(this.statButtons[i], BorderLayout.EAST);
            stats.add(row);
        }
        return stats;
    }

    /**
     * Muestra en la tarjeta el estado actual del jugador (lo lee del dominio).
     * @param player jugador del partido.
     */
    public void update(PlayerGame player) {
        Character character = player.getCharacter();
        Power power = character.getPower();
        this.lblHead.setIcon(this.headIcons.get(character.getCountry()));
        this.lblCountry.setText(character.getCountry().toUpperCase());
        this.lblPower.setText("Poder: " + power.getName() + "  |  Daño " + power.getBaseDamage()
                + "  |  Efecto " + power.getDuration() + " s");
        this.lblPoints.setText("Puntos: " + player.getPoints()
                + "   |   Próxima mejora: " + player.getNextUpgradeCost());

        int[] levels = {player.getSpeedStat(), player.getJumpStat(), player.getKickStat(), player.getSurvivalStat()};
        for (int i = 0; i < STAT_COUNT; i++) {
            this.statBars[i].setValue(levels[i]);
            this.statBars[i].setString("Nivel " + levels[i] + " / " + MAX_LEVEL);
        }
    }

    /**
     * @param index posicion de la estadistica (0 a STAT_COUNT - 1).
     * @return el nombre de la estadistica que entiende el dominio.
     */
    public static String getStatName(int index) {
        return STAT_NAMES[index];
    }

    public JButton getStatButton(int index) {
        return this.statButtons[index];
    }

    public JButton getPreviousButton() {
        return this.btnPrevious;
    }

    public JButton getNextButton() {
        return this.btnNext;
    }
}
