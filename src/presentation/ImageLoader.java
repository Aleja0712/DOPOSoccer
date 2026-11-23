package presentation;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Carga las imagenes de la carpeta de recursos para toda la capa de presentacion.
 */
public class ImageLoader {
    private static final String RESOURCES = "recursos/";

    public static Image load(String fileName) {
        ImageIcon icon = new ImageIcon(RESOURCES + fileName);
        Image image = null;
        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            image = icon.getImage();
        }
        return image;
    }

    /**
     * @param fileName nombre del archivo dentro de recursos/.
     * @return imagen recortada, o null si no se pudo cargar.
     */
    public static Image loadTrimmed(String fileName) {
        Image raw = load(fileName);
        Image result = raw;
        if (raw != null) {
            int w = raw.getWidth(null);
            int h = raw.getHeight(null);
            if (w > 0 && h > 0) {
                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = bi.createGraphics();
                g2.drawImage(raw, 0, 0, null);
                g2.dispose();
                result = cropTransparentMargins(bi);
            }
        }
        return result;
    }

    private static BufferedImage cropTransparentMargins(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int minX = w;
        int minY = h;
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int alpha = (img.getRGB(x, y) >> 24) & 0xff;
                if (alpha > 15) {
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }
        BufferedImage cropped = img;
        if (maxX >= minX && maxY >= minY) {
            cropped = img.getSubimage(minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
        }
        return cropped;
    }

    public static HashMap<String, Image> loadHeads() {
        HashMap<String, Image> heads = new HashMap<>();
        heads.put("Corea del Sur", load("korea.png"));
        heads.put("Camerun", load("cameroon.png"));
        heads.put("Nigeria", load("nigeria.png"));
        heads.put("Estados Unidos", load("usa.png"));
        heads.put("Colombia", load("colombia.png"));
        heads.put("Brasil", load("brazil.png"));
        return heads;
    }
}
