package com.kor2win.flextimer.timer.turnFlow;

public class TimerTurn {
    public final GameRound gameRound;
    public final Player player;
    private final int hashCode;

    public TimerTurn(GameRound gameRound, Player player) {
        this.gameRound = gameRound;
        this.player = player;
        hashCode = calculateHashCode();
    }

    public boolean equals(TimerTurn t) {
        return gameRound.equals(t.gameRound) && player.equals(t.player);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TimerTurn
                ? this.equals((TimerTurn) obj)
                : super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private int calculateHashCode() {
        return (gameRound.hashCode() ^ 0xf0f0f0f0) | (player.hashCode() ^ 0x0f0f0f0f);
    }

    public int roundNumber() {
        return gameRound.roundNumber;
    }

    public int phase() {
        return gameRound.phase;
    }
}
