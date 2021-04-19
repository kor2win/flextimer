package com.kor2win.flextimer.engine.timeConstraint;

import com.kor2win.flextimer.engine.turnFlow.*;

import java.time.*;

public interface TimeBank {
    Duration getAccumulated(TimerTurn timerTurn);

    void saveRemaining(TimerTurn timerTurn, Duration remaining);
}
