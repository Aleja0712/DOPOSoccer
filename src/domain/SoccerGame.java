package domain;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Random;

/**
 * Clase principal del dominio DOPOSoccer.
 */
public class SoccerGame implements Serializable {
    private static final long serialVersionUID = 1L;

    private Field field;
    private Marker marker;
    private MatchType matchType;
    private PlayerGame player1;
    private PlayerGame player2;
    private Ball ball;
    private CPUController cpuController;
    private boolean paused;
    private boolean finished;
    private FieldPowerUp activePowerUp;
    private double powerUpSpawnTimer;
    private Random random;

    public SoccerGame(int resolutionOption, MatchType matchType, Character c1, Character c2) {
        this.field = new Field(resolutionOption);
        this.marker = new Marker();
        this.matchType = matchType;
        this.player1 = new PlayerGame(1, c1, field.getWidth() * 0.20, field.getGroundY() - 80);
        this.player2 = new PlayerGame(2, c2, field.getWidth() * 0.75, field.getGroundY() - 80);
        this.ball = new Ball(field.getWidth() / 2.0 - Ball.SIZE / 2.0, field.getHeight() * 0.25);
        this.cpuController = null;
        this.paused = false;
        this.finished = false;
        this.activePowerUp = null;
        this.powerUpSpawnTimer = 0.0;
        this.random = new Random();
    }

    public void startNewMatch(MatchType newMatchType) {
        this.matchType = newMatchType;
        this.marker.reset();
        this.player1.resetForMatch();
        this.player2.resetForMatch();
        resetPositionsAfterGoal();
        this.paused = false;
        this.finished = false;
    }

    public void resetPositionsAfterGoal() {
        this.player1.setPosition(this.field.getWidth() * 0.20, this.field.getHeight() * 0.30);
        this.player2.setPosition(this.field.getWidth() * 0.75, this.field.getHeight() * 0.30);
        this.ball.setPosition(this.field.getWidth() / 2.0 - Ball.SIZE / 2.0, this.field.getHeight() * 0.20);
        this.ball.clearPowerShot();
    }

    public void update(double dt) {
        if (!this.paused && !this.finished) {
            if (this.cpuController != null) {
                this.cpuController.decideAction(this, this.player2, this.player1, dt);
            }
            this.player1.update(dt, this.field);
            this.player2.update(dt, this.field);
            resolvePlayerCollision();
            this.ball.update(dt, this.field);
            checkBallCollisions();
            updateFieldPowerUp(dt);

            int scorer = this.field.getGoalScored(this.ball);
            if (scorer != 0) {
                this.marker.addGoal(scorer);
                if (scorer == 1) {
                    this.player2.getPowerBar().onGoalConceded();
                } else {
                    this.player1.getPowerBar().onGoalConceded();
                }
                this.matchType.onGoalScored(scorer, this);
                resetPositionsAfterGoal();
            }

            this.matchType.update(dt, this);
            if (this.matchType.isFinished(this)) {
                finishMatch();
            }
        }
    }

    /**
     * Evita que los dos jugadores se atraviesen horizontalmente en el suelo,
     * permitiendo que puedan saltar por encima del rival.
     */
    private void resolvePlayerCollision() {
        boolean overlapX = this.player1.getX() < this.player2.getX() + this.player2.getWidth()
                && this.player1.getX() + this.player1.getWidth() > this.player2.getX();
        boolean overlapY = this.player1.getY() < this.player2.getY() + this.player2.getHeight() - 10.0
                && this.player1.getY() + this.player1.getHeight() - 10.0 > this.player2.getY();

        if (overlapX && overlapY) {
            double c1 = this.player1.getX() + this.player1.getWidth() / 2.0;
            double c2 = this.player2.getX() + this.player2.getWidth() / 2.0;
            double minDist = (this.player1.getWidth() + this.player2.getWidth()) / 2.0;
            double push = (minDist - Math.abs(c2 - c1)) / 2.0;

            if (c1 <= c2) {
                double newX1 = Math.max(0.0, this.player1.getX() - push);
                double newX2 = Math.min(this.field.getWidth() - this.player2.getWidth(), this.player2.getX() + push);
                this.player1.x = newX1;
                this.player2.x = newX2;
            } else {
                double newX1 = Math.min(this.field.getWidth() - this.player1.getWidth(), this.player1.getX() + push);
                double newX2 = Math.max(0.0, this.player2.getX() - push);
                this.player1.x = newX1;
                this.player2.x = newX2;
            }
        }
    }

    private void checkBallCollisions() {
        checkPlayerBallInteraction(this.player1, this.player2, 1);
        checkPlayerBallInteraction(this.player2, this.player1, -1);
    }

    /**
     * Colision direccional con separacion real entre jugador y balon
     * (permite cabecear, controlar y rebotar segun el angulo de impacto).
     */
    private void checkPlayerBallInteraction(PlayerGame p, PlayerGame rival, int dir) {
        double pCenterX = p.getX() + p.getWidth() / 2.0;
        double pCenterY = p.getY() + p.getHeight() / 2.0;
        double bCenterX = this.ball.getX() + this.ball.getWidth() / 2.0;
        double bCenterY = this.ball.getY() + this.ball.getHeight() / 2.0;

        // Punto mas cercano de la caja del jugador al centro del balon
        double closestX = Math.max(p.getX(), Math.min(bCenterX, p.getX() + p.getWidth()));
        double closestY = Math.max(p.getY(), Math.min(bCenterY, p.getY() + p.getHeight()));

        double dx = bCenterX - closestX;
        double dy = bCenterY - closestY;
        double dist = Math.hypot(dx, dy);
        double ballRadius = this.ball.getWidth() / 2.0;

        if (dist < ballRadius) {
            if (this.ball.getActivePowerShot() != null && this.ball.getPowerShooter() == rival) {
                Power power = this.ball.getActivePowerShot();
                p.receiveDamage(rival.getPowerShotDamage());
                power.applyEffectTo(p);
                this.ball.clearPowerShot();
            } else if (p.hasPowerAura()) {
                Power power = p.getCharacter().getPower();
                p.consumePowerAura();
                this.ball.activatePowerShot(power, p);
                power.launchBall(this.ball, dir);
            } else {
                double nx;
                double ny;
                if (dist > 0.001) {
                    nx = dx / dist;
                    ny = dy / dist;
                } else {
                    double altDx = bCenterX - pCenterX;
                    double altDy = bCenterY - pCenterY;
                    double altDist = Math.max(1.0, Math.hypot(altDx, altDy));
                    nx = altDx / altDist;
                    ny = altDy / altDist;
                }

                double overlap = (ballRadius - dist) + 1.5;
                this.ball.x += nx * overlap;
                this.ball.y += ny * overlap;

                double impactSpeed = Math.max(300.0, Math.hypot(this.ball.getVx(), this.ball.getVy()) * 0.72);
                double newVx = nx * impactSpeed + p.getVx() * 0.65;
                double newVy = ny * impactSpeed + p.getVy() * 0.45;

                if (ny < -0.4) {
                    newVy = Math.min(newVy, -340.0);
                }
                this.ball.setVelocity(newVx, newVy);
            }
        }
    }

    public void playerKick(int side) {
        if (!this.paused && !this.finished) {
            PlayerGame attacker = (side == 1) ? this.player1 : this.player2;
            PlayerGame defender = (side == 1) ? this.player2 : this.player1;
            int dir = (side == 1) ? 1 : -1;

            if (attacker.canAct()) {
                attacker.startKick();
                double aCenterX = attacker.getX() + attacker.getWidth() / 2.0;
                double aCenterY = attacker.getY() + attacker.getHeight() / 2.0;
                double bCenterX = this.ball.getX() + this.ball.getWidth() / 2.0;
                double bCenterY = this.ball.getY() + this.ball.getHeight() / 2.0;
                double dCenterX = defender.getX() + defender.getWidth() / 2.0;
                double dCenterY = defender.getY() + defender.getHeight() / 2.0;

                double distBall = Math.hypot(aCenterX - bCenterX, aCenterY - bCenterY);
                double distRival = Math.hypot(aCenterX - dCenterX, aCenterY - dCenterY);

                // Solo patea el balon si esta al frente (o casi centrado) y al alcance del pie
                boolean ballInFront = (bCenterX - aCenterX) * dir >= -15.0;
                boolean ballAtKickHeight = bCenterY >= attacker.getY() - 10.0;
                boolean rivalInFront = (dCenterX - aCenterX) * dir >= -10.0;

                if (distBall <= 86.0 && ballInFront && ballAtKickHeight) {
                    if (attacker.hasPowerAura()) {
                        Power power = attacker.getCharacter().getPower();
                        attacker.consumePowerAura();
                        this.ball.activatePowerShot(power, attacker);
                        power.launchBall(this.ball, dir);
                    } else {
                        // Tiro raso/elevado segun altura del balon (independiente del stat Kick)
                        double kickVy = (bCenterY > aCenterY) ? -380.0 : -260.0;
                        this.ball.setVelocity(dir * 570.0, kickVy);
                    }
                } else if (distRival <= 82.0 && rivalInFront) {
                    defender.receiveDamage(attacker.getNormalKickDamage());
                }
            }
        }
    }

    public void finishMatch() {
        if (!this.finished) {
            this.finished = true;
            PlayerGame winner = this.matchType.getWinner(this);
            if (winner == this.player1) {
                this.player1.addPoints(100);
                this.player2.addPoints(25);
            } else if (winner == this.player2) {
                this.player2.addPoints(100);
                this.player1.addPoints(25);
            }
        }
    }

    /**
     * Guarda el estado completo del partido actual en un archivo binario (.dat).
     * @param file archivo destino con extension .dat.
     * @throws SoccerException si el archivo es invalido o falla la escritura.
     */
    public void saveAs(File file) throws SoccerException {
        if (file == null) {
            throw new SoccerException(SoccerException.NULL_FILE);
        }
        if (!file.getName().endsWith(".dat")) {
            throw new SoccerException(SoccerException.INVALID_EXTENSION_DAT + file.getName());
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(this);
            out.close();
        } catch (FileNotFoundException e) {
            Log.record(e);
            throw new SoccerException(SoccerException.FILE_NOT_FOUND + file.getName());
        } catch (IOException e) {
            Log.record(e);
            throw new SoccerException(SoccerException.IO_ERROR + file.getName());
        }
    }

    /**
     * Abre y restaura el estado completo de un partido desde un archivo binario (.dat).
     * @param file archivo origen con extension .dat.
     * @throws SoccerException si el archivo no existe, no es .dat, esta corrupto o falla la lectura.
     */
    public void open(File file) throws SoccerException {
        if (file == null) {
            throw new SoccerException(SoccerException.NULL_FILE);
        }
        if (!file.getName().endsWith(".dat")) {
            throw new SoccerException(SoccerException.INVALID_EXTENSION_DAT + file.getName());
        }
        if (!file.exists()) {
            throw new SoccerException(SoccerException.FILE_NOT_FOUND + file.getName());
        }
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            SoccerGame loaded = (SoccerGame) in.readObject();
            in.close();
            this.field = loaded.field;
            this.marker = loaded.marker;
            this.matchType = loaded.matchType;
            this.player1 = loaded.player1;
            this.player2 = loaded.player2;
            this.ball = loaded.ball;
            this.cpuController = loaded.cpuController;
            this.paused = loaded.paused;
            this.finished = loaded.finished;
            this.activePowerUp = loaded.activePowerUp;
            this.powerUpSpawnTimer = loaded.powerUpSpawnTimer;
            this.random = new Random();

        } catch (FileNotFoundException e) {
            Log.record(e);
            throw new SoccerException(SoccerException.FILE_NOT_FOUND + file.getName());
        } catch (ClassNotFoundException e) {
            Log.record(e);
            throw new SoccerException(SoccerException.CLASS_NOT_FOUND + e.getMessage());
        } catch (InvalidClassException | StreamCorruptedException | EOFException | ClassCastException e) {
            Log.record(e);
            throw new SoccerException(SoccerException.CORRUPTED_FILE + file.getName());
        } catch (IOException e) {
            Log.record(e);
            throw new SoccerException(SoccerException.IO_ERROR + file.getName());
        }
    }

    /**
     * Guarda unicamente el progreso de ambos jugadores (puntos y estadisticas) en un archivo .dat.
     */
    public void saveProgress(File file) throws SoccerException {
        if (file == null) {
            throw new SoccerException(SoccerException.NULL_FILE);
        }
        if (!file.getName().endsWith(".dat")) {
            throw new SoccerException(SoccerException.INVALID_EXTENSION_DAT + file.getName());
        }
        int[][] data = {
            {
                this.player1.getPoints(), this.player1.getUpgradesBought(),
                this.player1.getSpeedStat(), this.player1.getJumpStat(),
                this.player1.getKickStat(), this.player1.getSurvivalStat()
            },
            {
                this.player2.getPoints(), this.player2.getUpgradesBought(),
                this.player2.getSpeedStat(), this.player2.getJumpStat(),
                this.player2.getKickStat(), this.player2.getSurvivalStat()
            }
        };
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(data);
            out.close();
        } catch (IOException e) {
            Log.record(e);
            throw new SoccerException(SoccerException.IO_ERROR + file.getName());
        }
    }

    /**
     * Carga el progreso de puntos y estadisticas de ambos jugadores desde un archivo .dat.
     */
    public void loadProgress(File file) throws SoccerException {
        if (file == null) {
            throw new SoccerException(SoccerException.NULL_FILE);
        }
        if (!file.getName().endsWith(".dat")) {
            throw new SoccerException(SoccerException.INVALID_EXTENSION_DAT + file.getName());
        }
        if (!file.exists()) {
            throw new SoccerException(SoccerException.FILE_NOT_FOUND + file.getName());
        }
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            int[][] data = (int[][]) in.readObject();
            in.close();
            if (data == null || data.length < 2 || data[0].length < 6 || data[1].length < 6) {
                throw new SoccerException(SoccerException.CORRUPTED_FILE + file.getName());
            }
            this.player1.restoreProgress(data[0][0], data[0][1], data[0][2], data[0][3], data[0][4], data[0][5]);
            this.player2.restoreProgress(data[1][0], data[1][1], data[1][2], data[1][3], data[1][4], data[1][5]);
        } catch (ClassNotFoundException e) {
            Log.record(e);
            throw new SoccerException(SoccerException.CLASS_NOT_FOUND + e.getMessage());
        } catch (InvalidClassException | StreamCorruptedException | EOFException | ClassCastException e) {
            Log.record(e);
            throw new SoccerException(SoccerException.CORRUPTED_FILE + file.getName());
        } catch (IOException e) {
            Log.record(e);
            throw new SoccerException(SoccerException.IO_ERROR + file.getName());
        }
    }

    private void updateFieldPowerUp(double dt) {
        if (this.activePowerUp == null) {
            this.powerUpSpawnTimer -= dt;
            if (this.powerUpSpawnTimer <= 0.0) {
                spawnRandomPowerUp();
                this.powerUpSpawnTimer = 10.0;
            }
        } else {
            this.activePowerUp.update(dt);
            if (this.activePowerUp.intersects(this.player1)) {
                this.activePowerUp.applyTo(this.player1);
                this.activePowerUp = null;
            } else if (this.activePowerUp.intersects(this.player2)) {
                this.activePowerUp.applyTo(this.player2);
                this.activePowerUp = null;
            } else if (this.activePowerUp.isExpired()) {
                this.activePowerUp = null;
            }
        }
    }

    private void spawnRandomPowerUp() {
        double minX = this.field.getWidth() * 0.25;
        double maxX = this.field.getWidth() * 0.75 - FieldPowerUp.SIZE;
        double px = minX + this.random.nextDouble() * (maxX - minX);
        double py = this.field.getGroundY() - FieldPowerUp.SIZE - (40.0 + this.random.nextDouble() * 110.0);

        int pick = this.random.nextInt(3);
        if (pick == 0) {
            this.activePowerUp = new HealthPowerUp(px, py);
        } else if (pick == 1) {
            this.activePowerUp = new EnergyPowerUp(px, py);
        } else {
            this.activePowerUp = new SpeedPowerUp(px, py);
        }
    }

    /**
     * Permite colocar un elemento especifico en cancha (util para extensibilidad y pruebas unitarias).
     */
    public void spawnPowerUp(FieldPowerUp powerUp) throws SoccerException {
        if (powerUp == null) {
            throw new SoccerException(SoccerException.INVALID_CONFIG);
        }
        this.activePowerUp = powerUp;
    }

    public FieldPowerUp getActivePowerUp() {
        return this.activePowerUp;
    }

    public void setCpuController(CPUController cpuController) {
        this.cpuController = cpuController;
    }

    public CPUController getCpuController() {
        return this.cpuController;
    }

    public boolean isVsMachine() {
        return this.cpuController != null;
    }

    public void setPaused(boolean paused){ 
        this.paused = paused; 
    }

    public boolean isPaused() { 
        return this.paused; 
    }

    public boolean isFinished() { 
        return this.finished; 
    }

    public Field getField() { 
        return this.field; 
    }

    public Marker getMarker() { 
        return this.marker; 
    }

    public PlayerGame getPlayer1() { 
        return this.player1; 
    }
    public PlayerGame getPlayer2() { 
        return this.player2; 
    }

    public Ball getBall() { 
        return this.ball; 
    }

    public MatchType getMatchType() { 
        return this.matchType; 
    }

}