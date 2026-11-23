package domain;

/**
 * Tiro de poder de Nigeria: Tornado (Inconsciente por 3 s, dano base 40)
 */
public class TornadoPower extends Power {
    public TornadoPower() {
        super("Tornado", 3.0, 40);
    }

    @Override
    public void launchBall(Ball ball, int direction) {
        ball.setVelocity(direction * 550.0, 280.0);
    }

    @Override
    public void applyEffectTo(PlayerGame rival) {
        rival.applyEffect("INCONSCIENTE", this.duration);
    }
}
