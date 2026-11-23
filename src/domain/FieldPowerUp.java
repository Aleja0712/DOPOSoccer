package domain;

import java.io.Serializable;

/**
 * Elemento coleccionable que aparece en la cancha durante el partido (extension Ciclo 3).
 */
public abstract class FieldPowerUp implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int SIZE = 36;

    protected double x;
    protected double y;
    protected double lifeTime;
    protected String name;
    protected String typeCode;

    public FieldPowerUp(double x, double y, String name, String typeCode) {
        this.x = x;
        this.y = y;
        this.lifeTime = 10.0;
        this.name = name;
        this.typeCode = typeCode;
    }

    /**
     * Aplica el beneficio del elemento al jugador que lo recoge.
     * @param player jugador que toco el elemento.
     */
    public abstract void applyTo(PlayerGame player);

    public void update(double dt) {
        this.lifeTime = Math.max(0.0, this.lifeTime - dt);
    }

    public boolean isExpired() {
        return this.lifeTime <= 0.0;
    }

    public boolean intersects(PlayerGame player) {
        return player.getX() < this.x + SIZE
                && player.getX() + player.getWidth() > this.x
                && player.getY() < this.y + SIZE
                && player.getY() + player.getHeight() > this.y;
    }

    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public int getSize() { return SIZE; }
    public String getName() { return this.name; }
    public String getTypeCode() { return this.typeCode; }
}
