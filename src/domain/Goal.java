package domain;

/**
 * Representa uno de los arcos de la cancha
 */
public class Goal {
    private int side; // 1 = Arco izquierdo (defendido por P1), 2 = Arco derecho (defendido por P2)
    private int width;
    private int height;

    /**
     * Construye un arco para el lado indicado
     * @param side lado de la cancha (1 izquierdo, 2 derecho).
     * @param width ancho del arco en pixeles.
     * @param height altura del arco en pixeles.
     */
    public Goal(int side, int width, int height) {
        this.side = side;
        this.width = width;
        this.height = height;
    }

    /**
     * Verifica si el balon entro en este arco
     * @param ball balon del partido.
     * @param fieldWidth ancho total de la cancha.
     * @param groundY coordenada Y del suelo.
     * @return true si el balon cruzo la linea de gol bajo el travesano.
     */
    public boolean checkGoal(Ball ball, int fieldWidth, double groundY) {
        double goalTopY = groundY - this.height;
        double ballCenterX = ball.getX() + ball.getWidth() / 2.0;
        double ballCenterY = ball.getY() + ball.getHeight() / 2.0;

        boolean underCrossbar = ballCenterY > goalTopY;
        boolean insideHorizontal = false;
        if (this.side == 1) {
            insideHorizontal = ballCenterX < this.width;
        } else {
            insideHorizontal = ballCenterX > (fieldWidth - this.width);
        }
        return underCrossbar && insideHorizontal;
    }

    public int getSide(){ 
        return this.side; 
    }
    
    public int getWidth(){ 
        return this.width; 
    }
    
    public int getHeight(){ 
        return this.height; 
    }
}