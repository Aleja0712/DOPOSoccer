package presentation;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;

/**
 * Panel que dibuja la imagen del estadio de fondo, estirada a todo su tamano.
 */
public class BackgroundPanel extends JPanel {
    private Image background;

    public BackgroundPanel(LayoutManager layout) {
        super(layout);
        this.background = ImageLoader.load("estadio.png");
        setBackground(new Color(40, 30, 80));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.background != null) {
            g.drawImage(this.background, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
