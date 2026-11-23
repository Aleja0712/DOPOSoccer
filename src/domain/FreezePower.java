package domain;


/**
 * Tiro de poder de Brasil: Tiro Helado (congela al rival por 2.0 s, dano base 35).
 */
public class FreezePower extends Power {
    private static final long serialVersionUID = 1L;

    public FreezePower() {
        super("Hielo", 2.0, 35);
    }

    @Override
    public void launchBall(Ball ball, int direction) {
        ball.setVelocity(direction * 620.0, -90.0);
    }

    @Override
    public void applyEffectTo(PlayerGame rival) {
        rival.applyEffect("CONGELADO", this.duration);
    }
}
