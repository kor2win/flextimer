package flextimer.turnDurationCalculator;

import flextimer.timerTurnFlow.util.GameTurn;

import java.time.Duration;

public interface TurnDurationCalculator {
    Duration remainingAfterTurnStart(GameTurn gameTurn, Duration remainingBeforeStart);
}
