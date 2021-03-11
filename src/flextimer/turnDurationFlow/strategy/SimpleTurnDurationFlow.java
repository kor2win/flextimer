package flextimer.turnDurationFlow.strategy;

import flextimer.GameTurn;
import flextimer.turnDurationFlow.util.StartedTurnDuration;
import flextimer.turnDurationFlow.TurnDurationFlow;

import java.time.Duration;

public class SimpleTurnDurationFlow implements TurnDurationFlow {
    public StartedTurnDuration startTurn(GameTurn gameTurn, Duration remainingDuration) {
        return new SimpleFlowStartedTurnDuration(remainingDuration);
    }

    private static class SimpleFlowStartedTurnDuration implements StartedTurnDuration {
        private final Duration totalDuration;

        public SimpleFlowStartedTurnDuration(Duration remainingDuration) {
            this.totalDuration = remainingDuration;
        }

        public Duration totalDuration() {
            return totalDuration;
        }

        public Duration durationOnFinish(Duration elapsedFromStart) {
            return totalDuration.minus(elapsedFromStart);
        }
    }
}
