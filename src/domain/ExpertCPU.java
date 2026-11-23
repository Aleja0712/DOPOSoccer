package domain;

/**
 * Perfil Experta: decide su estrategia dinamicamente segun el marcador, el tiempo,
 * la vida actual y la posicion proyectada del balon.
 */
public class ExpertCPU extends CPUController {
    private static final long serialVersionUID = 1L;
    private OffensiveCPU offensiveMode;
    private DefensiveCPU defensiveMode;

    public ExpertCPU() {
        super("Experta");
        this.offensiveMode = new OffensiveCPU();
        this.defensiveMode = new DefensiveCPU();
    }

    @Override
    public void decideAction(SoccerGame game, PlayerGame cpu, PlayerGame rival, double dt) {
        if (cpu.canAct()) {
            Ball ball = game.getBall();
            Field field = game.getField();
            int cpuGoals = game.getMarker().getGoals2();
            int rivalGoals = game.getMarker().getGoals1();

            boolean underTimePressure = isUnderTimePressure(game);
            boolean winning = cpuGoals > rivalGoals;
            boolean lowHealth = cpu.getHealth() <= cpu.getMaxHealth() * 0.35;
            boolean ballInDangerZone = ball.getX() > field.getWidth() * 0.65;

            if (ballInDangerZone || (winning && (underTimePressure || lowHealth))) {
                this.defensiveMode.decideAction(game, cpu, rival, dt);
            } else if (!winning || underTimePressure) {
                executePrecisionAttack(game, cpu, ball, field);
            } else {
                this.offensiveMode.decideAction(game, cpu, rival, dt);
            }

            double dist = distanceToBall(cpu, ball);
            if (dist < 170.0 || !winning) {
                tryActivatePower(cpu);
            }
        }
    }

    private boolean isUnderTimePressure(SoccerGame game) {
        boolean pressure = false;
        MatchType mt = game.getMatchType();
        if (mt instanceof TimeMatch) {
            TimeMatch tm = (TimeMatch) mt;
            pressure = tm.isGoldenGoal() || tm.getRemainingSeconds() <= 25.0;
        }
        return pressure;
    }

    private void executePrecisionAttack(SoccerGame game, PlayerGame cpu, Ball ball, Field field) {
        double predictedBallX = ball.getX() + ball.getWidth() / 2.0 + ball.getVx() * 0.12;
        double targetX = predictedBallX + 20.0;
        double cpuCenter = cpu.getX() + cpu.getWidth() / 2.0;

        if (cpuCenter > targetX + 10.0) {
            cpu.moveHorizontal(-1);
        } else if (cpuCenter < targetX - 10.0) {
            cpu.moveHorizontal(1);
        } else {
            cpu.moveHorizontal(0);
        }

        double dist = distanceToBall(cpu, ball);
        if (dist < 135.0 && ball.getY() + ball.getHeight() < cpu.getY() + 18.0) {
            cpu.jump(field.getGroundY());
        }
        if (dist <= 85.0) {
            game.playerKick(cpu.getSide());
        }
    }
}