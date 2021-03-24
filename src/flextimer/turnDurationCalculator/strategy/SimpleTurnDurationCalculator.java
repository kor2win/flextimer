package flextimer.turnDurationCalculator.strategy;

import flextimer.turnFlow.util.GameTurn;
import flextimer.turnDurationCalculator.TurnDurationCalculator;

import java.time.Duration;

public class SimpleTurnDurationCalculator implements TurnDurationCalculator {
    public Duration totalTurnDuration(GameTurn gameTurn, Duration accumulated) {
        return accumulated;
    }
}
