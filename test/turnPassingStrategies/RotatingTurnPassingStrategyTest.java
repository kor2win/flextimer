package turnPassingStrategies;

import flextimer.turnFlow.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RotatingTurnPassingStrategyTest extends RoundCanPlayedSimultaneouslyTestBase {
    @Override
    protected RoundCanPlayedSimultaneously buildStrategy() {
        return new RotatingTurnPassingStrategy();
    }

    @Test
    public void wholeTurnPassed() throws Exception {
        TimerTurn t = afterWholeRoundPasses(firstTurn);

        assertEquals(2, t.roundNumber());
        assertEquals(1, t.phase());
        assertEquals(secondPlayer, t.player);
    }
}
