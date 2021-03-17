package flextimer.timerTurnFlow;

import flextimer.player.Player;
import flextimer.player.PlayersOrder;
import flextimer.timerTurnFlow.strategy.StraightTimerTurnFlow;
import flextimer.timerTurnFlow.util.GameTurn;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StraightTimerTurnFlowTest {
    private static final int MAX_PHASES = 3;

    private static PlayersOrder playersOrder;
    private static Player firstPlayer;
    private static Player secondPlayer;

    private StraightTimerTurnFlow flow;

    @BeforeAll
    public static void setUpPlayers() throws Exception {
        playersOrder = buildPlayers();
        firstPlayer = playersOrder.first();
        secondPlayer = playersOrder.after(firstPlayer);
    }

    @BeforeEach
    public void setUpFlow() {
        flow = new StraightTimerTurnFlow(playersOrder, MAX_PHASES);
    }

    private static PlayersOrder buildPlayers() {
        Player[] arr = {
                new Player("Anton", 0x00FF00),
                new Player("Max", 0xFF0000)
        };

        return new PlayersOrder(arr);
    }

    private int playersSize() {
        return playersOrder.size();
    }

    private void passWholeTurn() {
        for (int i = 0; i < MAX_PHASES; i++) {
            passWholePhase();
        }
    }

    private void passWholePhase() {
        for (int i = 0; i < playersSize(); i++) {
            flow.switchToNextTurn();
        }
    }

    @Test
    public void freshGameState() {
        assertEquals(1, flow.turnNumber());
        assertEquals(1, flow.phase());
        assertEquals(firstPlayer, flow.player());
    }

    @Test
    public void playerPassesTurn() {
        flow.switchToNextTurn();

        assertEquals(1, flow.turnNumber());
        assertEquals(1, flow.phase());
        assertEquals(secondPlayer, flow.player());
    }

    @Test
    public void wholeTurnPassed() {
        passWholeTurn();

        assertEquals(2, flow.turnNumber());
        assertEquals(1, flow.phase());
        assertEquals(firstPlayer, flow.player());
    }

    @Test
    public void wholePhasePassed() {
        passWholePhase();

        assertEquals(1, flow.turnNumber());
        assertEquals(2, flow.phase());
        assertEquals(firstPlayer, flow.player());
    }

    @Test
    public void futureTurnAccess() {
        GameTurn t10ph1 = new GameTurn(10, 1);
        GameTurn t10ph2 = new GameTurn(10, 2);
        GameTurn t10ph3 = new GameTurn(10, 3);
        GameTurn t11ph1 = new GameTurn(11, 1);

        GameTurn after_t10ph1 = flow.nextTurnForPlayer(firstPlayer, t10ph1);
        GameTurn after_t10ph3 = flow.nextTurnForPlayer(firstPlayer, t10ph3);
        assertTrue(t10ph2.equals(after_t10ph1));
        assertTrue(t11ph1.equals(after_t10ph3));
    }
}
