package flextimer.turnFlow;

public abstract class TurnPassingStrategy {
    public TimerTurn firstTurn(PlayersOrder playersOrder) {
        GameTurn gameTurn = new GameTurn(1, 1);
        return new TimerTurn(
                gameTurn,
                playersOrder.first()
        );
    }

    public abstract TimerTurn turnAfter(PlayersOrder playersOrder, TimerTurn current, int phasesCount) throws UnknownPlayer;
}
