package domain;

import java.io.Serializable;

/**
 * Clase abstracta extensible que define el comportamiento de un jugador maquina.
 */
public abstract class CPUController implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String profileName;

    public CPUController(String profileName) {
        this.profileName = profileName;
    }

    /**
     * Decide y ejecuta las acciones de la maquina en el paso actual del juego.
     * @param game partida actual.
     * @param cpu jugador controlado por la maquina.
     * @param rival oponente humano.
     * @param dt delta de tiempo en segundos.
     */
    public abstract void decideAction(SoccerGame game, PlayerGame cpu, PlayerGame rival, double dt);

    public String getProfileName() {
        return this.profileName;
    }

    protected double distanceToBall(PlayerGame cpu, Ball ball) {
        double cx = cpu.getX() + cpu.getWidth() / 2.0;
        double cy = cpu.getY() + cpu.getHeight() / 2.0;
        double bx = ball.getX() + ball.getWidth() / 2.0;
        double by = ball.getY() + ball.getHeight() / 2.0;
        return Math.hypot(cx - bx, cy - by);
    }

    protected void tryActivatePower(PlayerGame cpu) {
        if (cpu.getPowerBar().isFull() && !cpu.hasPowerAura()) {
            try {
                cpu.activatePower();
            } catch (SoccerException e) {
                Log.record(e);
            }
        }
    }
}