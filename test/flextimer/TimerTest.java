package flextimer;

import flextimer.exception.PassTurnWhenPaused;
import flextimer.exception.PauseWhenPaused;
import flextimer.exception.StartWhenStarted;
import flextimer.exception.InvalidTimerAction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimerTest {
    private static Player[] players;

    private Timer timer;

    @BeforeAll
    public static void setUpPlayers() {
        players = buildPlayers();
    }

    @BeforeEach
    public void setUpTimer() {
        timer = new Timer(players);
    }

    private static Player[] buildPlayers() {
        return new Player[] {
                new Player("Anton", 0x00FF00),
                new Player("Max", 0xFF0000)
        };
    }

    @Test
    void canStartThenPauseThenResume() throws InvalidTimerAction {
        timer.start();
        timer.pause();
        timer.start();
    }

    @Test
    public void whenPassTurnWithoutStart_thenExceptionThrown() {
        assertThrows(PassTurnWhenPaused.class, timer::passTurn);
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