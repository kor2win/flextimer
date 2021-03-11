package flextimer.turnDurationFlow;

import flextimer.timerTurnFlow.util.GameTurn;
import flextimer.turnDurationFlow.strategy.SimpleTurnDurationFlow;
import flextimer.turnDurationFlow.util.StartedTurnDuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleTurnDurationFlowTest {
    private static final GameTurn firstTurn = new GameTurn(1, 1);
    private static final Duration firstTurnDuration = Duration.ofSeconds(5);

    private SimpleTurnDurationFlow flow;
    private StartedTurnDuration startedFirstTurnDuration;

    @BeforeEach
    public void setUp() {
        flow = new SimpleTurnDurationFlow();
        startedFirstTurnDuration = flow.startTurn(firstTurn, firstTurnDuration);
    }

    @Test
    public void startedTurnDurations() {
        final Duration elapsedDuration = Duration.ofSeconds(2);

        assertEquals(firstTurnDuration, startedFirstTurnDuration.totalDuration());
        assertEquals(firstTurnDuration.minus(elapsedDuration), startedFirstTurnDuration.durationOnFinish(elapsedDuration));
    }
}
