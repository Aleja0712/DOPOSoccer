package domain;

/**
 * Partido por goles: gana el primer jugador que alcance la meta de goles definida
*/
public class GoalMatch extends MatchType {
    private int targetGoals;

    /**
     * Crea un partido por goles validando que la meta sea positiva
     * @param targetGoals cantidad de goles necesarios para ganar.
     * @throws SoccerException si la meta de goles es menor o igual a cero.
     */
    public GoalMatch(int targetGoals) throws SoccerException {
        super();
        if (targetGoals <= 0) {
            throw new SoccerException(SoccerException.INVALID_CONFIG);
        }
        this.targetGoals = targetGoals;
    }

    @Override
    public void onGoalScored(int scoringSide, SoccerGame game) {
        int goalsScorer = (scoringSide == 1)
                ? game.getMarker().getGoals1()
                : game.getMarker().getGoals2();
        if (goalsScorer >= this.targetGoals) {
            this.finished = true;
        }
    }

    @Override
    public boolean isFinished(SoccerGame game) {
        boolean reachedGoal = game.getMarker().getGoals1() >= this.targetGoals
                || game.getMarker().getGoals2() >= this.targetGoals;
        if (reachedGoal) {
            this.finished = true;
        }
        return this.finished;
    }

    public int getTargetGoals() {
        return this.targetGoals;
    }
}