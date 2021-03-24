package flextimer.timeConstraint;

import flextimer.turnFlow.*;

import java.time.*;

public interface TurnDurationCalculator {
    Duration totalTurnDuration(GameTurn gameTurn, Duration accumulated);
}
