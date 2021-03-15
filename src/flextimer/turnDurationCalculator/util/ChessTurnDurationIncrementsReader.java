package flextimer.turnDurationCalculator.util;

import flextimer.timerTurnFlow.util.GameTurn;

import java.time.Duration;

public interface ChessTurnDurationIncrementsReader {
    Duration increment(GameTurn turn);
}
