package flextimer.turnDurationFlow.util;

import java.time.Duration;
import java.time.Instant;

public interface ContinuousTurn {
    void startTime(Instant instant);

    void end(Instant instant);

    void pauseTime(Instant instant);

    Instant depletedAt();

    Duration remaining(Instant instant);

    boolean isTimerGoing();

    boolean isEnded();
}
