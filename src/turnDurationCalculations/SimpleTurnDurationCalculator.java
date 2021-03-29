package turnDurationCalculations;

import flextimer.timeConstraint.*;
import flextimer.turnFlow.*;

import java.time.*;

public class SimpleTurnDurationCalculator implements TurnDurationCalculator {
    public Duration totalTurnDuration(GameRound gameRound, Duration accumulated) {
        return accumulated;
    }
}
