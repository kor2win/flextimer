package turnDurationCalculations;

import flextimer.turnFlow.*;
import org.junit.jupiter.api.*;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

public class ChessTurnDurationCalculatorTest {
    private static final Duration remainingBeforeStart = Duration.ofSeconds(10);
    private static final GameTurn t1ph1 = new GameTurn(1, 1);
    private static final GameTurn t1ph2 = new GameTurn(1, 2);
    private static final GameTurn t2ph1 = new GameTurn(2, 1);

    private ChessTurnDurationCalculator flow;
    private ChessTurnDurationIncrements increments;

    @BeforeEach
    public void setUp() {
        increments = new ChessTurnDurationIncrements();
        flow = new ChessTurnDurationCalculator(increments);
    }

    @Test
    public void firstPhaseIncrement() throws NegativeIncrement {
        Duration ph1Increment = Duration.ofSeconds(5);
        increments.setPhaseIncrement(1, ph1Increment);

        assertEquals(remainingBeforeStart.plus(ph1Increment), flow.totalTurnDuration(t1ph1, remainingBeforeStart));
        assertEquals(remainingBeforeStart, flow.totalTurnDuration(t1ph2, remainingBeforeStart));
    }

    @Test
    public void firstTurnIncrement() throws NegativeIncrement {
        Duration t1Increment = Duration.ofSeconds(5);
        increments.setTurnIncrement(1, t1Increment);

        assertEquals(remainingBeforeStart.plus(t1Increment), flow.totalTurnDuration(t1ph1, remainingBeforeStart));
        assertEquals(remainingBeforeStart, flow.totalTurnDuration(t1ph2, remainingBeforeStart));
        assertEquals(remainingBeforeStart, flow.totalTurnDuration(t2ph1, remainingBeforeStart));
    }

    @Test
    public void whenTurnOneAndPhaseOneIncrementsSet_thenApplyOnlyTurnIncrement() throws NegativeIncrement {
        Duration t1Increment = Duration.ofSeconds(1);
        Duration ph1Increment = Duration.ofSeconds(2);
        increments.setTurnIncrement(1, t1Increment);
        increments.setPhaseIncrement(1, ph1Increment);

        assertEquals(remainingBeforeStart.plus(t1Increment), flow.totalTurnDuration(t1ph1, remainingBeforeStart));
        assertEquals(remainingBeforeStart.plus(ph1Increment), flow.totalTurnDuration(t2ph1, remainingBeforeStart));
    }

    @Test
    public void whenIncrementDurationNegative_thenThrowException() {
        Duration negativeDuration = Duration.ofSeconds(-1);
        assertThrows(NegativeIncrement.class, () -> increments.setTurnIncrement(1, negativeDuration));
        assertThrows(NegativeIncrement.class, () -> increments.setPhaseIncrement(1, negativeDuration));
    }
}