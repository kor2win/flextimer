package flextimer.turnFlow;

import flextimer.player.*;
import flextimer.player.exception.UnknownPlayer;
import flextimer.turnFlow.strategy.*;
import flextimer.turnFlow.util.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TurnFlowTest {
    private static final int PHASES_COUNT = 3;
    private static PlayersOrder playersOrder;
    private static TurnFlow turnFlow;
    private static TurnPassingStrategy strategy;

    @BeforeAll
    public static void setUpPlayers() {
        playersOrder = buildPlayers();

        strategy = buildStrategy();
        turnFlow = new TurnFlow(
                playersOrder,
                strategy,
                PHASES_COUNT
        );
    }

    private static PlayersOrder buildPlayers() {
        Player[] arr = {
                new Player("Anton", 0x00FF00),
                new Player("Max", 0xFF0000)
        };

        return new PlayersOrder(arr);
    }

    private static TurnPassingStrategy buildStrategy() {
        return new StraightTurnPassingStrategy();
    }

    private TimerTurn turnAfterFirst() throws UnknownPlayer {
        return strategy.turnAfter(
                playersOrder,
                strategy.firstTurn(playersOrder),
                PHASES_COUNT
        );
    }

    private TimerTurn turnAfterSecond() throws UnknownPlayer {
        return strategy.turnAfter(
                playersOrder,
                turnAfterFirst(),
                PHASES_COUNT
        );
    }

    @Test
    public void firstTurn() {
        TimerTurn t1 = turnFlow.firstTurn();

        TimerTurn expected = strategy.firstTurn(playersOrder);

        assertTrue(expected.equals(t1));
    }

    @Test
    public void nextTurn() throws Exception {
        TimerTurn t1 = turnFlow.firstTurn();
        TimerTurn t2 = turnFlow.nextTurn(t1);

        TimerTurn expected = turnAfterFirst();

        assertTrue(expected.equals(t2));
    }

    @Test
    public void nextTurnOfSamePlayer() throws Exception {
        TimerTurn t1 = turnFlow.firstTurn();
        TimerTurn t3 = turnFlow.nextTurnOfSamePlayer(t1);

        TimerTurn expected = turnAfterSecond();

        assertTrue(t1.player.equals(t3.player));
        assertTrue(expected.equals(t3));
    }
}
