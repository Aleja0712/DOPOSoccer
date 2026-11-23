package presentation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Estilo visual comun (colores, fuentes y botones) de todas las pantallas de DOPOSoccer.
 */
public class GameStyle {
    public static final Color GREEN = new Color(60, 170, 60);
    public static final Color RED = new Color(200, 60, 50);
    public static final Color BLUE = new Color(40, 110, 210);
    public static final Color GRAY = new Color(110, 110, 120);
    public static final Color YELLOW = new Color(255, 205, 40);
    public static final Color PANEL = new Color(15, 15, 45, 210);
    public static final String FONT_NAME = "Arial Black";

    /**
     * Crea un boton grande con el estilo del juego.
     * @param text texto del boton.
     * @param color color de fondo del boton.
     * @param width ancho en pixeles.
     * @param height alto en pixeles.
     * @return el boton creado.
     */
    public static JButton createButton(String text, Color color, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_NAME, Font.BOLD, height / 3));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(color.darker(), 3));
        button.setPreferredSize(new Dimension(width, height));
        return button;
    }

    /**
     * Crea una etiqueta de texto blanco, centrada, con el tamano indicado.
     * @param text texto de la etiqueta.
     * @param size tamano de la fuente.
     * @return la etiqueta creada.
     */
    public static JLabel createLabel(String text, int size) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font(FONT_NAME, Font.BOLD, size));
        label.setForeground(Color.WHITE);
        return label;
    }
}
