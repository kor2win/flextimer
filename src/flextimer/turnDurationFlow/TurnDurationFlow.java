package flextimer.turnDurationFlow;

import flextimer.timerTurnFlow.util.GameTurn;

import java.time.Duration;

public interface TurnDurationFlow {
    Duration remainingAfterTurnStart(GameTurn gameTurn, Duration remainingBeforeStart);
}
