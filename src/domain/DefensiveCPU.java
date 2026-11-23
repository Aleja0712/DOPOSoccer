package domain;

/**
 * Perfil Defensiva: su prioridad es proteger su arco y despejar balones peligrosos.
 */
public class DefensiveCPU extends CPUController {
    private static final long serialVersionUID = 1L;

    public DefensiveCPU() {
        super("Defensiva");
    }

    @Override
    public void decideAction(SoccerGame game, PlayerGame cpu, PlayerGame rival, double dt) {
        if (cpu.canAct()) {
            Ball ball = game.getBall();
            Field field = game.getField();
            double cpuCenter = cpu.getX() + cpu.getWidth() / 2.0;
            double ballCenter = ball.getX() + ball.getWidth() / 2.0;
            double homeX = field.getWidth() * 0.80;
            double targetX = homeX;

            if (ballCenter >= field.getWidth() * 0.48) {
                targetX = Math.min(field.getWidth() - cpu.getWidth(), ballCenter + 18.0);
            }

            if (cpuCenter > targetX + 14.0) {
                cpu.moveHorizontal(-1);
            } else if (cpuCenter < targetX - 14.0) {
                cpu.moveHorizontal(1);
            } else {
                cpu.moveHorizontal(0);
            }

            double dist = distanceToBall(cpu, ball);
            if (ballCenter > field.getWidth() * 0.55 && dist < 140.0
                    && ball.getY() + ball.getHeight() < cpu.getY() + 20.0) {
                cpu.jump(field.getGroundY());
            }

            if (ballCenter > field.getWidth() * 0.55) {
                tryActivatePower(cpu);
            }

            if (dist <= 85.0) {
                game.playerKick(cpu.getSide());
            }
        }
    }
}