package flextimer.timeConstraint;

import flextimer.turnFlow.*;

import java.time.*;

public interface TimeBank {
    Duration getAccumulated(TimerTurn timerTurn);

    void saveRemaining(TimerTurn timerTurn, Duration remaining);
}
