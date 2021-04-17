package com.kor2win.flextimer.timer.turnFlow;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlayersOrderTest {
    private static final Player p1 = new Player("a");
    private static final Player p2 = new Player("b");
    private static final Player p3 = new Player("c");
    private static final Player p4 = new Player("d");
    private static PlayersOrder playersOrder;

    @BeforeAll
    public static void setUp() {
        playersOrder = new PlayersOrder(new Player[]{p1, p2, p3, p4});
    }

    @Test
    public void basicProperties() {
        assertEquals(p1, playersOrder.first());
        assertEquals(p4, playersOrder.last());
        assertEquals(4, playersOrder.size());
    }

    @Test
    public void getByIndex() throws Exception {
        assertEquals(p1, playersOrder.get(0));
        assertEquals(p3, playersOrder.get(2));
    }

    @Test
    public void indexOutOfRange() {
        assertThrows(IndexOutOfBounds.class, () -> playersOrder.get(5));
        assertThrows(IndexOutOfBounds.class, () -> playersOrder.get(-1));
    }

    @Test
    public void before() throws Exception {
        assertEquals(p1, playersOrder.before(p2));
        assertEquals(p4, playersOrder.before(p1));
    }

    @Test
    public void after() throws Exception {
        assertEquals(p3, playersOrder.after(p2));
        assertEquals(p1, playersOrder.after(p4));
    }

    @Test
    public void whenUnknownPlayerSearched_thenThrow() {
        Player unknownPlayer = new Player("some player");
        assertThrows(UnknownPlayer.class, () -> playersOrder.before(unknownPlayer));
        assertThrows(UnknownPlayer.class, () -> playersOrder.after(unknownPlayer));
    }
}
