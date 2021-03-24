package flextimer.timeConstraint.util;

import flextimer.player.Player;
import flextimer.turnFlow.util.TimerTurn;

import java.time.Duration;
import java.time.Instant;

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
