package flextimer.timeConstraint;

import flextimer.turnFlow.*;

import java.time.*;

public interface ConstrainedTimerTurn {
    void start(Instant instant);

    void end(Instant instant);

    void pause(Instant instant);

    Instant depletedAt();

    Duration remaining(Instant instant);

    boolean isGoing();

    boolean isEnded();

    TimerTurn timerTurn();

    Player player();
}
