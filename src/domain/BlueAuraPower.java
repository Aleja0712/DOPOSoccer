package domain;

/**
 * Tiro de poder de Corea del Sur: Aura Azul.
 */
public class BlueAuraPower extends Power {
    public BlueAuraPower() {
        super("Aura Azul", 1.5, 40);
    }

    @Override
    public void launchBall(Ball ball, int direction) {
        ball.setVelocity(direction * 750.0, 0.0);
    }

    @Override
    public void applyEffectTo(PlayerGame rival) {
        rival.applyEffect("EMPUJE", this.duration);
    }
}