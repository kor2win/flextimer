package com.kor2win.flextimer.engine.timeConstraint;

import com.kor2win.flextimer.engine.turnFlow.*;

import java.time.*;

public interface TurnDurationCalculator {
    Duration totalTurnDuration(GameRound gameRound, Duration accumulated);
}
