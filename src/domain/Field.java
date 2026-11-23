package domain;

/**
 * Representa la cancha de juego en una de sus tres resoluciones y contiene los dos arcos
 */
public class Field {
    private int width;
    private int height;
    private Goal leftGoal;
    private Goal rightGoal;

    /**
     * Construye la cancha con la resolucion seleccionada
     * @param resolutionOption 1 = Baja (800x500), 2 = Media (1024x640), 3 = Alta (1280x720).
     */
    public Field(int resolutionOption) {
        setResolution(resolutionOption);
    }

    /**
     * Configura las dimensiones de la cancha y sus arcos sin usar switch-break
     * @param option opcion de resolucion (1, 2 o 3).
     */
    public void setResolution(int option) {
        if (option == 1) {
            this.width = 800;
            this.height = 500;
        } else if (option == 3) {
            this.width = 1280;
            this.height = 720;
        } else {
            this.width = 1024;
            this.height = 640;
        }
        int goalW = (int) (this.width * 0.09);
        int goalH = (int) (this.height * 0.30);
        this.leftGoal = new Goal(1, goalW, goalH);
        this.rightGoal = new Goal(2, goalW, goalH);
    }

    public double getGroundY() {
        return this.height * 0.85;
    }

    public double getGoalTopY() {
        return getGroundY() - this.leftGoal.getHeight();
    }

    /**
     * Determina que jugador anoto gol apoyandose en los objetos Goal
     * @return 1 si anoto el Jugador 1 (en el arco derecho), 2 si anoto el Jugador 2 (en el arco izquierdo), 0 si no hay gol.
     */
    public int getGoalScored(Ball ball) {
        int scorer = 0;
        if (this.leftGoal.checkGoal(ball, this.width, getGroundY())) {
            scorer = 2;
        } else if (this.rightGoal.checkGoal(ball, this.width, getGroundY())) {
            scorer = 1;
        }
        return scorer;
    }

    public int getWidth(){ 
        return this.width; 
    }
    
    public int getHeight(){ 
        return this.height; 
    }
    
    public Goal getLeftGoal(){ 
        return this.leftGoal; 
    }
    
    public Goal getRightGoal(){ 
        return this.rightGoal; 
    }
}