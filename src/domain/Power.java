package domain;

import java.io.Serializable;

/**
 * Clase abstracta que modela un tiro de poder unico de cada pais.
 */
public abstract class Power implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String name;
    protected double duration;
    protected int baseDamage;

    public Power(String name, double duration, int baseDamage) {
        this.name = name;
        this.duration = duration;
        this.baseDamage = baseDamage;
    }

    public abstract void launchBall(Ball ball, int direction);

    public abstract void applyEffectTo(PlayerGame rival);

    public String getName() {
        return this.name;
    }

    public double getDuration() {
        return this.duration;
    }

    public int getBaseDamage() {
        return this.baseDamage;
    }
}