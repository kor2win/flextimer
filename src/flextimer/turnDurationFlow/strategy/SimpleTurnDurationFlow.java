package flextimer.turnDurationFlow.strategy;

import flextimer.timerTurnFlow.util.GameTurn;
import flextimer.turnDurationFlow.TurnDurationFlow;

import java.time.Duration;

public class SimpleTurnDurationFlow implements TurnDurationFlow {
    public Duration remainingAfterTurnStart(GameTurn gameTurn, Duration remainingBeforeStart) {
        return remainingBeforeStart;
    }
}
