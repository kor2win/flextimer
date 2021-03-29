package flextimer.ui;

import org.jetbrains.annotations.*;

import java.time.*;

public class TimerClock {
    private Clock clock;

    public TimerClock(Clock clock) {
        this.clock = clock;
    }

    @TestOnly
    protected void setClock(Clock clock) {
        this.clock = clock;
    }

    @TestOnly
    protected void shiftClock(Duration shift) {
        clock = Clock.offset(clock, shift);
    }

    public Instant instant() {
        return clock.instant();
    }
}
