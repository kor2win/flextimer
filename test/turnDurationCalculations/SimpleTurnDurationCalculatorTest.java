package turnDurationCalculations;

import flextimer.turnFlow.*;
import org.junit.jupiter.api.*;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;


public class SimpleTurnDurationCalculatorTest {

    private SimpleTurnDurationCalculator flow;

    @BeforeEach
    public void setUp() {
        flow = new SimpleTurnDurationCalculator();
    }

    @Test
    public void startedTurnDurations() {
        GameRound turn = new GameRound(1, 1);
        Duration remainingBeforeStart = Duration.ofSeconds(5);
        Duration remainingAfterStart = flow.totalTurnDuration(turn, remainingBeforeStart);

        assertEquals(remainingBeforeStart, remainingAfterStart);
    }
}
