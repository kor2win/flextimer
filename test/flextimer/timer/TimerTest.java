package flextimer.timer;

import flextimer.exception.*;
import flextimer.player.Player;
import flextimer.player.PlayersOrder;
import flextimer.player.exception.UnknownPlayer;
import flextimer.timeBank.TimeBank;
import flextimer.timerTurnFlow.TimerTurnFlow;
import flextimer.turnDurationCalculator.TurnDurationCalculator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class TimerTest {
    private static final long MILLIS_DELTA = 40;

    private static PlayersOrder playersOrder;
    private static Player p1;
    private static Player p2;
    private static Duration p1_initDuration;
    private static Duration p2_initDuration;

    private Timer timer;
    private TimeBank timeBank;

    @BeforeAll
    public static void setUpPlayers() throws Exception {
        playersOrder = buildPlayers();
        p1 = playersOrder.first();
        p2 = playersOrder.after(p1);
        p1_initDuration = Duration.ofSeconds(20);
        p2_initDuration = Duration.ofSeconds(30);
    }

    @BeforeEach
    public void setUpTimer() {
        timeBank = buildTimeBank();
        timer = new Timer(
                buildMockTimerTurnFlow(),
                buildMockTurnDurationCalculator(),
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

    private TurnDurationCalculator buildMockTurnDurationCalculator() {
        return (gameTurn, remainingBeforeStart) -> remainingBeforeStart;
    }

    private TimeBank buildTimeBank() {
        TimeBank timeBank = new TimeBank();

        timeBank.playerTime(p1, p1_initDuration);
        timeBank.playerTime(p2, p2_initDuration);

        return timeBank;
    }

    private void assertDurationEquals(Duration expected, Duration actual) {
        assertTrue(
                Math.abs(expected.minus(actual).toMillis()) < MILLIS_DELTA,
                String.format("expected: %s but was: %s", expected.toString(), actual.toString())
        );
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

    @Test
    public void whenEnabledStopOnTurnPass_thenTimerStoppedAfterTurnPass() throws Exception {
        timer.setPauseOnTurnPass(true);
        timer.start();
        timer.passTurn();
        assertFalse(timer.isStarted());
    }

    @Test
    public void whenTurnPassOnStoppedTimer_thenTimerIsStopped() throws Exception {
        timer.passTurn();
        assertFalse(timer.isStarted());

        Duration remainingBeforeTimeSkip = timer.remainingDuration();
        timer.clock = Clock.offset(timer.clock, Duration.ofSeconds(2));
        assertEquals(remainingBeforeTimeSkip, timer.remainingDuration());
    }

    @Test
    public void whenDurationExceeded_thenTurnPassedToNextPlayer() throws Exception {
        Duration extra = Duration.ofSeconds(1);

        timer.start();
        timer.clock = Clock.offset(timer.clock, p1_initDuration.plus(extra));

        timer.passTimeScheduler.waitUntilScheduled();

        assertEquals(p2, timer.currentTurn().player);
    }

    @Disabled
    @Test
    public void whenDurationExceeded_thenRemainingDurationForPlayersCalculated() throws Exception {
        Duration extra = Duration.ofSeconds(1);

        timer.start();
        timer.clock = Clock.offset(timer.clock, p1_initDuration.plus(extra));
        Duration p1_expectedRemaining = Duration.ZERO;
        Duration p2_expectedRemaining = p2_initDuration.minus(extra);

        timer.passTimeScheduler.waitUntilScheduled();

        Duration p1_actualRemaining = timeBank.remainingTime(p1);
        Duration p2_actualRemaining = timer.remainingDuration();

        assertDurationEquals(p1_expectedRemaining, p1_actualRemaining);
        assertDurationEquals(p2_expectedRemaining, p2_actualRemaining);
    }
}