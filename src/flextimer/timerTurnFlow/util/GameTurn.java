package flextimer.timerTurnFlow.util;

public class GameTurn {
    public final int turnNumber;
    public final int phase;

    public GameTurn(int turnNumber, int phase) {
        this.turnNumber = turnNumber;
        this.phase = phase;
    }

    public boolean equals(GameTurn o) {
        return turnNumber == o.turnNumber && phase == o.phase;
    }
}
