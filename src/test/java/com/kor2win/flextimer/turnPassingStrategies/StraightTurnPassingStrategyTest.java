package com.kor2win.flextimer.turnPassingStrategies;

import com.kor2win.flextimer.timer.turnFlow.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class StraightTurnPassingStrategyTest extends RoundCanPlayedSimultaneouslyTestBase {
    @Override
    protected RoundCanPlayedSimultaneously buildStrategy() {
        return new StraightTurnPassingStrategy();
    }

    @Test
    public void wholeTurnPassed() throws Exception {
        TimerTurn t = afterWholeRoundPasses(firstTurn);

        assertEquals(2, t.roundNumber());
        assertEquals(1, t.phase());
        assertEquals(firstPlayer, t.player);
    }
}
