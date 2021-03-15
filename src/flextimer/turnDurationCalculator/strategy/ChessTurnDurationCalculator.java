package flextimer.turnDurationCalculator.strategy;

import flextimer.timerTurnFlow.util.GameTurn;
import flextimer.turnDurationCalculator.TurnDurationCalculator;
import flextimer.turnDurationCalculator.util.ChessTurnDurationIncrementsReader;

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

