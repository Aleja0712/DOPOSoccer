package domain;

/**
 * Perfil Ofensiva: su prioridad es buscar el balon y anotar en el arco rival.
 */
public class OffensiveCPU extends CPUController {
    private static final long serialVersionUID = 1L;

    public OffensiveCPU() {
        super("Ofensiva");
    }

    @Override
    public void decideAction(SoccerGame game, PlayerGame cpu, PlayerGame rival, double dt) {
        if (cpu.canAct()) {
            Ball ball = game.getBall();
            Field field = game.getField();
            double cpuCenter = cpu.getX() + cpu.getWidth() / 2.0;
            double ballCenter = ball.getX() + ball.getWidth() / 2.0;
            double targetX = ballCenter + 22.0;

            if (cpuCenter > targetX + 12.0) {
                cpu.moveHorizontal(-1);
            } else if (cpuCenter < targetX - 12.0) {
                cpu.moveHorizontal(1);
            } else {
                cpu.moveHorizontal(0);
            }

            double dist = distanceToBall(cpu, ball);
            if (dist < 130.0 && ball.getY() + ball.getHeight() < cpu.getY() + 15.0) {
                cpu.jump(field.getGroundY());
            }

            tryActivatePower(cpu);

            if (dist <= 85.0) {
                game.playerKick(cpu.getSide());
            }
        }
    }
}
