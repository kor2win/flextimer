package flextimer.timeBank;


import flextimer.turnFlow.util.*;

import java.time.*;

public interface TimeBank {
    Duration getAccumulated(TimerTurn timerTurn);

    void saveRemainingDuration(TimerTurn timerTurn, Duration remaining);
}
