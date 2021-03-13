package flextimer.turnDurationFlow.strategy;

import flextimer.timerTurnFlow.util.GameTurn;
import flextimer.turnDurationFlow.TurnDurationCalculator;
import flextimer.turnDurationFlow.util.ChessTurnDurationIncrementsReader;

import java.time.Duration;

public class ChessTurnDurationCalculator implements TurnDurationCalculator {
    private final ChessTurnDurationIncrementsReader increments;

    public ChessTurnDurationCalculator(ChessTurnDurationIncrementsReader increments) {
        this.increments = increments;
    }

    public Duration remainingAfterTurnStart(GameTurn gameTurn, Duration remainingBeforeStart) {
        return remainingBeforeStart.plus(increments.increment(gameTurn));
    }
}

