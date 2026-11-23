package domain;

/**
 * Jugador dentro del partido con sus estadisticas, vida, barra de poder, aura y efectos[cite: 2, 3, 6].
 */
public class PlayerGame extends MovableEntity {
    private int side;
    private Character character;
    private PowerBar powerBar;
    private boolean powerAura;
    private double speedBoostTimer;

    private int speed;
    private int jump;
    private int kick;
    private int survival;
    private int points;
    private int upgradesBought;

    private double health;
    private double stunnedTimer;
    private String currentEffect;
    private double effectTimer;

    public PlayerGame(int side, Character character, double startX, double startY) {
        super(startX, startY, 60, 80);
        this.side = side;
        this.character = character;
        this.powerBar = new PowerBar();
        this.powerAura = false;
        this.speed = 1;
        this.jump = 1;
        this.kick = 1;
        this.survival = 1;
        this.points = 0;
        this.upgradesBought = 0;
        this.health = getMaxHealth();
        this.stunnedTimer = 0.0;
        this.currentEffect = "NONE";
        this.effectTimer = 0.0;
    }

    /**
     * Formula Anexo 2: multiplicadorKick = 1 + 0.10 * (Kick - 1)
     */
    public double getKickMultiplier() {
        return 1.0 + 0.10 * (this.kick - 1);
    }

    /**
     * Formula Anexo 2: vidaMax = 100 * (1 + 0.10 * (Survival - 1))
     */
    public double getMaxHealth() {
        return 100.0 * (1.0 + 0.10 * (this.survival - 1));
    }

    /**
     * Dano de patada normal: 10 * multiplicadorKick
     */
    public double getNormalKickDamage() {
        return 10.0 * getKickMultiplier();
    }

    /**
     * Dano de tiro de poder: DanoTiro * multiplicadorKick
     */
    public double getPowerShotDamage() {
        return this.character.getPower().getBaseDamage() * getKickMultiplier();
    }

    /**
     * Precio de cada punto de mejora: empieza en 500 y se duplica respecto al anterior
     */
    public int getNextUpgradeCost() {
        return 500 * (int) Math.pow(2, this.upgradesBought);
    }

    /**
     * Mejora una estadistica del jugador validando puntos y nivel maximo (10)
     * @param statName nombre de la estadistica ("SPEED", "JUMP", "KICK", "SURVIVAL").
     * @throws SoccerException si no alcanzan los puntos, si ya esta en 10 o si el nombre es invalido
     */
    public void upgradeStat(String statName) throws SoccerException {
        if (statName == null) {
            throw new SoccerException(SoccerException.INVALID_CONFIG);
        }
        int cost = getNextUpgradeCost();
        if (this.points < cost) {
            throw new SoccerException(SoccerException.INSUFFICIENT_POINTS);
        }
        String key = statName.trim().toUpperCase();
        if ("SPEED".equals(key)) {
            if (this.speed >= 10) {
                throw new SoccerException(SoccerException.INVALID_STAT_LEVEL);
            }
            this.speed++;
        } else if ("JUMP".equals(key)) {
            if (this.jump >= 10) {
                throw new SoccerException(SoccerException.INVALID_STAT_LEVEL);
            }
            this.jump++;
        } else if ("KICK".equals(key)) {
            if (this.kick >= 10) {
                throw new SoccerException(SoccerException.INVALID_STAT_LEVEL);
            }
            this.kick++;
        } else if ("SURVIVAL".equals(key)) {
            if (this.survival >= 10) {
                throw new SoccerException(SoccerException.INVALID_STAT_LEVEL);
            }
            this.survival++;
            this.health = getMaxHealth();
        } else {
            throw new SoccerException(SoccerException.INVALID_CONFIG);
        }
        this.points -= cost;
        this.upgradesBought++;
    }

    /**
     * Descuenta vida al jugador; si llega a cero, queda aturdido por 3 segundos
     * @param damage puntos de dano recibidos.
     */
    public void receiveDamage(double damage) {
        if (!isStunned()) {
            this.health = Math.max(0.0, this.health - damage);
            if (this.health == 0.0) {
                this.stunnedTimer = 3.0;
                this.vx = 0.0;
            }
        }
    }

    /**
     * Aplica el efecto de un tiro de poder reemplazando cualquier efecto anterior (Regla 8)
     * @param effectName nombre del efecto.
     * @param duration duracion en segundos.
     */
    public void applyEffect(String effectName, double duration) {
        this.currentEffect = effectName;
        this.effectTimer = duration;
        if ("INCONSCIENTE".equals(effectName)) {
            this.vy = -450.0;
        }
    }

    /**
     * Activa el aura de poder cuando la barra esta llena y no tiene otro poder activo (Reglas 2, 3 y 7)
     * @throws SoccerException si la barra no esta llena o ya tiene el aura activa.
     */
    public void activatePower() throws SoccerException {
        if (this.powerAura || !this.powerBar.isFull()) {
            throw new SoccerException(SoccerException.POWER_NOT_READY);
        }
        this.powerBar.consume();
        this.powerAura = true;
    }

    public void update(double dt, Field field) {
        this.powerBar.update(dt);

        if (this.stunnedTimer > 0.0) {
            this.stunnedTimer -= dt;
            if (this.stunnedTimer <= 0.0) {
                this.stunnedTimer = 0.0;
                this.health = getMaxHealth();
            }
        }

        if (this.effectTimer > 0.0) {
            this.effectTimer -= dt;
            if ("EMPUJE".equals(this.currentEffect)) {
                this.vx = (this.side == 1) ? -350.0 : 350.0;
            }
            if (this.effectTimer <= 0.0) {
                this.effectTimer = 0.0;
                this.currentEffect = "NONE";
                this.vx = 0.0;
            }
        }

        if (this.speedBoostTimer > 0.0) {
            this.speedBoostTimer = Math.max(0.0, this.speedBoostTimer - dt);
        }
        if (this.effectTimer > 0.0) {
            this.effectTimer -= dt;
            if ("EMPUJE".equals(this.currentEffect)) {
                this.vx = (this.side == 1) ? -350.0 : 350.0;
            } else if ("QUEMADO".equals(this.currentEffect)) {
                receiveDamage(6.0 * dt);
            } else if ("CONGELADO".equals(this.currentEffect)) {
                this.vx = 0.0;
            }
            if (this.effectTimer <= 0.0) {
                this.effectTimer = 0.0;
                this.currentEffect = "NONE";
                this.vx = 0.0;
            }
        }

        applyGravity(dt, field.getGroundY());
        if (this.x < 0.0) {
            this.x = 0.0;
        }
        if (this.x + this.width > field.getWidth()) {
            this.x = field.getWidth() - this.width;
        }
    }

    public void moveHorizontal(int dir) {
        if (canAct()) {
            double baseSpeed = 260.0 * (1.0 + 0.10 * (this.speed - 1));
            if ("ELECTROCUTADO".equals(this.currentEffect)) {
                baseSpeed *= 0.5;
            }
            if (this.speedBoostTimer > 0.0) {
                baseSpeed *= 1.4;
            }
            this.vx = dir * baseSpeed;
        }
    }

    public void jump(double groundY) {
        if (canAct() && !"ELECTROCUTADO".equals(this.currentEffect)) {
            if (this.y + this.height >= groundY - 2.0) {
                this.vy = -515.0 * (1.0 + 0.10 * (this.jump - 1));
            }
        }
    }

    public boolean canAct() {
        return !isStunned()
                && !"INCONSCIENTE".equals(this.currentEffect)
                && !"EMPUJE".equals(this.currentEffect)
                && !"CONGELADO".equals(this.currentEffect);
    }

    public boolean isStunned() { 
        return this.stunnedTimer > 0.0; 
    }
    
    public boolean hasPowerAura() { 
        return this.powerAura; 
    }
    
    public void consumePowerAura() { 
        this.powerAura = false; 
    }
    
    public void addPoints(int pts) { 
        this.points += pts; 
    }
        
    public int getPoints() { 
        return this.points; 
    }
    
    public double getHealth() { 
        return this.health; 
    }

    public void resetForMatch() {
        this.health = getMaxHealth();
        this.powerBar.reset();
        this.powerAura = false;
        this.stunnedTimer = 0.0;
        this.effectTimer = 0.0;
        this.currentEffect = "NONE";
        this.speedBoostTimer = 0.0;
    }

    public void heal(double amount) {
        if (amount > 0.0 && !isStunned()) {
            this.health = Math.min(getMaxHealth(), this.health + amount);
        }
    }

    public void applySpeedBoost(double duration) {
        if (duration > 0.0) {
            this.speedBoostTimer = duration;
        }
    }

    public boolean hasSpeedBoost() {
        return this.speedBoostTimer > 0.0;
    }

    public PowerBar getPowerBar() { 
        return this.powerBar; 
    }
    
    public Character getCharacter() { 
        return this.character; 
    }
    
    public void setCharacter(Character c) { 
        this.character = c; 
    }
    
    public int getSide() { 
        return this.side; 
    }
    
    public int getSpeedStat() { 
        return this.speed; 
    }
    
    public int getJumpStat() { 
        return this.jump; 
    }
    
    public int getKickStat() { 
        return this.kick; 
    }
    
    public int getSurvivalStat() { 
        return this.survival; 
    }
    
    public String getCurrentEffect() { 
        return this.currentEffect; 
    }

    public int getUpgradesBought() {
        return this.upgradesBought;
    }

    /**
     * Restaura el progreso guardado del jugador (puntos y estadisticas).
     */
    public void restoreProgress(int points, int upgradesBought, int speed, int jump, int kick, int survival) {
        this.points = Math.max(0, points);
        this.upgradesBought = Math.max(0, upgradesBought);
        this.speed = Math.max(1, Math.min(10, speed));
        this.jump = Math.max(1, Math.min(10, jump));
        this.kick = Math.max(1, Math.min(10, kick));
        this.survival = Math.max(1, Math.min(10, survival));
        this.health = getMaxHealth();
    }
    
}