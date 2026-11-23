package presentation;

import domain.Ball;
import domain.Field;
import domain.PlayerGame;
import domain.SoccerGame;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.HashMap;
import domain.FieldPowerUp;

/**
 * Lienzo encargado de dibujar en 2D el estado de la cancha, jugadores, balon, auras y efectos.
 */
public class FieldCanvas extends JPanel {

    private SoccerGame game;
    private Image background;
    private Image ballImage;
    private Image goalImage;
    private HashMap<String, Image> heads;

    /**
     * Construye el lienzo asociado al juego actual.
     * @param game instancia de la fachada del dominio.
     */
    public FieldCanvas(SoccerGame game) {
        this.game = game;
        this.background = ImageLoader.load("estadio.png");
        this.ballImage = ImageLoader.loadTrimmed("ball.png");
        this.goalImage = ImageLoader.load("goal.png");
        this.heads = ImageLoader.loadHeads();
        setFocusable(true);
        updatePreferredSize();
    }

    /**
     * Actualiza la referencia del juego y ajusta la resolucion del lienzo.
     * @param newGame instancia actualizada del partido.
     */
    public void setGame(SoccerGame newGame) {
        this.game = newGame;
        updatePreferredSize();
    }

    /**
     * Ajusta las dimensiones del panel segun la resolucion configurada en Field.
     */
    public void updatePreferredSize() {
        Field f = this.game.getField();
        setPreferredSize(new Dimension(f.getWidth(), f.getHeight()));
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        Field field = this.game.getField();
        int w = field.getWidth();
        int h = field.getHeight();
        int groundY = (int) field.getGroundY();

        // Escala limpiamente la cancha al tamano actual del panel (cero bordes negros)
        if (w > 0 && h > 0) {
            g2.scale((double) getWidth() / w, (double) getHeight() / h);
        }

        drawBackground(g2, w, h, groundY);
        drawGoals(g2, field, w, groundY);
        drawFieldPowerUp(g2, this.game.getActivePowerUp());
        drawPlayer(g2, this.game.getPlayer1(), new Color(30, 100, 220));
        drawPlayer(g2, this.game.getPlayer2(), new Color(220, 50, 50));
        drawBallAndIllusions(g2, this.game.getBall());

        g2.dispose();
    }

    /**
     * Dibuja el fondo de la cancha (estadio y pasto en una sola imagen).
     */
    private void drawBackground(Graphics2D g2, int w, int h, int groundY) {
        if (this.background != null) {
            g2.drawImage(this.background, 0, 0, w, h, null);
        } else {
            g2.setColor(new Color(25, 35, 60));
            g2.fillRect(0, 0, w, groundY);
            g2.setColor(new Color(46, 160, 67));
            g2.fillRect(0, groundY, w, h - groundY);
        }
    }

    /**
     * Dibuja los dos arcos. La imagen mira a la derecha (arco izquierdo);
     * para el arco derecho se dibuja volteada con ancho negativo.
     */
    private void drawGoals(Graphics2D g2, Field field, int w, int groundY) {
        int gw = field.getLeftGoal().getWidth();
        int gh = field.getLeftGoal().getHeight();
        int topY = groundY - gh;

        if (this.goalImage != null) {
            g2.drawImage(this.goalImage, 0, topY, gw, gh, null);
            g2.drawImage(this.goalImage, w, topY, -gw, gh, null);
        } else {
            g2.setColor(new Color(230, 230, 230, 90));
            g2.fillRect(0, topY, gw, gh);
            g2.fillRect(w - gw, topY, gw, gh);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(5f));
            g2.drawRect(0, topY, gw, gh);
            g2.drawRect(w - gw, topY, gw, gh);
        }
    }

    /**
     * Dibuja un jugador: aura (si tiene poder), cabeza, botin y estado.
     * @param teamColor color del botin, distingue al jugador 1 del jugador 2.
     */
    private void drawPlayer(Graphics2D g2, PlayerGame p, Color teamColor) {
        int px = (int) p.getX();
        int py = (int) p.getY();
        int pw = p.getWidth();
        int ph = p.getHeight();

        if (p.hasPowerAura()) {
            g2.setColor(new Color(0, 220, 255, 130));
            g2.fillOval(px - 14, py - 14, pw + 28, ph + 28);
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(px - 14, py - 14, pw + 28, ph + 28);
        }

        drawHead(g2, p, px, py, pw);

        int bootX = (p.getSide() == 1) ? px + pw - 30 : px + 4;
        g2.setColor(teamColor);
        g2.fillRoundRect(bootX, py + ph - 18, 26, 18, 10, 10);
        g2.setColor(Color.WHITE);
        g2.fillRect(bootX, py + ph - 5, 26, 5);

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        if (p.isStunned()) {
            g2.setColor(Color.YELLOW);
            g2.drawString("¡ATURDIDO!", px - 6, py - 10);
        } else if (!"NONE".equals(p.getCurrentEffect())) {
            g2.setColor(Color.ORANGE);
            g2.drawString(p.getCurrentEffect(), px - 6, py - 10);
        }

        if (p.hasSpeedBoost()) {
            g2.setColor(new Color(255, 215, 0, 110));
            g2.fillOval(px - 8, py - 8, pw + 16, ph + 16);
        }
        if ("CONGELADO".equals(p.getCurrentEffect())) {
            g2.setColor(new Color(130, 224, 255, 140));
            g2.fillRoundRect(px - 6, py - 6, pw + 12, ph + 12, 14, 14);
        } else if ("QUEMADO".equals(p.getCurrentEffect())) {
            g2.setColor(new Color(255, 80, 0, 120));
            g2.fillOval(px - 10, py - 10, pw + 20, ph + 20);
        } else if ("ELECTROCUTADO".equals(p.getCurrentEffect())) {
            g2.setColor(new Color(255, 255, 0, 120));
            g2.drawOval(px - 10, py - 10, pw + 20, ph + 20);
        }
    }

    /**
     * Dibuja la cabeza del personaje segun su pais. La imagen mira a la derecha,
     * asi que para el jugador 2 se voltea para que mire hacia la izquierda.
     */
    private void drawHead(Graphics2D g2, PlayerGame p, int px, int py, int pw) {
        Image head = this.heads.get(p.getCharacter().getCountry());
        int size = pw + 16;
        int headX = px - 8;
        int headY = py - 4;
        if (head == null) {
            g2.setColor(new Color(245, 205, 165));
            g2.fillOval(headX, headY, size, size);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(headX, headY, size, size);
        } else if (p.getSide() == 1) {
            g2.drawImage(head, headX, headY, size, size, null);
        } else {
            g2.drawImage(head, headX + size, headY, -size, size, null);
        }
    }

    private void drawBallAndIllusions(Graphics2D g2, Ball ball) {
        int bx = (int) ball.getX();
        int by = (int) ball.getY();
        int bw = ball.getWidth();
        int bh = ball.getHeight();
        boolean powered = ball.getActivePowerShot() != null;

        // Ilusion (EE. UU.): 7 balones falsos identicos al real
        if (ball.hasIllusionActive()) {
            int[][] offsets = {
                {-70, -45}, {70, -45}, {-110, 20}, {110, 20},
                {-50, 55}, {50, 55}, {0, -80}
            };
            for (int i = 0; i < offsets.length; i++) {
                drawSingleBall(g2, bx + offsets[i][0], by + offsets[i][1], bw, bh, powered);
            }
        }
        drawSingleBall(g2, bx, by, bw, bh, powered);
    }

    private void drawSingleBall(Graphics2D g2, int x, int y, int w, int h, boolean powered) {
        if (powered) {
            g2.setColor(new Color(255, 140, 0, 160));
            g2.fillOval(x - 8, y - 8, w + 16, h + 16);
        }
        if (this.ballImage != null) {
            g2.drawImage(this.ballImage, x, y, w, h, null);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillOval(x, y, w, h);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x, y, w, h);
        }
    }

    private void drawFieldPowerUp(Graphics2D g2, FieldPowerUp item) {
        if (item != null) {
            int ix = (int) item.getX();
            int iy = (int) item.getY();
            int size = item.getSize();
            String code = item.getTypeCode();

            Color fill = new Color(241, 196, 15);
            String symbol = "S";
            if ("HEALTH".equals(code)) {
                fill = new Color(46, 204, 113);
                symbol = "+";
            } else if ("ENERGY".equals(code)) {
                fill = new Color(52, 152, 219);
                symbol = "P";
            }

            g2.setColor(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 95));
            g2.fillOval(ix - 6, iy - 6, size + 12, size + 12);
            g2.setColor(fill);
            g2.fillOval(ix, iy, size, size);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawOval(ix, iy, size, size);
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2.drawString(symbol, ix + size / 2 - 6, iy + size / 2 + 6);
        }
    }
}
