package com.kor2win.flextimer.turnPassingStrategies;

import com.kor2win.flextimer.timer.turnFlow.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TurnPassingStrategyFactoryTest {
    private static final Map<String, Object> ARGS_EMPTY = new HashMap<>();
    private static final Map<String, Object> ARGS_WITH_SIMULTANEOUS_UNTIL = new HashMap<>();
    private static final TurnPassingStrategyFactory factory = new TurnPassingStrategyFactory();

    @BeforeAll
    public static void setUpArgs() {
        ARGS_WITH_SIMULTANEOUS_UNTIL.put("simultaneous_until", new GameRound(1, 1));
    }

    @Test
    public void availableTypes() {
        Set<String> types = factory.getTypes();

        assertTrue(types.contains(TurnPassingStrategyFactory.TYPE_STRAIGHT));
        assertTrue(types.contains(TurnPassingStrategyFactory.TYPE_ROTATING));
    }

    @Test
    public void straightMake() {
        TurnPassingStrategy s1 = factory.make(TurnPassingStrategyFactory.TYPE_STRAIGHT, ARGS_EMPTY);
        TurnPassingStrategy s2 = factory.make(TurnPassingStrategyFactory.TYPE_STRAIGHT, ARGS_WITH_SIMULTANEOUS_UNTIL);

        assertNotNull(s1);
        assertNotNull(s2);
    }

    @Test
    public void rotatingMake() {
        TurnPassingStrategy s1 = factory.make(TurnPassingStrategyFactory.TYPE_ROTATING, ARGS_EMPTY);
        TurnPassingStrategy s2 = factory.make(TurnPassingStrategyFactory.TYPE_ROTATING, ARGS_WITH_SIMULTANEOUS_UNTIL);

        assertNotNull(s1);
        assertNotNull(s2);
    }

    @Test
    public void whenTypeUnknown_thenExceptionThrown() {
        assertThrows(UnknownTurnPassingStrategyType.class, () -> factory.make("", ARGS_EMPTY));
    }
}
