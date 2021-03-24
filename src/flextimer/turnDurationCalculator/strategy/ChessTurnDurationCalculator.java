package flextimer.turnDurationCalculator.strategy;

import flextimer.turnFlow.util.GameTurn;
import flextimer.turnDurationCalculator.TurnDurationCalculator;
import flextimer.turnDurationCalculator.util.ChessTurnDurationIncrementsReader;

import java.time.Duration;

public class ChessTurnDurationCalculator implements TurnDurationCalculator {
    private final ChessTurnDurationIncrementsReader increments;

    public ChessTurnDurationCalculator(ChessTurnDurationIncrementsReader increments) {
        this.increments = increments;
    }

    public Duration totalTurnDuration(GameTurn gameTurn, Duration accumulated) {
        return accumulated.plus(increments.increment(gameTurn));
    }
}

