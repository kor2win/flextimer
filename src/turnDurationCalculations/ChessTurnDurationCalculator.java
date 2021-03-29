package turnDurationCalculations;

import flextimer.timeConstraint.*;
import flextimer.turnFlow.*;

import java.time.*;

public class ChessTurnDurationCalculator implements TurnDurationCalculator {
    private final ChessTurnDurationIncrementsReader increments;

    public ChessTurnDurationCalculator(ChessTurnDurationIncrementsReader increments) {
        this.increments = increments;
    }

    public Duration totalTurnDuration(GameRound gameRound, Duration accumulated) {
        return accumulated.plus(increments.increment(gameRound));
    }
}

