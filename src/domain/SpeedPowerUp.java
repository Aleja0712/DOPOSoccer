package domain;

/**
 * Botas turbo en cancha: aumenta la velocidad del jugador en 40% durante 4 segundos.
 */
public class SpeedPowerUp extends FieldPowerUp {
    private static final long serialVersionUID = 1L;

    public SpeedPowerUp(double x, double y) {
        super(x, y, "Turbo Velocidad", "SPEED");
    }

    @Override
    public void applyTo(PlayerGame player) {
        player.applySpeedBoost(4.0);
    }
}