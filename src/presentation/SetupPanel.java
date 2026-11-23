package presentation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.HashMap;

/**
 * Pantalla de preparacion del partido: cada jugador elige personaje y mejora
 * sus estadisticas; en el centro se escoge el tipo de partido y la resolucion.
 */
public class SetupPanel extends BackgroundPanel {
    private PlayerSetupPanel player1Panel;
    private PlayerSetupPanel player2Panel;
    private JComboBox<String> comboMatchType;
    private JTextField txtMatchValue;
    private JComboBox<String> comboResolution;
    private JButton btnBack;
    private JButton btnPlay;
    private JComboBox<String> comboCpuProfile;
    private JLabel lblControlsP2;

    public SetupPanel(HashMap<String, Image> heads) {
        super(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        prepareView(heads);
    }

    private void prepareView(HashMap<String, Image> heads) {
        add(GameStyle.createLabel("ELIGE TU PERSONAJE", 34), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3, 20, 0));
        center.setOpaque(false);
        this.player1Panel = new PlayerSetupPanel("JUGADOR 1", heads);
        this.player2Panel = new PlayerSetupPanel("JUGADOR 2", heads);
        center.add(this.player1Panel);
        center.add(prepareMatchOptions());
        center.add(this.player2Panel);
        add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        south.setOpaque(false);
        this.btnBack = GameStyle.createButton("VOLVER", GameStyle.RED, 200, 58);
        this.btnPlay = GameStyle.createButton("¡JUGAR!", GameStyle.GREEN, 200, 58);
        south.add(this.btnBack);
        south.add(this.btnPlay);
        add(south, BorderLayout.SOUTH);
    }

    /**
     * Panel central: tipo de partido, minutos o goles, resolucion y controles.
     */
    private JPanel prepareMatchOptions() {
        RoundedPanel options = new RoundedPanel(new GridLayout(11, 1, 0, 4));
        options.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        this.comboMatchType = new JComboBox<>(new String[]{"Por tiempo (minutos)", "Por goles (meta)"});
        this.txtMatchValue = new JTextField("2");
        this.txtMatchValue.setHorizontalAlignment(JTextField.CENTER);
        this.txtMatchValue.setFont(new Font(GameStyle.FONT_NAME, Font.BOLD, 16));
        this.comboResolution = new JComboBox<>(new String[]{
            "Baja (800 x 500)", "Media (1024 x 640)", "Alta (1280 x 720)"
        });
        this.comboResolution.setSelectedIndex(1);

        this.comboCpuProfile = new JComboBox<>(new String[]{
            "Torpe", "Ofensiva", "Defensiva", "Experta"
        });
        this.comboCpuProfile.setEnabled(false);

        this.lblControlsP2 = createControlsLabel("J2: ← / → mover  ·  ↑ saltar  ·  ↓ patear  ·  ENTER poder");

        options.add(GameStyle.createLabel("PARTIDO", 22));
        options.add(GameStyle.createLabel("Tipo de partido", 13));
        options.add(this.comboMatchType);
        options.add(GameStyle.createLabel("Minutos o goles para ganar", 13));
        options.add(this.txtMatchValue);
        options.add(GameStyle.createLabel("Resolución de la cancha", 13));
        options.add(this.comboResolution);
        options.add(GameStyle.createLabel("Perfil de la máquina", 13));
        options.add(this.comboCpuProfile);
        options.add(createControlsLabel("J1: A / D mover  ·  W saltar  ·  S patear  ·  E poder"));
        options.add(this.lblControlsP2);
        return options;
    }

    public void setMachineMode(boolean vsMachine) {
        this.comboCpuProfile.setEnabled(vsMachine);
        if (vsMachine) {
            this.player2Panel.setTitle("MÁQUINA");
            this.lblControlsP2.setText("MÁQUINA: controlada automáticamente");
        } else {
            this.player2Panel.setTitle("JUGADOR 2");
            this.lblControlsP2.setText("J2: ← / → mover  ·  ↑ saltar  ·  ↓ patear  ·  ENTER poder");
        }
    }

    public JComboBox<String> getCpuProfileCombo() {
        return this.comboCpuProfile;
    }

    private JLabel createControlsLabel(String text) {
        JLabel label = GameStyle.createLabel(text, 10);
        label.setForeground(GameStyle.YELLOW);
        return label;
    }

    /**
     * @param side 1 o 2.
     * @return la tarjeta del jugador indicado.
     */
    public PlayerSetupPanel getPlayerPanel(int side) {
        PlayerSetupPanel panel = this.player2Panel;
        if (side == 1) {
            panel = this.player1Panel;
        }
        return panel;
    }

    public JComboBox<String> getMatchTypeCombo() {
        return this.comboMatchType;
    }

    public JTextField getMatchValueField() {
        return this.txtMatchValue;
    }

    public JComboBox<String> getResolutionCombo() {
        return this.comboResolution;
    }

    public JButton getBackButton() {
        return this.btnBack;
    }

    public JButton getPlayButton() {
        return this.btnPlay;
    }
}
