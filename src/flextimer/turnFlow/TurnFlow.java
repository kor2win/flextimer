package flextimer.turnFlow;

public class TurnFlow {
    private final PlayersOrder playersOrder;
    private final TurnPassingStrategy turnPassingStrategy;
    private final int numberOfPhases;

    public TurnFlow(PlayersOrder playersOrder, TurnPassingStrategy turnPassingStrategy, int numberOfPhases) {
        this.playersOrder = playersOrder;
        this.turnPassingStrategy = turnPassingStrategy;
        this.numberOfPhases = numberOfPhases;
    }

    public TimerTurn firstTurn() {
        return turnPassingStrategy.firstTurn(playersOrder);
    }

    public TimerTurn nextTurn(TimerTurn current) {
        try {
            return turnPassingStrategy.turnAfter(playersOrder, current, numberOfPhases);
        } catch (UnknownPlayer ignored) {
        }

        return null;
    }

    public TimerTurn nextTurnOfSamePlayer(TimerTurn timerTurn) {
        TimerTurn t = this.nextTurn(timerTurn);

        while (!t.player.equals(timerTurn.player)) {
            t = this.nextTurn(t);
        }

        return t;
    }
}