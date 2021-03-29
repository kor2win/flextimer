package turnPassingStrategies;

import flextimer.turnFlow.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public abstract class RoundCanPlayedSimultaneouslyTestBase {
    protected static final int PHASES_COUNT = 3;

    protected static PlayersOrder playersOrder;
    protected static Player firstPlayer;
    protected static Player secondPlayer;

    protected RoundCanPlayedSimultaneously strategy;
    protected TimerTurn firstTurn;

    @BeforeAll
    public static void setUpPlayers() throws Exception {
        playersOrder = buildPlayers();
        firstPlayer = playersOrder.first();
        secondPlayer = playersOrder.after(firstPlayer);
    }

    @BeforeEach
    public void setUpStrategy() {
        strategy = buildStrategy();
        firstTurn = strategy.firstTurn(playersOrder);
    }

    protected abstract RoundCanPlayedSimultaneously buildStrategy();

    protected static PlayersOrder buildPlayers() {
        Player[] arr = {
                new Player("Anton", 0x00FF00),
                new Player("Max", 0xFF0000),
                new Player("Lisa", 0x94E2FF)
        };

        return new PlayersOrder(arr);
    }

    protected TimerTurn afterWholeRoundPasses(TimerTurn t) throws UnknownPlayer {
        for (int i = 0; i < PHASES_COUNT - 1; i++) {
            t = afterWholePhasePasses(t);
        }

        return t;
    }

    protected TimerTurn afterWholePhasePasses(TimerTurn t) throws UnknownPlayer {
        for (int i = 0; i < playersCount(); i++) {
            t = strategy.nextTurn(playersOrder, t, PHASES_COUNT);
        }

        return t;
    }

    protected int playersCount() {
        return playersOrder.size();
    }

    protected void assertRound(SimultaneousTurns turns, TimerTurn firstInRound) throws UnknownPlayer {
        assertEquals(playersOrder.size(), turns.size());
        assertEquals(firstInRound, turns.get(0));

        TimerTurn t = firstInRound;
        for (int i = 1; i < turns.size(); i++) {
            t = strategy.nextTurn(playersOrder, t, PHASES_COUNT);
            assertEquals(t, turns.get(i));
        }
    }

    @Test
    public void firstTurn() {
        assertEquals(1, firstTurn.roundNumber());
        assertEquals(1, firstTurn.phase());
        assertEquals(firstPlayer, firstTurn.player);
    }

    @Test
    public void turnAfter() throws Exception {
        TimerTurn t = strategy.nextTurn(playersOrder, firstTurn, PHASES_COUNT);
        assertEquals(1, t.roundNumber());
        assertEquals(1, t.phase());
        assertEquals(secondPlayer, t.player);
    }

    @Test
    public void wholePhasePassed() throws Exception {
        TimerTurn t = afterWholePhasePasses(firstTurn);

        assertEquals(1, t.roundNumber());
        assertEquals(2, t.phase());
        assertEquals(firstPlayer, t.player);
    }

    @Test
    public void firstRound() throws Exception {
        SimultaneousTurns turns = strategy.firstSimultaneousTurns(playersOrder, PHASES_COUNT);

        assertRound(turns, firstTurn);
    }

    @Test
    public void nextRound() throws Exception {
        SimultaneousTurns turns = strategy.firstSimultaneousTurns(playersOrder, PHASES_COUNT);
        TimerTurn after = turns.lastTurn();
        turns = strategy.simultaneousTurnsAfterTurn(playersOrder, after, PHASES_COUNT);

        TimerTurn firstInRound = strategy.nextTurn(playersOrder, after, PHASES_COUNT);
        assertRound(turns, firstInRound);
    }
}