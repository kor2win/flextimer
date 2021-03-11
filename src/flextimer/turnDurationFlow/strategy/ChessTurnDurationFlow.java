package flextimer.turnDurationFlow.strategy;

import flextimer.GameTurn;
import flextimer.turnDurationFlow.util.StartedTurnDuration;
import flextimer.turnDurationFlow.TurnDurationFlow;
import flextimer.turnDurationFlow.util.ChessTurnDurationIncrementsReader;

import java.time.Duration;

public class ChessTurnDurationFlow implements TurnDurationFlow {
    private final ChessTurnDurationIncrementsReader increments;

    public ChessTurnDurationFlow(ChessTurnDurationIncrementsReader increments) {
        this.increments = increments;
    }

    public StartedTurnDuration startTurn(GameTurn gameTurn, Duration remainingDuration) {
        return new ChessFlowStartedTurnDuration(gameTurn, remainingDuration);
    }

    private class ChessFlowStartedTurnDuration implements StartedTurnDuration {
        private final Duration totalDuration;

        public ChessFlowStartedTurnDuration(GameTurn gameTurn, Duration remainingDuration) {
            this.totalDuration = remainingDuration.plus(increments.increment(gameTurn));
        }

        public Duration totalDuration() {
            return totalDuration;
        }

        public Duration durationOnFinish(Duration elapsedFromStart) {
            return totalDuration.minus(elapsedFromStart);
        }
    }
}

