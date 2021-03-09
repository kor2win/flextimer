package flextimer;

import flextimer.exception.*;
import flextimer.turnFlow.TurnFlow;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimerTest {
    private static Player[] players;
    private static Player firstPlayer;
    private static Player secondPlayer;

    private Timer timer;

    @BeforeAll
    public static void setUpPlayers() {
        players = buildPlayers();
        firstPlayer = players[0];
        secondPlayer = players[1];
    }

    @BeforeEach
    public void setUpTimer() {
        timer = new Timer(buildMockTurnFlow());
    }

    private static Player[] buildPlayers() {
        return new Player[] {
                new Player("Anton", 0x00FF00),
                new Player("Max", 0xFF0000)
        };
    }

    private TurnFlow buildMockTurnFlow() {
        return new TurnFlow(players, 1) {
            protected boolean isLastPhase() {
                return true;
            }

            protected boolean isLastPlayer() {
                return playerIndex + 1 == players.length;
            }

            protected void nextTurn() {
                turnNumber++;
            }

            protected void nextPhase() {
                phase = 1;
                playerIndex++;
            }

            protected void nextPlayer() {
                playerIndex++;
            }
        };
    }

    @Test
    public void canStartThenPauseThenResume() throws InvalidTimerAction {
        timer.start();
        timer.pause();
        timer.start();
    }

    @Test
    public void canPassTurn() {
        assertEquals(firstPlayer, timer.currentTurn().player());
        timer.passTurn();
        assertEquals(secondPlayer, timer.currentTurn().player());
    }

    @Test
    public void whenPauseWithoutStart_thenExceptionThrown() {
        assertThrows(PauseWhenPaused.class, timer::pause);
    }

    @Test
    public void whenStartAfterStart_thenExceptionThrown() throws InvalidTimerAction {
        timer.start();
        assertThrows(StartWhenStarted.class, timer::start);
    }

    @Test
    public void canGetCurrentTurn() {
        assertNotEquals(null, timer.currentTurn());
    }
}