package com.kor2win.flextimer.timeBanking;

import com.kor2win.flextimer.timer.timeConstraint.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TimeBankFactoryTest {
    private static final Map<String, Object> ARGS_EMPTY = new HashMap<>();
    private static final TimeBankFactory factory = new TimeBankFactory();

    @Test
    public void availableTypes() {
        assertTrue(factory.getTypes().contains(TimeBankFactory.TYPE_PLAYERS));
    }

    @Test
    public void playersMake() {
        TimeBank timeBank = factory.make(TimeBankFactory.TYPE_PLAYERS, ARGS_EMPTY);

        assertNotNull(timeBank);
    }

    @Test
    public void whenTypeUnknown_thenExceptionThrown() {
        assertThrows(UnknownBankType.class, () -> factory.make("", ARGS_EMPTY));
    }
}
