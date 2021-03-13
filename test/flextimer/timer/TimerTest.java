package flextimer.timer;

import flextimer.exception.*;
import flextimer.player.Player;
import flextimer.player.PlayersOrder;
import flextimer.player.UnknownPlayer;
import flextimer.timeBank.TimeBank;
import flextimer.timerTurnFlow.TimerTurnFlow;
import flextimer.timerTurnFlow.util.GameTurn;
import flextimer.turnDurationFlow.TurnDurationFlow;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class TimerTest {
    private static PlayersOrder playersOrder;
    private static Player p1;
    private static Player p2;

    private Timer timer;
    private TimeBank timeBank;

    @BeforeAll
    public static void setUpPlayers() throws Exception {
        playersOrder = buildPlayers();
        p1 = playersOrder.first();
        p2 = playersOrder.after(p1);
    }

    @BeforeEach
    public void setUpTimer() {
        timeBank = buildTimeBank();
        timer = new Timer(
                buildMockTimerTurnFlow(),
                buildMockTurnDurationFlow(),
                timeBank
        );

        timer.clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    }

    private static PlayersOrder buildPlayers() {
        var arr = new Player[] {
                new Player("Anton", 0x00FF00),
                new Player("Max", 0xFF0000)
        };

        return new PlayersOrder(arr);
    }

    private TimerTurnFlow buildMockTimerTurnFlow() {
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

    private TurnDurationFlow buildMockTurnDurationFlow() {
        return (gameTurn, remainingBeforeStart) -> remainingBeforeStart;
    }

    private TimeBank buildTimeBank() {
        TimeBank timeBank = new TimeBank();

        timeBank.playerTime(p1, Duration.ofSeconds(20));
        timeBank.playerTime(p2, Duration.ofSeconds(30));

        return timeBank;
    }

    @Test
    public void canStartThenPauseThenResume() throws Exception {
        timer.start();
        timer.pause();
        timer.start();
    }

    @Test
    public void canPassTurn() throws Exception {
        assertEquals(p1, timer.currentTurn().player);
        timer.passTurn();
        assertEquals(p2, timer.currentTurn().player);
    }

    @Test
    public void whenPauseWithoutStart_thenExceptionThrown() {
        assertThrows(PauseWhenPaused.class, timer::pause);
    }

    @Test
    public void whenStartAfterStart_thenExceptionThrown() throws Exception {
        timer.start();
        assertThrows(StartWhenStarted.class, timer::start);
    }

    @Test
    public void canGetCurrentTurn() {
        assertNotEquals(null, timer.currentTurn());
    }

    @Test
    public void remainingDurationDecreasesOverTimeOnStartedTimer() throws Exception {
        Duration total = timer.remainingDuration();
        Duration elapsed = Duration.ofSeconds(3);

        timer.start();
        timer.clock = Clock.offset(timer.clock, elapsed);

        assertEquals(total.minus(elapsed), timer.remainingDuration());
    }

    @Test
    public void remainingDurationUnaffectedOverTimeOnPausedTimer() throws Exception {
        Duration total = timer.remainingDuration();
        Duration elapsedOnPause_1 = Duration.ofSeconds(3);
        Duration elapsedOnPause_2 = Duration.ofSeconds(4);
        Duration active = Duration.ofSeconds(5);

        timer.clock = Clock.offset(timer.clock, elapsedOnPause_1);
        assertEquals(total, timer.remainingDuration());

        timer.start();
        timer.clock = Clock.offset(timer.clock, active);

        timer.pause();
        timer.clock = Clock.offset(timer.clock, elapsedOnPause_2);
        assertEquals(total.minus(active), timer.remainingDuration());
    }

    @Test
    public void remainingDurationStoredOnTurnPass() throws Exception {
        Duration p1active = Duration.ofSeconds(5);
        Duration p2active = Duration.ofSeconds(4);
        Duration p1beforeStart = timeBank.remainingTime(p1);
        Duration p2beforeStart = timeBank.remainingTime(p2);

        timer.start();
        timer.clock = Clock.offset(timer.clock, p1active);
        timer.passTurn();
        timer.clock = Clock.offset(timer.clock, p2active);

        assertEquals(p1beforeStart.minus(p1active), timeBank.remainingTime(p1));
        assertEquals(p2beforeStart.minus(p2active), timer.remainingDuration());
    }
}