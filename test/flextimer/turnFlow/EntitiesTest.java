package flextimer.turnFlow;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class EntitiesTest {
    @Test
    public void hashingProperties() {
        Player a1 = new Player("name");
        Player a2 = new Player("name");
        Player aX = new Player("some");

        GameRound b1 = GameRound.FIRST;
        GameRound b2 = GameRound.FIRST;
        GameRound bX = new GameRound(1, 2);

        TimerTurn c1 = new TimerTurn(b1, a1);
        TimerTurn c2 = new TimerTurn(b2, a2);
        TimerTurn cX = new TimerTurn(bX, aX);

        assertTrue(a1.equals(a2));
        assertTrue(b1.equals(b2));
        assertTrue(c1.equals(c2));
        assertEquals(a1, a2);
        assertEquals(b1, b2);
        assertEquals(c1, c2);

        assertFalse(a1.equals(aX));
        assertFalse(b1.equals(bX));
        assertFalse(c1.equals(cX));
        assertNotEquals(a1, aX);
        assertNotEquals(b1, bX);
        assertNotEquals(c1, cX);

        assertEquals(a1.hashCode(), a2.hashCode());
        assertEquals(b1.hashCode(), b2.hashCode());
        assertEquals(c1.hashCode(), c2.hashCode());
    }
}
