package presentation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

/**
 * Pantalla para escoger la modalidad de juego: Jugador vs Jugador o Jugador vs Maquina.
 */
public class ModePanel extends BackgroundPanel {
    private JButton btnPlayerVsPlayer;
    private JButton btnPlayerVsMachine;
    private JButton btnBack;

    public ModePanel() {
        super(new GridBagLayout());
        prepareView();
    }

    private void prepareView() {
        RoundedPanel box = new RoundedPanel(new GridLayout(4, 1, 0, 18));
        box.setBorder(BorderFactory.createEmptyBorder(25, 40, 30, 40));

        this.btnPlayerVsPlayer = GameStyle.createButton("Jugador vs Jugador", GameStyle.GREEN, 380, 66);
        this.btnPlayerVsMachine = GameStyle.createButton("Jugador vs Máquina", GameStyle.BLUE, 380, 66);
        this.btnBack = GameStyle.createButton("Volver", GameStyle.RED, 380, 56);

        box.add(GameStyle.createLabel("MODO DE JUEGO", 34));
        box.add(this.btnPlayerVsPlayer);
        box.add(this.btnPlayerVsMachine);
        box.add(this.btnBack);
        add(box);
    }

    public JButton getPlayerVsPlayerButton() {
        return this.btnPlayerVsPlayer;
    }

    public JButton getPlayerVsMachineButton() {
        return this.btnPlayerVsMachine;
    }

    public JButton getBackButton() {
        return this.btnBack;
    }
}
