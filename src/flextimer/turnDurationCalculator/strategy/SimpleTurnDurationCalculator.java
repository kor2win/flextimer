package flextimer.turnDurationCalculator.strategy;

import flextimer.timerTurnFlow.util.GameTurn;
import flextimer.turnDurationCalculator.TurnDurationCalculator;

import java.time.Duration;

public class SimpleTurnDurationCalculator implements TurnDurationCalculator {
    public Duration remainingAfterTurnStart(GameTurn gameTurn, Duration remainingBeforeStart) {
        return remainingBeforeStart;
    }
}
