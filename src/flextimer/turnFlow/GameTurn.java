package flextimer.turnFlow;

public class GameTurn {
    public final int turnNumber;
    public final int phase;
    private final int hashCode;

    public GameTurn(int turnNumber, int phase) {
        this.turnNumber = turnNumber;
        this.phase = phase;
        hashCode = calculateHashCode();
    }

    public boolean equals(GameTurn o) {
        return turnNumber == o.turnNumber && phase == o.phase;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private int calculateHashCode() {
        return (turnNumber ^ 0xf0f0f0f0) | ((turnNumber * phase) ^ 0x0f0f0f0f);
    }
}
