package flextimer.turnDurationFlow.strategy;

import flextimer.timerTurnFlow.util.GameTurn;
import flextimer.turnDurationFlow.TurnDurationFlow;
import flextimer.turnDurationFlow.util.ChessTurnDurationIncrementsReader;

import java.time.Duration;

public class ChessTurnDurationFlow implements TurnDurationFlow {
    private final ChessTurnDurationIncrementsReader increments;

    public ChessTurnDurationFlow(ChessTurnDurationIncrementsReader increments) {
        this.increments = increments;
    }

    public Duration remainingAfterTurnStart(GameTurn gameTurn, Duration remainingBeforeStart) {
        return remainingBeforeStart.plus(increments.increment(gameTurn));
    }
}

