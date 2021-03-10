package flextimer.timerTurnFlow;

import flextimer.player.Player;
import flextimer.player.PlayersOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RotatingTimerTurnFlowTest {
    private static final int MAX_PHASES = 3;

    private static PlayersOrder playersOrder;
    private static Player firstPlayer;
    private static Player secondPlayer;

    private RotatingTimerTurnFlow flow;

    @BeforeAll
    public static void setUpPlayers() throws Exception {
        playersOrder = buildPlayers();
        firstPlayer = playersOrder.first();
        secondPlayer = playersOrder.after(firstPlayer);
    }

    @BeforeEach
    public void setUpFlow() {
        flow = new RotatingTimerTurnFlow(playersOrder, MAX_PHASES);
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

    private void passWholeTurn() throws Exception {
        for (int i = 0; i < MAX_PHASES; i++) {
            passWholePhase();
        }
    }

    private void passWholePhase() throws Exception {
        for (int i = 0; i < playersSize(); i++) {
            flow.passTurn();
        }
    }

    @Test
    public void freshGameState() {
        assertEquals(1, flow.turnNumber());
        assertEquals(1, flow.phase());
        assertEquals(firstPlayer, flow.player());
    }

    @Test
    public void playerPassesTurn() throws Exception {
        flow.passTurn();

        assertEquals(1, flow.turnNumber());
        assertEquals(1, flow.phase());
        assertEquals(secondPlayer, flow.player());
    }

    @Test
    public void wholePhasePassed() throws Exception {
        passWholePhase();

        assertEquals(1, flow.turnNumber());
        assertEquals(2, flow.phase());
        assertEquals(firstPlayer, flow.player());
    }

    @Test
    public void wholeTurnPassed() throws Exception {
        passWholeTurn();

        assertEquals(2, flow.turnNumber());
        assertEquals(1, flow.phase());
        assertEquals(secondPlayer, flow.player());
    }
}
