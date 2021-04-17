package com.kor2win.flextimer.turnDurationCalculations;

import com.kor2win.flextimer.timer.turnFlow.*;
import com.kor2win.flextimer.timer.timeConstraint.*;

import java.time.*;

public class SimpleTurnDurationCalculator implements TurnDurationCalculator {
    public Duration totalTurnDuration(GameRound gameRound, Duration accumulated) {
        return accumulated;
    }
}
