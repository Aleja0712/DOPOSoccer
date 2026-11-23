package domain;

/**
 * Representa la condicion de finalizacion y victoria de un partido.
 */
public abstract class MatchType {
    protected double elapsedSeconds;
    protected boolean finished;

    /**
     * Inicializa el estado base del partido.
     */
    public MatchType() {
        this.elapsedSeconds = 0.0;
        this.finished = false;
    }

    /**
     * Actualiza el tiempo transcurrido del partido y verifica si ha finalizado.
     * @param dt delta de tiempo en segundos.
     * @param game instancia del partido actual.
     */
    public void update(double dt, SoccerGame game) {
        if (!this.finished) {
            this.elapsedSeconds += dt;
            this.finished = isFinished(game);
        }
    }

    /**
     * Notifica al tipo de partido que se ha anotado un gol.
     * @param scoringSide lado del jugador que anoto (1 o 2).
     * @param game instancia del partido actual.
     */
    public abstract void onGoalScored(int scoringSide, SoccerGame game);

    /**
     * Evalua si el partido cumplio su condicion de finalizacion.
     * @param game instancia del partido actual.
     * @return true si el partido termino, false en caso contrario.
     */
    public abstract boolean isFinished(SoccerGame game);

    /**
     * Determina el jugador ganador del partido segun el marcador actual.
     * @param game instancia del partido actual.
     * @return el jugador ganador o null si no hay ganador aun.
     */
    public PlayerGame getWinner(SoccerGame game) {
        PlayerGame winner = null;
        int g1 = game.getMarker().getGoals1();
        int g2 = game.getMarker().getGoals2();
        if (g1 > g2) {
            winner = game.getPlayer1();
        } else if (g2 > g1) {
            winner = game.getPlayer2();
        }
        return winner;
    }
        


    public double getElapsedSeconds() {
        return this.elapsedSeconds;
    }
}