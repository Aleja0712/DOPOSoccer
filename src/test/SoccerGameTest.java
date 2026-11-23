package test;

import domain.CPUController;
import domain.Character;
import domain.ClumsyCPU;
import domain.DefensiveCPU;
import domain.EnergyPowerUp;
import domain.ExpertCPU;
import domain.GoalMatch;
import domain.HealthPowerUp;
import domain.OffensiveCPU;
import domain.PlayerGame;
import domain.PowerBar;
import domain.SoccerException;
import domain.SoccerGame;
import domain.SpeedPowerUp;
import domain.TimeMatch;
import static org.junit.Assert.*;

import java.io.File;
import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad del dominio de DOPOSoccer (version 1).
 */
public class SoccerGameTest {
    private SoccerGame game;
    private PlayerGame p1;
    private PlayerGame p2;

    /**
     * Antes de cada prueba se crea un partido nuevo por goles (meta 3):
     * Camerun (jugador 1) contra Corea del Sur (jugador 2).
     */
    @Before
    public void setUp() throws SoccerException {
        this.game = new SoccerGame(2, new GoalMatch(3), Character.createCameroon(), Character.createSouthKorea());
        this.p1 = this.game.getPlayer1();
        this.p2 = this.game.getPlayer2();
    }

    // ---------- Estadisticas, dano y vida (Anexo 2) ----------

    @Test
    public void shouldCalculateStatsAndDamageAccordingToAnnex2Example() throws SoccerException {
        this.p1.addPoints(20000);
        this.p2.addPoints(20000);
        for (int i = 0; i < 4; i++) {
            this.p1.upgradeStat("KICK");
        }
        for (int i = 0; i < 2; i++) {
            this.p2.upgradeStat("SURVIVAL");
        }
        assertEquals(120.0, this.p2.getMaxHealth(), 0.01);
        assertEquals(1.4, this.p1.getKickMultiplier(), 0.01);
        assertEquals(14.0, this.p1.getNormalKickDamage(), 0.01);
        assertEquals(84.0, this.p1.getPowerShotDamage(), 0.01);

        this.p2.receiveDamage(this.p1.getNormalKickDamage());
        assertEquals(106.0, this.p2.getHealth(), 0.01);
    }

    @Test
    public void shouldDoubleTheUpgradeCostAfterEachPurchase() throws SoccerException {
        this.p1.addPoints(3500);
        assertEquals(500, this.p1.getNextUpgradeCost());
        this.p1.upgradeStat("SPEED");
        assertEquals(1000, this.p1.getNextUpgradeCost());
        this.p1.upgradeStat("JUMP");
        assertEquals(2000, this.p1.getNextUpgradeCost());
        this.p1.upgradeStat("KICK");
        assertEquals(0, this.p1.getPoints());
        assertEquals(4000, this.p1.getNextUpgradeCost());
    }

    @Test
    public void shouldThrowSoccerExceptionWhenPointsAreInsufficient() {
        try {
            this.p1.upgradeStat("SPEED");
            fail("Debio lanzar SoccerException por puntos insuficientes.");
        } catch (SoccerException e) {
            assertEquals(SoccerException.INSUFFICIENT_POINTS, e.getMessage());
        }
    }

    @Test
    public void shouldNotUpgradeAStatAboveTen() throws SoccerException {
        this.p1.addPoints(1000000);
        for (int i = 0; i < 9; i++) {
            this.p1.upgradeStat("SPEED");
        }
        assertEquals(10, this.p1.getSpeedStat());
        try {
            this.p1.upgradeStat("SPEED");
            fail("Debio lanzar SoccerException porque Speed ya esta en 10.");
        } catch (SoccerException e) {
            assertEquals(SoccerException.INVALID_STAT_LEVEL, e.getMessage());
        }
    }

    // ---------- Aturdimiento ----------

    @Test
    public void shouldStunPlayerFor3SecondsAndRestoreHealthWhenReachingZero() {
        this.p2.receiveDamage(150.0);
        assertEquals(0.0, this.p2.getHealth(), 0.01);
        assertTrue(this.p2.isStunned());
        assertFalse(this.p2.canAct());

        this.p2.update(3.1, this.game.getField());
        assertFalse(this.p2.isStunned());
        assertEquals(100.0, this.p2.getHealth(), 0.01);
    }

    // ---------- Barra de poder y aura (reglas 1, 2, 3 y 7) ----------

    @Test
    public void shouldFillThePowerBarInTenSeconds() {
        PowerBar bar = new PowerBar();
        bar.update(5.0);
        assertEquals(50.0, bar.getCharge(), 0.01);
        assertFalse(bar.isFull());
        bar.update(5.0);
        assertTrue(bar.isFull());
    }

    @Test
    public void shouldIncreaseThePowerBarAQuarterWhenAGoalIsConceded() {
        PowerBar bar = new PowerBar();
        bar.onGoalConceded();
        assertEquals(25.0, bar.getCharge(), 0.01);
    }

    @Test
    public void shouldNotActivatePowerWhenTheBarIsNotFull() {
        try {
            this.p1.activatePower();
            fail("Debio lanzar SoccerException porque la barra no esta llena.");
        } catch (SoccerException e) {
            assertEquals(SoccerException.POWER_NOT_READY, e.getMessage());
        }
    }

    @Test
    public void shouldActivateTheAuraAndEmptyTheBarWhenItIsFull() throws SoccerException {
        this.p1.getPowerBar().update(10.0);
        this.p1.activatePower();
        assertTrue(this.p1.hasPowerAura());
        assertEquals(0.0, this.p1.getPowerBar().getCharge(), 0.01);
    }

    @Test
    public void shouldNotActivateASecondPowerWhileTheAuraIsActive() throws SoccerException {
        this.p1.getPowerBar().update(10.0);
        this.p1.activatePower();
        this.p1.getPowerBar().update(10.0);
        try {
            this.p1.activatePower();
            fail("Debio lanzar SoccerException porque ya tiene un aura activa.");
        } catch (SoccerException e) {
            assertEquals(SoccerException.POWER_NOT_READY, e.getMessage());
        }
    }

    // ---------- Efectos de los tiros de poder (Anexo 1 y regla 8) ----------

    @Test
    public void shouldReplaceThePreviousEffectWithTheNewOne() {
        this.p2.applyEffect("ELECTROCUTADO", 3.0);
        this.p2.applyEffect("CONFUSION", 2.5);
        assertEquals("CONFUSION", this.p2.getCurrentEffect());
    }

    @Test
    public void shouldNotJumpWhenElectrocuted() {
        double groundY = this.game.getField().getGroundY();
        this.p2.applyEffect("ELECTROCUTADO", 3.0);
        this.p2.jump(groundY);
        assertEquals(0.0, this.p2.getVy(), 0.01);

        this.p1.jump(groundY);
        assertTrue(this.p1.getVy() < 0);
    }

    // ---------- Tipos de partido y puntos ----------

    @Test
    public void shouldFinishGoalMatchWhenTheTargetIsReached() {
        this.game.getMarker().addGoal(1);
        this.game.getMarker().addGoal(1);
        assertFalse(this.game.getMatchType().isFinished(this.game));
        this.game.getMarker().addGoal(1);
        assertTrue(this.game.getMatchType().isFinished(this.game));
        assertSame(this.p1, this.game.getMatchType().getWinner(this.game));
    }

    @Test
    public void shouldEnterGoldenGoalWhenTimeEndsTied() throws SoccerException {
        TimeMatch match = new TimeMatch(1);
        SoccerGame timeGame = new SoccerGame(2, match, Character.createNigeria(), Character.createUSA());
        match.update(60.0, timeGame);
        assertTrue(match.isGoldenGoal());
        assertFalse(match.isFinished(timeGame));

        timeGame.getMarker().addGoal(2);
        match.onGoalScored(2, timeGame);
        assertTrue(match.isFinished(timeGame));
        assertSame(timeGame.getPlayer2(), match.getWinner(timeGame));
    }

    @Test
    public void shouldFinishTimeMatchWhenTimeEndsWithAWinner() throws SoccerException {
        TimeMatch match = new TimeMatch(1);
        SoccerGame timeGame = new SoccerGame(2, match, Character.createNigeria(), Character.createUSA());
        timeGame.getMarker().addGoal(1);
        match.update(60.0, timeGame);
        assertFalse(match.isGoldenGoal());
        assertTrue(match.isFinished(timeGame));
    }

    @Test
    public void shouldGive100PointsToTheWinnerAnd25ToTheLoser() {
        this.game.getMarker().addGoal(2);
        this.game.getMarker().addGoal(2);
        this.game.getMarker().addGoal(2);
        this.game.finishMatch();
        assertEquals(25, this.p1.getPoints());
        assertEquals(100, this.p2.getPoints());
    }

    /**
     * Prueba creada al encontrar un error: al terminar el partido antes de tiempo
     * (boton "Terminar Partido"), el jugador que iba ganando no recibia sus puntos.
     */
    @Test
    public void shouldGivePointsToTheLeaderWhenTheMatchIsEndedEarly() {
        this.game.getMarker().addGoal(1);
        this.game.finishMatch();
        assertEquals(100, this.p1.getPoints());
        assertEquals(25, this.p2.getPoints());
    }

    @Test
    public void shouldRejectAMatchWithZeroMinutes() {
        try {
            new TimeMatch(0);
            fail("Debio lanzar SoccerException por configuracion invalida.");
        } catch (SoccerException e) {
            assertEquals(SoccerException.INVALID_CONFIG, e.getMessage());
        }
    }

    @Test
    public void shouldSaveAndOpenMatchPreservingCompleteState() throws SoccerException {
        SoccerGame original = new SoccerGame(
            2,
            new GoalMatch(5),
            Character.createSouthKorea(),
            Character.createCameroon()
        );
        original.setCpuController(new ExpertCPU());
        original.getPlayer1().addPoints(600);
        original.getPlayer1().upgradeStat("KICK");
        original.getMarker().addGoal(1);
        original.getMarker().addGoal(2);
        original.getPlayer2().receiveDamage(20.0);

        File file = new File("test_match_v2.dat");
        original.saveAs(file);

        SoccerGame loaded = new SoccerGame(
            1,
            new TimeMatch(1),
            Character.createNigeria(),
            Character.createUSA()
        );
        loaded.open(file);

        assertEquals(1, loaded.getMarker().getGoals1());
        assertEquals(1, loaded.getMarker().getGoals2());
        assertEquals(2, loaded.getPlayer1().getKickStat());
        assertEquals(100, loaded.getPlayer1().getPoints());
        assertEquals(80.0, loaded.getPlayer2().getHealth(), 0.01);
        assertTrue(loaded.isVsMachine());
        assertEquals("Experta", loaded.getCpuController().getProfileName());
        assertEquals("Corea del Sur", loaded.getPlayer1().getCharacter().getCountry());

        file.delete();
    }

    @Test
    public void shouldThrowDetailedSoccerExceptionsOnInvalidPersistenceFiles() throws Exception {
        SoccerGame game = new SoccerGame(
            2,
            new GoalMatch(3),
            Character.createSouthKorea(),
            Character.createNigeria()
        );

        // 1. Extension invalida al guardar
        try {
            game.saveAs(new File("partida.txt"));
            fail("Debio lanzar SoccerException por extension invalida.");
        } catch (SoccerException e) {
            assertTrue(e.getMessage().startsWith(SoccerException.INVALID_EXTENSION_DAT));
        }

        // 2. Archivo inexistente al abrir
        try {
            game.open(new File("no_existe_v2.dat"));
            fail("Debio lanzar SoccerException por archivo inexistente.");
        } catch (SoccerException e) {
            assertTrue(e.getMessage().startsWith(SoccerException.FILE_NOT_FOUND));
        }

        // 3. Archivo .dat corrupto
        File corrupt = new File("corrupto_v2.dat");
        PrintWriter writer = new PrintWriter(corrupt);
        writer.println("contenido no serializado");
        writer.close();
        try {
            game.open(corrupt);
            fail("Debio lanzar SoccerException por archivo corrupto.");
        } catch (SoccerException e) {
            assertTrue(e.getMessage().startsWith(SoccerException.CORRUPTED_FILE));
        } finally {
            corrupt.delete();
        }
    }

    @Test
    public void shouldSaveAndLoadPlayerProgressBetweenSessions() throws SoccerException {
        SoccerGame session1 = new SoccerGame(
            2,
            new GoalMatch(3),
            Character.createSouthKorea(),
            Character.createUSA()
        );
        session1.getPlayer1().addPoints(1600);
        session1.getPlayer1().upgradeStat("SPEED");
        session1.getPlayer1().upgradeStat("SURVIVAL");

        File progressFile = new File("test_progress_v2.dat");
        session1.saveProgress(progressFile);

        SoccerGame session2 = new SoccerGame(
            2,
            new TimeMatch(2),
            Character.createNigeria(),
            Character.createCameroon()
        );
        session2.loadProgress(progressFile);

        assertEquals(2, session2.getPlayer1().getSpeedStat());
        assertEquals(2, session2.getPlayer1().getSurvivalStat());
        assertEquals(100, session2.getPlayer1().getPoints());
        assertEquals(110.0, session2.getPlayer1().getMaxHealth(), 0.01);

        progressFile.delete();
    }

    @Test
    public void shouldExecuteAllFourMachineProfilesWithoutErrors() throws SoccerException {
        CPUController[] profiles = {
            new ClumsyCPU(),
            new OffensiveCPU(),
            new DefensiveCPU(),
            new ExpertCPU()
        };

        for (int i = 0; i < profiles.length; i++) {
            SoccerGame game = new SoccerGame(
                2,
                new TimeMatch(1),
                Character.createSouthKorea(),
                Character.createCameroon()
            );
            game.setCpuController(profiles[i]);
            assertTrue(game.isVsMachine());
            for (int step = 0; step < 40; step++) {
                game.update(0.025);
            }
            assertFalse(game.isFinished());
        }
    }

    @Test
    public void shouldApplyColombiaFireShotAndBrazilFreezePowers() throws SoccerException {
        SoccerGame game = new SoccerGame(
            2,
            new GoalMatch(5),
            Character.createColombia(),
            Character.createBrazil()
        );
        PlayerGame p1 = game.getPlayer1();
        PlayerGame p2 = game.getPlayer2();

        // Colombia aplica quemadura (QUEMADO) y reduce vida con el tiempo
        p1.getCharacter().getPower().applyEffectTo(p2);
        assertEquals("QUEMADO", p2.getCurrentEffect());
        double healthBefore = p2.getHealth();
        p2.update(1.0, game.getField());
        assertTrue(p2.getHealth() < healthBefore);

        // Brasil congela al rival (CONGELADO) impidiendo que actue
        p2.getCharacter().getPower().applyEffectTo(p1);
        assertEquals("CONGELADO", p1.getCurrentEffect());
        assertFalse(p1.canAct());
    }

    @Test
    public void shouldApplyFieldPowerUpsAndPersistActivePowerUp() throws SoccerException {
        SoccerGame game = new SoccerGame(
            2,
            new GoalMatch(5),
            Character.createSouthKorea(),
            Character.createCameroon()
        );
        PlayerGame p1 = game.getPlayer1();
        p1.receiveDamage(40.0); // Queda en 60

        // Recoger botiquin en la posicion de P1
        game.spawnPowerUp(new HealthPowerUp(p1.getX(), p1.getY()));
        game.update(0.025);
        assertNull(game.getActivePowerUp());
        assertEquals(90.0, p1.getHealth(), 0.5);

        // Recoger orbe de energia (+50% de barra de poder)
        game.spawnPowerUp(new EnergyPowerUp(p1.getX(), p1.getY()));
        game.update(0.025);
        assertTrue(p1.getPowerBar().getCharge() >= 50.0);

        // Recoger turbo de velocidad
        game.spawnPowerUp(new SpeedPowerUp(p1.getX(), p1.getY()));
        game.update(0.025);
        assertTrue(p1.hasSpeedBoost());
    }

    @Test
    public void shouldNotAllowNullPowerUp() throws SoccerException {
        SoccerGame game = new SoccerGame(
            2,
            new GoalMatch(3),
            Character.createSouthKorea(),
            Character.createUSA()
        );
        try {
            game.spawnPowerUp(null);
            fail("Debio lanzar SoccerException al intentar colocar un power-up nulo.");
        } catch (SoccerException e) {
            assertEquals(SoccerException.INVALID_CONFIG, e.getMessage());
        }
    }
}
