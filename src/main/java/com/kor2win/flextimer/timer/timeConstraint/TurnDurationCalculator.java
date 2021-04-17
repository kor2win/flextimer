package com.kor2win.flextimer.timer.timeConstraint;

import com.kor2win.flextimer.timer.turnFlow.*;

import java.time.*;

public interface TurnDurationCalculator {
    Duration totalTurnDuration(GameRound gameRound, Duration accumulated);
}
