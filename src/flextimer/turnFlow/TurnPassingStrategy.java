package flextimer.turnFlow;

public abstract class TurnPassingStrategy {
    public TimerTurn firstTurn(PlayersOrder playersOrder) {
        return new TimerTurn(GameRound.FIRST, playersOrder.first());
    }

    public abstract TimerTurn nextTurn(PlayersOrder playersOrder, TimerTurn current, int phasesCount) throws UnknownPlayer;

    public abstract SimultaneousTurns firstSimultaneousTurns(PlayersOrder playersOrder, int phasesCount);

    public abstract SimultaneousTurns simultaneousTurnsAfterTurn(PlayersOrder playersOrder, TimerTurn lastPlayed, int phasesCount) throws UnknownPlayer;
}
