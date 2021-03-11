package flextimer.turnDurationFlow.util;

import java.time.Duration;

public interface StartedTurnDuration {
    Duration totalDuration();
    Duration durationOnFinish(Duration elapsedFromStart);
}
