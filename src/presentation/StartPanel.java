package presentation;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

/**
 * Pantalla de inicio de DOPOSoccer: imagen de portada y botones Jugar y Salir.
 */
public class StartPanel extends JPanel {
    private static final int BUTTON_AREA = 110;
    private static final Color BACKGROUND_COLOR = new Color(40, 30, 80);

    private Image background;
    private Image cover;
    private JButton btnPlay;
    private JButton btnExit;

    public StartPanel() {
        this.background = ImageLoader.load("estadio.png");
        this.cover = ImageLoader.load("doposoccer.png");
        prepareView();
    }

    private void prepareView() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        this.btnPlay = GameStyle.createButton("JUGAR", GameStyle.GREEN, 220, 64);
        this.btnExit = GameStyle.createButton("SALIR", GameStyle.RED, 220, 64);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 22));
        buttons.setOpaque(false);
        buttons.setPreferredSize(new Dimension(0, BUTTON_AREA));
        buttons.add(this.btnPlay);
        buttons.add(this.btnExit);
        add(buttons, BorderLayout.SOUTH);
    }

    /**
     * Dibuja el estadio de fondo y encima la portada completa, sin deformarla,
     * centrada en el espacio que queda encima de los botones.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (this.background != null) {
            g2.drawImage(this.background, 0, 0, getWidth(), getHeight(), null);
        }
        if (this.cover != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            int areaW = getWidth();
            int areaH = getHeight() - BUTTON_AREA;
            int imgW = this.cover.getWidth(null);
            int imgH = this.cover.getHeight(null);
            double scale = Math.min((double) areaW / imgW, (double) areaH / imgH);
            int drawW = (int) (imgW * scale);
            int drawH = (int) (imgH * scale);
            g2.drawImage(this.cover, (areaW - drawW) / 2, (areaH - drawH) / 2, drawW, drawH, null);
        }
    }

    public JButton getPlayButton() {
        return this.btnPlay;
    }

    public JButton getExitButton() {
        return this.btnExit;
    }
}
