package domain;

/**
 * Tiro de poder de Colombia: Disparo de Fuego (dano alto y efecto QUEMADO por 2.5 s).
 */
public class FireShotPower extends Power {
    private static final long serialVersionUID = 1L;

    public FireShotPower() {
        super("Fuego", 2.5, 50);
    }

    @Override
    public void launchBall(Ball ball, int direction) {
        ball.setVelocity(direction * 720.0, -140.0);
    }

    @Override
    public void applyEffectTo(PlayerGame rival) {
        rival.applyEffect("QUEMADO", this.duration);
    }
}