package domain;

/**
 * Orbe de energia en cancha: recarga 50% de la barra de poder de inmediato.
 */
public class EnergyPowerUp extends FieldPowerUp {
    private static final long serialVersionUID = 1L;

    public EnergyPowerUp(double x, double y) {
        super(x, y, "Energia +50% Poder", "ENERGY");
    }

    @Override
    public void applyTo(PlayerGame player) {
        player.getPowerBar().addCharge(50.0);
    }
}