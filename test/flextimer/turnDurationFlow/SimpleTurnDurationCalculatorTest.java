package flextimer.turnDurationFlow;

import flextimer.timerTurnFlow.util.GameTurn;
import flextimer.turnDurationFlow.strategy.SimpleTurnDurationCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleTurnDurationCalculatorTest {

    private SimpleTurnDurationCalculator flow;

    @BeforeEach
    public void setUp() {
        flow = new SimpleTurnDurationCalculator();
    }

    @Test
    public void startedTurnDurations() {
        GameTurn turn = new GameTurn(1, 1);
        Duration remainingBeforeStart = Duration.ofSeconds(5);
        Duration remainingAfterStart = flow.remainingAfterTurnStart(turn, remainingBeforeStart);

        assertEquals(remainingBeforeStart, remainingAfterStart);
    }
}