package com.kor2win.flextimer.timer.ui;

import java.time.*;

public class TimerClock {
    private Clock clock;

    public TimerClock(Clock clock) {
        this.clock = clock;
    }

    protected void setClock(Clock clock) {
        this.clock = clock;
    }

    protected void shiftClock(Duration shift) {
        clock = Clock.offset(clock, shift);
    }

    public Instant instant() {
        return clock.instant();
    }
}
