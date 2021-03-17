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
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class TimerTest {
    private static final long MILLIS_DELTA = 40;
    private static final Duration increment = Duration.ofSeconds(1);

    private static PlayersOrder playersOrder;
    private static Player p1;
    private static Player p2;
    private static Duration p1_initDuration;
    private static Duration p2_initDuration;

    private Timer t;
    private Timer tIncremented;


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
        t = new Timer(
                buildMockTimerTurnFlow(),
                buildMockTurnDurationCalculator(),
                buildTimeBank()
        );

        t.clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());



        tIncremented = new Timer(
                buildMockTimerTurnFlow(),
                buildMockTurnDurationCalculatorWithIncrement(),
                buildTimeBank()
        );
        tIncremented.clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    }

    private static PlayersOrder buildPlayers() {
        var arr = new Player[] {
                new Player("Anton", 0x00FF00),
                new Player("Max", 0xFF0000)
        };

        return new PlayersOrder(arr);
    }

    private TimerTurnFlow buildMockTimerTurnFlow() {
        return new MockTimerTurnFlow(playersOrder, 1);
    }

    private TurnDurationCalculator buildMockTurnDurationCalculator() {
        return (gameTurn, remainingBeforeStart) -> remainingBeforeStart;
    }

    private TurnDurationCalculator buildMockTurnDurationCalculatorWithIncrement() {
        return (gameTurn, remainingBeforeStart) -> remainingBeforeStart.plus(increment);
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

    private void shiftTimerClock(Timer timer, Duration shift) {
        timer.clock = Clock.offset(timer.clock, shift);
    }

    @Test
    public void canStartThenPauseThenResume() throws Exception {
        t.start();
        t.pause();
        t.start();
    }

    @Test
    public void canPassTurn() throws Exception {
        assertEquals(p1, t.currentTurn().player);
        t.passTurn();
        assertEquals(p2, t.currentTurn().player);
    }

    @Test
    public void whenPauseWithoutStart_thenExceptionThrown() {
        assertThrows(PauseWhenPaused.class, t::pause);
    }

    @Test
    public void whenStartAfterStart_thenExceptionThrown() throws Exception {
        t.start();
        assertThrows(StartWhenStarted.class, t::start);
    }

    @Test
    public void remainingDurationDecreasesOverTimeOnStartedTimer() throws Exception {
        Duration total = t.remainingDuration();
        Duration elapsed = Duration.ofSeconds(3);

        t.start();
        shiftTimerClock(t, elapsed);

        assertEquals(total.minus(elapsed), t.remainingDuration());
    }

    @Test
    public void remainingDurationUnaffectedOverTimeOnPausedTimer() throws Exception {
        Duration total = t.remainingDuration();
        Duration elapsedOnPause_1 = Duration.ofSeconds(3);
        Duration elapsedOnPause_2 = Duration.ofSeconds(4);
        Duration active = Duration.ofSeconds(5);

        shiftTimerClock(t, elapsedOnPause_1);
        assertEquals(total, t.remainingDuration());

        t.start();
        shiftTimerClock(t, active);

        t.pause();
        shiftTimerClock(t, elapsedOnPause_2);
        assertEquals(total.minus(active), t.remainingDuration());
    }

    @Test
    public void canGetPlayersRemaining() {
        assertEquals(p2_initDuration, t.playerRemainingDuration(p2));
    }

    @Test
    public void remainingDurationStoredOnTurnPass() throws Exception {
        Duration p1active = Duration.ofSeconds(5);
        Duration p2active = Duration.ofSeconds(4);
        Duration p1beforeStart = t.playerRemainingDuration(p1);
        Duration p2beforeStart = t.playerRemainingDuration(p2);

        t.start();
        shiftTimerClock(t, p1active);
        t.passTurn();
        shiftTimerClock(t, p2active);

        assertEquals(p1beforeStart.minus(p1active), t.playerRemainingDuration(p1));
        assertEquals(p2beforeStart.minus(p2active), t.playerRemainingDuration(p2));
    }

    @Test
    public void whenEnabledStopOnTurnPass_thenTimerStoppedAfterTurnPass() throws Exception {
        t.setPauseOnTurnPass(true);
        t.start();
        t.passTurn();
        assertFalse(t.isStarted());
    }

    @Test
    public void whenDepleteOnZeroRemainingEnabled_thenDepleted() throws Exception {
        tIncremented.setDepleteOnZeroRemaining(true);
        tIncremented.start();
        shiftTimerClock(tIncremented, tIncremented.remainingDuration());
        tIncremented.waitUntilScheduledTurnPass();

        assertTrue(tIncremented.isDepleted());
    }

    @Test
    public void whenTurnPassOnStoppedTimer_thenTimerIsStopped() throws Exception {
        t.passTurn();
        assertFalse(t.isStarted());

        Duration remainingBeforeTimeSkip = t.remainingDuration();
        shiftTimerClock(t, Duration.ofSeconds(2));
        assertEquals(remainingBeforeTimeSkip, t.remainingDuration());
    }

    @Test
    public void whenDurationExceeded_thenTurnPassedToNextPlayer() throws Exception {
        Duration extra = Duration.ofSeconds(1);

        tIncremented.start();
        shiftTimerClock(tIncremented, p1_initDuration.plus(extra));

        tIncremented.waitUntilScheduledTurnPass();

        assertEquals(p2, tIncremented.currentTurn().player);
    }

    @Test
    public void passTurnOnPausedTimer() throws Exception {
        Duration p1beforeStart = t.playerRemainingDuration(p1);
        Duration p1active = Duration.ofSeconds(5);

        t.start();
        shiftTimerClock(t, p1active);
        t.pause();
        t.passTurn();

        assertEquals(p1beforeStart.minus(p1active), t.playerRemainingDuration(p1));
    }

    @Test
    public void whenDurationExceededAndTurnPassedManuallyOnTimerWithIncrement_thenRemainingDurationForPlayersChanged() throws Exception {
        Duration extra = Duration.ofSeconds(3);

        tIncremented.start();
        tIncremented.cancelScheduledTurnPass();

        shiftTimerClock(tIncremented, p1_initDuration.plus(increment).plus(extra));
        Duration p2_expectedRemaining = p2_initDuration.plus(increment).minus(extra);

        tIncremented.passTurn();

        assertEquals(Duration.ZERO, tIncremented.playerRemainingDuration(p1));
        assertEquals(p2_expectedRemaining, tIncremented.playerRemainingDuration(p2));
    }

    @Test
    public void whenDurationExceededAndTurnPassedBySchedulerOnTimerWithIncrement_thenRemainingDurationForPlayersChanged() throws Exception {
        Duration extra = Duration.ofSeconds(2);

        tIncremented.start();
        shiftTimerClock(tIncremented, p1_initDuration.plus(increment).plus(extra));

        tIncremented.waitUntilScheduledTurnPass();

        Duration p1_actualRemaining = tIncremented.playerRemainingDuration(p1);
        Duration p2_actualRemaining = tIncremented.playerRemainingDuration(p2);

        assertEquals(Duration.ZERO, p1_actualRemaining);
        assertDurationEquals(p2_initDuration.plus(increment).minus(extra), p2_actualRemaining);
    }

    @Test
    public void depleteTimer() throws Exception {
        t.start();
        t.cancelScheduledTurnPass();
        shiftTimerClock(t, t.remainingDuration());
        t.passTurn();

        assertTrue(t.isDepleted());
    }
}

class MockTimerTurnFlow extends TimerTurnFlow {
    public MockTimerTurnFlow(PlayersOrder playersOrder, int maxPhases) {
        super(playersOrder, maxPhases);
    }

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

    protected void nextPlayer() {
        try {
            player = playersOrder.after(player);
        } catch (UnknownPlayer ignored) {
        }
    }

    protected TimerTurnFlow newInstance() {
        return new MockTimerTurnFlow(playersOrder, maxPhases);
    }
}