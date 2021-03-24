package flextimer.turnDurationCalculator;

import flextimer.turnFlow.util.GameTurn;

import java.time.Duration;

public interface TurnDurationCalculator {
    Duration totalTurnDuration(GameTurn gameTurn, Duration accumulated);
}
