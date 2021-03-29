package flextimer.turnFlow;

public class TurnFlow {
    private final PlayersOrder playersOrder;
    private final TurnPassingStrategy turnPassingStrategy;
    private final int phasesCount;

    public TurnFlow(PlayersOrder playersOrder, TurnPassingStrategy turnPassingStrategy, int phasesCount) {
        this.playersOrder = playersOrder;
        this.turnPassingStrategy = turnPassingStrategy;
        this.phasesCount = phasesCount;
    }

    public TimerTurn nextTurnOfSamePlayer(TimerTurn timerTurn) throws UnknownPlayer {
        TimerTurn t = nextTurn(timerTurn);

        while (!t.player.equals(timerTurn.player)) {
            t = nextTurn(t);
        }

        return t;
    }

    private TimerTurn nextTurn(TimerTurn current) throws UnknownPlayer {
        return turnPassingStrategy.nextTurn(playersOrder, current, phasesCount);
    }

    public SimultaneousTurns firstSimultaneousTurns() {
        return turnPassingStrategy.firstSimultaneousTurns(playersOrder, phasesCount);
    }

    public SimultaneousTurns nextRoundOfTurns(TimerTurn current) throws UnknownPlayer {
        return turnPassingStrategy.simultaneousTurnsAfterTurn(playersOrder, current, phasesCount);
    }
}