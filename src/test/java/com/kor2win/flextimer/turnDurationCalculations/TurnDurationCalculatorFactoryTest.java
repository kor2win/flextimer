package com.kor2win.flextimer.turnDurationCalculations;

import com.kor2win.flextimer.timer.timeConstraint.*;
import org.junit.jupiter.api.*;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TurnDurationCalculatorFactoryTest {
    private static final Map<String, Object> ARGS_EMPTY = new HashMap<>();
    private static final TurnDurationCalculatorFactory factory = new TurnDurationCalculatorFactory();

    private Map<String, Object> buildValidChessArgs() {
        Map<String, Object> argsWithIncrements = new HashMap<>();

        Map<Integer, Duration> roundIncrements = new HashMap<>();
        roundIncrements.put(1, Duration.ofSeconds(2));

        Map<Integer, Duration> phaseIncrements = new HashMap<>();
        phaseIncrements.put(2, Duration.ofSeconds(3));
        phaseIncrements.put(3, Duration.ofSeconds(4));

        argsWithIncrements.put("round_increments", roundIncrements);
        argsWithIncrements.put("phase_increments", phaseIncrements);

        return argsWithIncrements;
    }

    private Map<String, Object> buildInvalidChessArgs() {
        Map<String, Object> argsInvalid = new HashMap<>();
        Map<Integer, Duration> roundIncrements = new HashMap<>();
        roundIncrements.put(1, Duration.ofSeconds(-1));
        argsInvalid.put("round_increments", roundIncrements);
        return argsInvalid;
    }

    @Test
    public void availableTypes() {
        Set<String> types = factory.getTypes();
        assertTrue(types.contains(TurnDurationCalculatorFactory.TYPE_SIMPLE));
        assertTrue(types.contains(TurnDurationCalculatorFactory.TYPE_CHESS));
    }

    @Test
    public void simpleMake() {
        TurnDurationCalculator c = factory.make(TurnDurationCalculatorFactory.TYPE_SIMPLE, ARGS_EMPTY);

        assertNotNull(c);
    }

    @Test
    public void chessMake() {
        Map<String, Object> argsWithIncrements = buildValidChessArgs();

        TurnDurationCalculator c1 = factory.make(TurnDurationCalculatorFactory.TYPE_CHESS, ARGS_EMPTY);
        TurnDurationCalculator c2 = factory.make(TurnDurationCalculatorFactory.TYPE_CHESS, argsWithIncrements);

        assertNotNull(c1);
        assertNotNull(c2);
    }

    @Test
    public void whenTypeUnknown_thenExceptionThrown() {
        assertThrows(UnknownCalculatorType.class, () -> factory.make("", ARGS_EMPTY));
    }

    @Test
    public void whenChessMakeWithInvalidArgumentsStructure_thenExceptionThrown() {
        Map<String, Object> argsInvalid = buildInvalidChessArgs();

        assertThrows(InvalidAttributesStructure.class, () -> factory.make(TurnDurationCalculatorFactory.TYPE_CHESS, argsInvalid));
    }
}
