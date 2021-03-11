package flextimer.turnDurationFlow;

import flextimer.GameTurn;
import flextimer.turnDurationFlow.util.StartedTurnDuration;

import java.time.Duration;

public interface TurnDurationFlow {
    StartedTurnDuration startTurn(GameTurn gameTurn, Duration remainingDuration);
}
