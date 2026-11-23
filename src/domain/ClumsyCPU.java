package domain;

import java.util.Random;

/**
 * Perfil Torpe: escoge al azar sus movimientos, saltos, patadas y activacion de poder.
 */
public class ClumsyCPU extends CPUController {
    private static final long serialVersionUID = 1L;
    private Random random;
    private double decisionTimer;
    private int currentDirection;

    public ClumsyCPU() {
        super("Torpe");
        this.random = new Random();
        this.decisionTimer = 0.0;
        this.currentDirection = 0;
    }

    @Override
    public void decideAction(SoccerGame game, PlayerGame cpu, PlayerGame rival, double dt) {
        if (cpu.canAct()) {
            this.decisionTimer -= dt;
            if (this.decisionTimer <= 0.0) {
                this.decisionTimer = 0.35;
                this.currentDirection = this.random.nextInt(3) - 1; 

                if (this.random.nextDouble() < 0.25) {
                    cpu.jump(game.getField().getGroundY());
                }
                if (this.random.nextDouble() < 0.30) {
                    game.playerKick(cpu.getSide());
                }
                if (this.random.nextDouble() < 0.20) {
                    tryActivatePower(cpu);
                }
            }
            cpu.moveHorizontal(this.currentDirection);
        }
    }
}
