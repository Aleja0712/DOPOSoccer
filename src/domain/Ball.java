package domain;

/**
 * Balon del partido afectado por gravedad, rebotes y tiros de poder.
 */
public class Ball extends MovableEntity {
    private static final long serialVersionUID = 1L;
    public static final int SIZE = 40;
    private static final double BALL_GRAVITY_FACTOR = 0.82;
    private static final double MAX_SPEED = 920.0;

    private Power activePowerShot;
    private PlayerGame powerShooter;
    private double illusionTimer;

    public Ball(double x, double y) {
        super(x, y, SIZE, SIZE);
        this.activePowerShot = null;
        this.powerShooter = null;
        this.illusionTimer = 0.0;
    }

    public void update(double dt, Field field) {
        if (this.illusionTimer > 0.0) {
            this.illusionTimer = Math.max(0.0, this.illusionTimer - dt);
        }

        double prevY = this.y;
        this.vy += (GRAVITY * BALL_GRAVITY_FACTOR) * dt;
        this.vx *= 0.996; // Resistencia suave del aire
        clampSpeed();

        this.x += this.vx * dt;
        this.y += this.vy * dt;

        handleGroundAndCeiling(field);
        handleGoalRoofsAndWalls(field, prevY);
    }

    private void clampSpeed() {
        double speed = Math.hypot(this.vx, this.vy);
        if (speed > MAX_SPEED) {
            double factor = MAX_SPEED / speed;
            this.vx *= factor;
            this.vy *= factor;
        }
    }

    private void handleGroundAndCeiling(Field field) {
        double groundY = field.getGroundY();
        if (this.y + this.height >= groundY) {
            this.y = groundY - this.height;
            this.vy = -this.vy * 0.78;
            this.vx *= 0.96;
            if (Math.abs(this.vy) < 45.0) {
                this.vy = 0.0;
            }
            if (Math.abs(this.vx) < 8.0) {
                this.vx = 0.0;
            }
        }
        if (this.y <= 0.0) {
            this.y = 0.0;
            this.vy = Math.abs(this.vy) * 0.75;
        }
    }

    private void handleGoalRoofsAndWalls(Field field, double prevY) {
        double goalTopY = field.getGoalTopY();
        int leftGoalW = field.getLeftGoal().getWidth();
        int rightGoalX = field.getWidth() - field.getRightGoal().getWidth();

        // Rebote sobre el techo/travesano del arco izquierdo (evita que quede atascado o cuente gol falso)
        boolean overLeftGoal = this.x < leftGoalW;
        if (overLeftGoal && (prevY + this.height) <= goalTopY + 6.0 && (this.y + this.height) >= goalTopY) {
            this.y = goalTopY - this.height;
            this.vy = -Math.max(180.0, Math.abs(this.vy) * 0.75);
            this.vx = Math.max(this.vx, 140.0);
        }

        // Rebote sobre el techo/travesano del arco derecho
        boolean overRightGoal = (this.x + this.width) > rightGoalX;
        if (overRightGoal && (prevY + this.height) <= goalTopY + 6.0 && (this.y + this.height) >= goalTopY) {
            this.y = goalTopY - this.height;
            this.vy = -Math.max(180.0, Math.abs(this.vy) * 0.75);
            this.vx = Math.min(this.vx, -140.0);
        }

        // Rebote en bordes laterales por encima del travesano
        if ((this.y + this.height / 2.0) <= goalTopY) {
            if (this.x <= 0.0) {
                this.x = 0.0;
                this.vx = Math.abs(this.vx) * 0.80;
            } else if (this.x + this.width >= field.getWidth()) {
                this.x = field.getWidth() - this.width;
                this.vx = -Math.abs(this.vx) * 0.80;
            }
        }
    }

    public void activatePowerShot(Power power, PlayerGame shooter) {
        this.activePowerShot = power;
        this.powerShooter = shooter;
    }

    public void clearPowerShot() {
        this.activePowerShot = null;
        this.powerShooter = null;
    }

    public void activateIllusion(double duration) {
        this.illusionTimer = duration;
    }

    public Power getActivePowerShot() { 
        return this.activePowerShot; 
    }

    public PlayerGame getPowerShooter() { 
        return this.powerShooter; 
    }

    public boolean hasIllusionActive() { 
        return this.illusionTimer > 0.0; 
    }

}