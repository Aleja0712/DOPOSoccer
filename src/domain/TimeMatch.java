package domain;

/**
 * Partido por tiempo: dura un numero de minutos predefinido y entra en muerte subita si hay empate
 */
public class TimeMatch extends MatchType {
    private double remainingSeconds;
    private boolean goldenGoal;

    /**
     * Crea un partido por tiempo validando que los minutos sean positivos¿
     * @param minutes duracion del partido en minutos.
     * @throws SoccerException si los minutos son menores o iguales a cero.
     */
    public TimeMatch(int minutes) throws SoccerException {
        super();
        if (minutes <= 0) {
            throw new SoccerException(SoccerException.INVALID_CONFIG);
        }
        this.remainingSeconds = minutes * 60.0;
        this.goldenGoal = false;
    }

    @Override
    public void update(double dt, SoccerGame game) {
        if (!this.finished && !this.goldenGoal) {
            super.update(dt, game);
            this.remainingSeconds = Math.max(0.0, this.remainingSeconds - dt);
            if (this.remainingSeconds == 0.0) {
                if (game.getMarker().getGoals1() != game.getMarker().getGoals2()) {
                    this.finished = true;
                } else {
                    this.goldenGoal = true;
                }
            }
        } else if (!this.finished && this.goldenGoal) {
            this.elapsedSeconds += dt;
        }
    }

    @Override
    public void onGoalScored(int scoringSide, SoccerGame game) {
        if (this.goldenGoal) {
            this.finished = true;
        }
    }

    @Override
    public boolean isFinished(SoccerGame game) {
        return this.finished;
    }

    public double getRemainingSeconds() {
        return this.remainingSeconds;
    }

    public boolean isGoldenGoal() {
        return this.goldenGoal;
    }
}