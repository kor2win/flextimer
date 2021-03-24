package flextimer.turnFlow;

import flextimer.player.*;
import flextimer.player.exception.*;
import flextimer.turnFlow.util.*;

public class TurnFlow implements FutureTurnAccessor {
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

    @Override
    public TimerTurn nextTurnOfSamePlayer(TimerTurn timerTurn) {
        TimerTurn t = this.nextTurn(timerTurn);

        while (!t.player.equals(timerTurn.player)) {
            t = this.nextTurn(t);
        }

        return t;
    }
}