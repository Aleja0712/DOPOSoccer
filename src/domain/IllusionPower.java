package domain;

/**
 * Tiro de poder de Estados Unidos: Ilusion (Confusion con 8 balones por 2.5 s, dano base 30)
 */
public class IllusionPower extends Power {
    public IllusionPower() {
        super("Ilusion", 2.5, 30);
    }

    @Override
    public void launchBall(Ball ball, int direction) {
        ball.setVelocity(direction * 500.0, -200.0);
        ball.activateIllusion(this.duration);
    }

    @Override
    public void applyEffectTo(PlayerGame rival) {
        rival.applyEffect("CONFUSION", this.duration);
    }
}