package domain;

import java.io.Serializable;

/**
 * Entidad fisica que se desplaza y sufre gravedad dentro de la cancha.
 */
public abstract class MovableEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    protected static final double GRAVITY = 1150.0;

    protected double x;
    protected double y;
    protected double vx;
    protected double vy;
    protected int width;
    protected int height;

    public MovableEntity(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void applyGravity(double dt, double groundY) {
        this.vy += GRAVITY * dt;
        this.x += this.vx * dt;
        this.y += this.vy * dt;
        if (this.y < 0.0) {
            this.y = 0.0;
            if (this.vy < 0.0) {
                this.vy = 0.0;
            }
        }
        if (this.y + this.height >= groundY) {
            this.y = groundY - this.height;
            this.vy = 0.0;
        }
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        this.vx = 0.0;
        this.vy = 0.0;
    }

    public void setVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public double getX() { 
        return x; 
    }
    
    public double getY() { 
        return y; 
    }
    
    public double getVx() { 
        return vx; 
    }
    
    public double getVy() { 
        return vy; 
    }
    
    public int getWidth() { 
        return width;
    }
    
    public int getHeight() { 
        return height; 
    }
}