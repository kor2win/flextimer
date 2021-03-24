package turnPassingStrategies;

import flextimer.turnFlow.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RotatingTurnPassingStrategyTest {
    private static final int PHASES_COUNT = 3;

    private static RotatingTurnPassingStrategy strategy;
    private static PlayersOrder playersOrder;
    private static Player firstPlayer;
    private static Player secondPlayer;
    private static TimerTurn firstTurn;

    @BeforeAll
    public static void setUpPlayers() throws Exception {
        strategy = new RotatingTurnPassingStrategy();
        playersOrder = buildPlayers();
        firstPlayer = playersOrder.first();
        secondPlayer = playersOrder.after(firstPlayer);

        firstTurn = strategy.firstTurn(playersOrder);
    }

    private static PlayersOrder buildPlayers() {
        Player[] arr = {
                new Player("Anton", 0x00FF00),
                new Player("Max", 0xFF0000)
        };

        return new PlayersOrder(arr);
    }

    private TimerTurn afterWholeTurnPasses(TimerTurn t) throws UnknownPlayer {
        for (int i = 0; i < PHASES_COUNT - 1; i++) {
            t = afterWholePhasePasses(t);
        }

        return t;
    }

    private TimerTurn afterWholePhasePasses(TimerTurn t) throws UnknownPlayer {
        for (int i = 0; i < playersCount(); i++) {
            t = strategy.turnAfter(playersOrder, t, PHASES_COUNT);
        }

        return t;
    }

    private int playersCount() {
        return playersOrder.size();
    }

    @Test
    public void firstTurn() {
        assertEquals(1, firstTurn.turnNumber());
        assertEquals(1, firstTurn.phase());
        assertEquals(firstPlayer, firstTurn.player);
    }

    @Test
    public void turnAfter() throws Exception {
        TimerTurn t = strategy.turnAfter(playersOrder, firstTurn, PHASES_COUNT);
        assertEquals(1, t.turnNumber());
        assertEquals(1, t.phase());
        assertEquals(secondPlayer, t.player);
    }

    @Test
    public void wholeTurnPassed() throws Exception {
        TimerTurn t = afterWholeTurnPasses(firstTurn);

        assertEquals(2, t.turnNumber());
        assertEquals(1, t.phase());
        assertEquals(secondPlayer, t.player);
    }

    @Test
    public void wholePhasePassed() throws Exception {
        TimerTurn t = afterWholePhasePasses(firstTurn);

        assertEquals(1, t.turnNumber());
        assertEquals(2, t.phase());
        assertEquals(firstPlayer, t.player);
    }
}
