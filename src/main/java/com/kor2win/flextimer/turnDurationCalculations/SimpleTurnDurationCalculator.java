package com.kor2win.flextimer.turnDurationCalculations;

import com.kor2win.flextimer.engine.turnFlow.*;
import com.kor2win.flextimer.engine.timeConstraint.*;

import java.time.*;

public class SimpleTurnDurationCalculator implements TurnDurationCalculator {
    public Duration totalTurnDuration(GameRound gameRound, Duration accumulated) {
        return accumulated;
    }
}
