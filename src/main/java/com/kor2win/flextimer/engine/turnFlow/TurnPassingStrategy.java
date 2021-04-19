package com.kor2win.flextimer.engine.turnFlow;

public abstract class TurnPassingStrategy {
    public TimerTurn firstTurn(PlayersOrder playersOrder) {
        return new TimerTurn(GameRound.FIRST, playersOrder.first());
    }

    public abstract TimerTurn nextTurn(PlayersOrder playersOrder, TimerTurn current, int phasesCount);

    public abstract SimultaneousTurns firstSimultaneousTurns(PlayersOrder playersOrder, int phasesCount);

    public abstract SimultaneousTurns simultaneousTurnsAfterTurn(PlayersOrder playersOrder, TimerTurn lastPlayed, int phasesCount);
}
