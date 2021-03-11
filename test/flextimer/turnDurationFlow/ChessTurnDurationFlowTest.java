package flextimer.turnDurationFlow;

import flextimer.timerTurnFlow.util.GameTurn;
import flextimer.turnDurationFlow.exception.NegativeIncrement;
import flextimer.turnDurationFlow.exception.TurnDurationException;
import flextimer.turnDurationFlow.strategy.ChessTurnDurationFlow;
import flextimer.turnDurationFlow.util.ChessTurnDurationIncrements;
import flextimer.turnDurationFlow.util.StartedTurnDuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChessTurnDurationFlowTest {
    private static final Duration remainingDuration = Duration.ofSeconds(10);
    private static final GameTurn t1ph1 = new GameTurn(1, 1);
    private static final GameTurn t1ph2 = new GameTurn(1, 2);
    private static final GameTurn t2ph1 = new GameTurn(2, 1);

    private ChessTurnDurationFlow flow;
    private ChessTurnDurationIncrements increments;

    @BeforeEach
    public void setUp() {
        increments = new ChessTurnDurationIncrements();
        flow = new ChessTurnDurationFlow(increments);
    }

    private StartedTurnDuration startTurn(GameTurn turn) {
        return flow.startTurn(turn, remainingDuration);
    }

    @Test
    public void firstPhaseIncrement() throws TurnDurationException {
        Duration ph1Increment = Duration.ofSeconds(5);
        increments.setPhaseIncrement(1, ph1Increment);

        assertEquals(remainingDuration.plus(ph1Increment), startTurn(t1ph1).totalDuration());
        assertEquals(remainingDuration, startTurn(t1ph2).totalDuration());
    }

    @Test
    public void firstTurnIncrement() throws TurnDurationException {
        Duration t1Increment = Duration.ofSeconds(5);
        increments.setTurnIncrement(1, t1Increment);

        assertEquals(remainingDuration.plus(t1Increment), startTurn(t1ph1).totalDuration());
        assertEquals(remainingDuration, startTurn(t1ph2).totalDuration());
        assertEquals(remainingDuration, startTurn(t2ph1).totalDuration());
    }

    @Test
    public void whenTurnOneAndPhaseOneIncrementsSet_thenApplyOnlyTurnIncrement() throws TurnDurationException {
        Duration t1Increment = Duration.ofSeconds(1);
        Duration ph1Increment = Duration.ofSeconds(2);
        increments.setTurnIncrement(1, t1Increment);
        increments.setPhaseIncrement(1, ph1Increment);

        assertEquals(remainingDuration.plus(t1Increment), startTurn(t1ph1).totalDuration());
        assertEquals(remainingDuration.plus(ph1Increment), startTurn(t2ph1).totalDuration());
    }

    @Test
    public void endTurnDuration() {
        Duration elapsedDuration = Duration.ofSeconds(2);

        assertEquals(remainingDuration.minus(elapsedDuration), startTurn(t1ph1).durationOnFinish(elapsedDuration));
    }

    @Test
    public void whenIncrementDurationNegative_thenThrowException() {
        Duration negativeDuration = Duration.ofSeconds(-1);
        assertThrows(NegativeIncrement.class, () -> increments.setTurnIncrement(1, negativeDuration));
        assertThrows(NegativeIncrement.class, () -> increments.setPhaseIncrement(1, negativeDuration));
    }
}