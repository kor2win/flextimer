package com.kor2win.flextimer.timer.timeConstraint;

import com.kor2win.flextimer.timer.turnFlow.*;

import java.time.*;

public interface TimeBank {
    Duration getAccumulated(TimerTurn timerTurn);

    void saveRemaining(TimerTurn timerTurn, Duration remaining);
}
