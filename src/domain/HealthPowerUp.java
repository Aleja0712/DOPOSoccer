package domain;

/**
 * Botiquin en cancha: recupera 30 puntos de vida sin exceder la vida maxima.
 */
public class HealthPowerUp extends FieldPowerUp {
    private static final long serialVersionUID = 1L;

    public HealthPowerUp(double x, double y) {
        super(x, y, "Botiquin +30 Vida", "HEALTH");
    }

    @Override
    public void applyTo(PlayerGame player) {
        player.heal(30.0);
    }
}