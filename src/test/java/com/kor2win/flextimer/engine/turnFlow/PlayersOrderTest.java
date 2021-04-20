package com.kor2win.flextimer.engine.turnFlow;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlayersOrderTest {
    private static final Player p1 = new Player("a");
    private static final Player p2 = new Player("b");
    private static final Player p3 = new Player("c");
    private static final Player p4 = new Player("d");
    private static PlayersOrder playersOrder;

    private void assertOrdersEquals(PlayersOrder expected, PlayersOrder actual) {
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    @BeforeAll
    public static void setUp() {
        playersOrder = new PlayersOrder(Arrays.asList(p1, p2, p3, p4));
    }

    @Test
    public void basicProperties() {
        assertEquals(p1, playersOrder.first());
        assertEquals(p4, playersOrder.last());
        assertEquals(4, playersOrder.size());
    }

    @Test
    public void getByIndex() {
        assertEquals(p1, playersOrder.get(0));
        assertEquals(p3, playersOrder.get(2));
    }

    @Test
    public void indexOutOfRange() {
        assertThrows(IndexOutOfBounds.class, () -> playersOrder.get(5));
        assertThrows(IndexOutOfBounds.class, () -> playersOrder.get(-1));
    }

    @Test
    public void before() {
        assertEquals(p1, playersOrder.before(p2));
        assertEquals(p4, playersOrder.before(p1));
    }

    @Test
    public void after() {
        assertEquals(p3, playersOrder.after(p2));
        assertEquals(p1, playersOrder.after(p4));
    }

    @Test
    public void whenUnknownPlayerSearched_thenThrow() {
        Player unknownPlayer = new Player("some player");
        assertThrows(UnknownPlayer.class, () -> playersOrder.before(unknownPlayer));
        assertThrows(UnknownPlayer.class, () -> playersOrder.after(unknownPlayer));
    }

    @Test
    public void builder() {
        PlayersOrder o = PlayersOrder
                .builder()
                .add(p1)
                .add(p2)
                .add(p3)
                .add(p4)
                .build();

        assertOrdersEquals(playersOrder, o);
    }

    @Test
    public void whenConstructingByEmptyArray_thenExceptionThrown() {
        assertThrows(InvalidPlayersInitialization.class, () -> new PlayersOrder(Collections.emptyList()));
    }

    @Test
    public void whenConstructingWithDuplicates_thenExceptionThrown() {
        assertThrows(InvalidPlayersInitialization.class, () -> new PlayersOrder(Arrays.asList(p1, p1)));
    }
}
