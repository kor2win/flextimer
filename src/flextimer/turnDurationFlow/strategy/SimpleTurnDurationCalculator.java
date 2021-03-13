package flextimer.turnDurationFlow.strategy;

import flextimer.timerTurnFlow.util.GameTurn;
import flextimer.turnDurationFlow.TurnDurationCalculator;

import java.time.Duration;

public class SimpleTurnDurationCalculator implements TurnDurationCalculator {
    public Duration remainingAfterTurnStart(GameTurn gameTurn, Duration remainingBeforeStart) {
        return remainingBeforeStart;
    }
}
