package com.kor2win.flextimer.engine.turnFlow;

public class TurnFlow implements FutureTurnAccess {
    private final TurnPassingStrategy turnPassingStrategy;
    private final TurnFlowConfig config;

    public TurnFlow(TurnPassingStrategy turnPassingStrategy, TurnFlowConfig config) {
        this.turnPassingStrategy = turnPassingStrategy;
        this.config = config;
    }

    public SimultaneousTurns firstSimultaneousTurns() {
        return turnPassingStrategy.firstSimultaneousTurns(config.playersOrder(), config.phasesCount());
    }

    public SimultaneousTurns nextRoundOfTurns(TimerTurn current) {
        return turnPassingStrategy.simultaneousTurnsAfterTurn(config.playersOrder(), current, config.phasesCount());
    }

    public TimerTurn nextTurnOfSamePlayer(TimerTurn timerTurn) {
        TimerTurn t = nextTurn(timerTurn);

        while (!t.player.equals(timerTurn.player)) {
            t = nextTurn(t);
        }

        return t;
    }

    private TimerTurn nextTurn(TimerTurn current) {
        return turnPassingStrategy.nextTurn(config.playersOrder(), current, config.phasesCount());
    }
}