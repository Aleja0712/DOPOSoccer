package domain;

/**
 * Tiro de poder de Camerun: Rayo (Electrocutado por 3 s, dano base 60)
 */
public class LightningPower extends Power {
    public LightningPower() {
        super("Rayo", 3.0, 60);
    }

    @Override
    public void launchBall(Ball ball, int direction) {
        ball.setVelocity(direction * 650.0, -50.0);
    }

    @Override
    public void applyEffectTo(PlayerGame rival) {
        rival.applyEffect("ELECTROCUTADO", this.duration);
    }
}