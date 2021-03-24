package flextimer.turnFlow;

public class TimerTurn {
    public final GameTurn gameTurn;
    public final Player player;
    private final int hashCode;

    public TimerTurn(GameTurn gameTurn, Player player) {
        this.gameTurn = gameTurn;
        this.player = player;
        hashCode = calculateHashCode();
    }

    public boolean equals(TimerTurn t) {
        return gameTurn.equals(t.gameTurn) && player.equals(t.player);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private int calculateHashCode() {
        return (gameTurn.hashCode() ^ 0xf0f0f0f0) | (player.hashCode() ^ 0x0f0f0f0f);
    }

    public int turnNumber() {
        return gameTurn.turnNumber;
    }

    public int phase() {
        return gameTurn.phase;
    }
}
