package flextimer;

import flextimer.turnFlow.RotatingTurnFlow;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RotatingTurnFlowTest {
    private static final int MAX_PHASES = 3;

    private static Player[] players;
    private static Player firstPlayer;
    private static Player secondPlayer;

    private RotatingTurnFlow flow;

    @BeforeAll
    public static void setUpPlayers() {
        players = buildPlayers();
        firstPlayer = players[0];
        secondPlayer = players[1];
    }

    @BeforeEach
    public void setUpFlow() {
        flow = new RotatingTurnFlow(players, MAX_PHASES);
    }

    private static Player[] buildPlayers() {
        return new Player[] {
                new Player("Anton", 0x00FF00),
                new Player("Max", 0xFF0000)
        };
    }

    private int currentTurnNumber() {
        return flow.currentTurn().number();
    }

    private int currentTurnPhase() {
        return flow.currentTurn().phase();
    }

    private Player currentPlayer() {
        return flow.currentTurn().player();
    }

    private int playersCount() {
        return players.length;
    }

    private void passWholeTurn() {
        for (int i = 0; i < MAX_PHASES; i++) {
            passWholePhase();
        }
    }

    private void passWholePhase() {
        for (int i = 0; i < playersCount(); i++) {
            flow.passTurn();
        }
    }

    @Test
    public void freshGameState() {
        assertEquals(1, currentTurnNumber());
        assertEquals(1, currentTurnPhase());
        assertEquals(firstPlayer, currentPlayer());
    }

    @Test
    public void playerPassesTurn() {
        flow.passTurn();

        assertEquals(1, currentTurnNumber());
        assertEquals(1, currentTurnPhase());
        assertEquals(secondPlayer, currentPlayer());
    }

    @Test
    public void wholePhasePassed() {
        passWholePhase();

        assertEquals(1, currentTurnNumber());
        assertEquals(2, currentTurnPhase());
        assertEquals(firstPlayer, currentPlayer());
    }

    @Test
    public void wholeTurnPassed() {
        passWholeTurn();

        assertEquals(2, currentTurnNumber());
        assertEquals(1, currentTurnPhase());
        assertEquals(secondPlayer, currentPlayer());
    }
}
