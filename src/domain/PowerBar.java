package domain;

/**
 * Barra de poder que se llena en 10 segundos y sube 1/4 al recibir un gol.
 */
public class PowerBar {
    private double charge; // 0.0 a 100.0
    private static final double FILL_RATE_PER_SECOND = 10.0; // 100% en 10 s

    public PowerBar() {
        this.charge = 0.0;
    }

    public void update(double dt) {
        charge = Math.min(100.0, charge + FILL_RATE_PER_SECOND * dt);
    }

    public void onGoalConceded() {
        charge = Math.min(100.0, charge + 25.0); // Aumenta 1/4 de inmediato
    }

    public boolean isFull() {
        return charge >= 100.0;
    }

    public void consume() {
        this.charge = 0.0;
    }

    public void reset() {
        this.charge = 0.0;
    }

    public double getCharge() {
        return charge;
    }

    public void addCharge(double amount) {
        if (amount > 0.0) {
            this.charge = Math.min(100.0, this.charge + amount);
        }
    }
}