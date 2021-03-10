package flextimer;

import flextimer.exception.*;
import flextimer.player.Player;
import flextimer.player.PlayersOrder;
import flextimer.player.UnknownPlayer;
import flextimer.timerTurnFlow.TimerTurnFlow;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimerTest {
    private static PlayersOrder playersOrder;
    private static Player firstPlayer;
    private static Player secondPlayer;

    private Timer timer;

    @BeforeAll
    public static void setUpPlayers() throws Exception {
        playersOrder = buildPlayers();
        firstPlayer = playersOrder.first();
        secondPlayer = playersOrder.after(firstPlayer);
    }

    @BeforeEach
    public void setUpTimer() {
        timer = new Timer(playersOrder, buildMockTurnFlow());
    }

    private static PlayersOrder buildPlayers() {
        var arr = new Player[] {
                new Player("Anton", 0x00FF00),
                new Player("Max", 0xFF0000)
        };

        return new PlayersOrder(arr);
    }

    private TimerTurnFlow buildMockTurnFlow() {
        return new TimerTurnFlow(playersOrder, 1) {
            protected boolean isLastPhase() {
                return true;
            }

            protected boolean isLastPlayer() {
                return playersOrder.last().equals(player);
            }

            protected void nextTurn() {
                turnNumber++;
                phase = 1;
                player = playersOrder.first();
            }

            protected void nextPhase() {
                phase = 1;
                player = playersOrder.first();
            }

            protected void nextPlayer() throws UnknownPlayer {
                player = playersOrder.after(player);
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
    public void canPassTurn() throws Exception {
        assertEquals(firstPlayer, timer.currentTurn().player);
        timer.passTurn();
        assertEquals(secondPlayer, timer.currentTurn().player);
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