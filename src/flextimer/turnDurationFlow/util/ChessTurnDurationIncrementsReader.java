package flextimer.turnDurationFlow.util;

import flextimer.GameTurn;

import java.time.Duration;

public interface ChessTurnDurationIncrementsReader {
    Duration increment(GameTurn turn);
}
