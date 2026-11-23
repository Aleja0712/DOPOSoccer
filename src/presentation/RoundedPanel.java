package presentation;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/**
 * Panel con fondo oscuro semitransparente y esquinas redondeadas,
 * para que el texto se lea bien encima de la imagen del estadio.
 */
public class RoundedPanel extends JPanel {
    private static final int ARC = 30;

    public RoundedPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(GameStyle.PANEL);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
        super.paintComponent(g);
    }
}
