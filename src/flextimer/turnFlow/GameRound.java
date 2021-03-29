package flextimer.turnFlow;

public class GameRound {
    public static final GameRound FIRST = new GameRound(1, 1);

    public final int roundNumber;
    public final int phase;

    private final int hashCode;

    public GameRound(int roundNumber, int phase) {
        this.roundNumber = roundNumber;
        this.phase = phase;
        hashCode = calculateHashCode();
    }

    public boolean equals(GameRound o) {
        return roundNumber == o.roundNumber && phase == o.phase;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GameRound
                ? this.equals((GameRound) obj)
                : super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private int calculateHashCode() {
        return (roundNumber ^ 0xf0f0f0f0) | ((roundNumber * phase) ^ 0x0f0f0f0f);
    }
}
