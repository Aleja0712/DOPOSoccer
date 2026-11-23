package domain;

/**
 * Marcador de goles del partido.
 */
public class Marker {
    private int goals1;
    private int goals2;

    public Marker() {
        reset();
    }

    public void addGoal(int playerSide) {
        if (playerSide == 1) goals1++;
        else if (playerSide == 2) goals2++;
    }

    public void reset() {
        this.goals1 = 0;
        this.goals2 = 0;
    }

    public int getGoals1() { 
        return goals1; 
    }
    
    public int getGoals2() { 
        return goals2; 
    }
}