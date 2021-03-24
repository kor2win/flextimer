package flextimer.timer;

import flextimer.exception.*;
import flextimer.player.*;
import flextimer.timeBank.*;
import flextimer.timeConstraint.*;
import flextimer.turnDurationCalculator.*;
import flextimer.turnFlow.*;
import flextimer.turnFlow.strategy.*;
import flextimer.turnFlow.util.*;
import org.junit.jupiter.api.*;

import java.time.*;

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
        t = buildTimer(new StraightTurnPassingStrategy(), buildTurnDurationCalculator());
        tIncremented = buildTimer(new StraightTurnPassingStrategy(), buildTurnDurationCalculatorWithIncrement());
    }

    private Timer buildTimer(TurnPassingStrategy turnPassingStrategy, TurnDurationCalculator turnDurationCalculator) {
        TurnFlow turnFlow = new TurnFlow(playersOrder, turnPassingStrategy, 1);
        TimeConstraint timeConstraint = new TimeConstraint(turnDurationCalculator, buildTimeBank(), turnFlow);

        Timer t = new Timer(turnFlow, timeConstraint);
        t.clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

        return t;
    }

    private static PlayersOrder buildPlayers() {
        var arr = new Player[] {
                new Player("Anton", 0x00FF00),
                new Player("Max", 0xFF0000)
        };

        return new PlayersOrder(arr);
    }

    private TurnDurationCalculator buildTurnDurationCalculator() {
        return (gameTurn, remainingBeforeStart) -> remainingBeforeStart;
    }

    private TurnDurationCalculator buildTurnDurationCalculatorWithIncrement() {
        return (gameTurn, remainingBeforeStart) -> remainingBeforeStart.plus(increment);
    }

    private TimeBank buildTimeBank() {
        PlayersTimeBank timeBank = new PlayersTimeBank();
        TimerTurn t1p1 = new TimerTurn(new GameTurn(1, 1), p1);
        TimerTurn t1p2 = new TimerTurn(new GameTurn(1, 1), p2);
        timeBank.saveRemainingDuration(t1p1, p1_initDuration);
        timeBank.saveRemainingDuration(t1p2, p2_initDuration);
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

    private void passBackToCurrentPlayer(Timer t) {
        t.passTurn();
        t.passTurn();
    }

    @Test
    public void canStartThenPauseThenResume() throws Exception {
        t.start();
        t.pause();
        t.start();
    }

    @Test
    public void canPassTurn() {
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
        Duration total = t.currentRemainingDuration();
        Duration elapsed = Duration.ofSeconds(3);

        t.start();
        shiftTimerClock(t, elapsed);

        assertEquals(total.minus(elapsed), t.currentRemainingDuration());
    }

    @Test
    public void remainingDurationUnaffectedOverTimeOnPausedTimer() throws Exception {
        Duration total = t.currentRemainingDuration();
        Duration elapsedOnPause_1 = Duration.ofSeconds(3);
        Duration elapsedOnPause_2 = Duration.ofSeconds(4);
        Duration active = Duration.ofSeconds(5);

        shiftTimerClock(t, elapsedOnPause_1);
        assertEquals(total, t.currentRemainingDuration());

        t.start();
        shiftTimerClock(t, active);

        t.pause();
        shiftTimerClock(t, elapsedOnPause_2);
        assertEquals(total.minus(active), t.currentRemainingDuration());
    }

    @Test
    public void remainingDurationStoredOnTurnPass() throws Exception {
        Duration p1active = Duration.ofSeconds(5);
        Duration p2active = Duration.ofSeconds(4);

        t.start();
        shiftTimerClock(t, p1active);
        assertEquals(p1_initDuration.minus(p1active), t.currentRemainingDuration());

        t.passTurn();
        shiftTimerClock(t, p2active);
        assertEquals(p2_initDuration.minus(p2active), t.currentRemainingDuration());
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
        shiftTimerClock(tIncremented, tIncremented.currentRemainingDuration());
        tIncremented.waitUntilScheduledTurnPass();

        assertTrue(tIncremented.isDepleted());
    }

    @Test
    public void whenTurnPassOnStoppedTimer_thenTimerIsStopped() {
        t.passTurn();
        assertFalse(t.isStarted());

        Duration remainingBeforeTimeSkip = t.currentRemainingDuration();
        shiftTimerClock(t, Duration.ofSeconds(2));
        assertEquals(remainingBeforeTimeSkip, t.currentRemainingDuration());
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
        Duration p1beforeStart = t.currentRemainingDuration();
        Duration p1active = Duration.ofSeconds(5);

        t.start();
        shiftTimerClock(t, p1active);
        t.pause();

        passBackToCurrentPlayer(t);

        assertEquals(p1, t.currentPlayer());
        assertEquals(p1beforeStart.minus(p1active), t.currentRemainingDuration());
    }

    @Test
    public void whenDurationExceededAndTurnPassedManuallyOnTimerWithIncrement_thenRemainingDurationForPlayersChanged() throws Exception {
        Duration extra = Duration.ofSeconds(3);

        tIncremented.start();
        tIncremented.cancelScheduledTurnPass();

        shiftTimerClock(tIncremented, p1_initDuration.plus(increment).plus(extra));
        Duration p2_expectedRemaining = p2_initDuration.plus(increment).minus(extra);

        tIncremented.passTurn();
        assertEquals(p2_expectedRemaining, tIncremented.currentRemainingDuration());
        tIncremented.passTurn();
        assertEquals(increment, tIncremented.currentRemainingDuration());
    }

    @Test
    public void whenDurationExceededAndTurnPassedBySchedulerOnTimerWithIncrement_thenRemainingDurationForPlayersChanged() throws Exception {
        Duration extra = Duration.ofSeconds(2);

        tIncremented.start();
        shiftTimerClock(tIncremented, p1_initDuration.plus(increment).plus(extra));

        tIncremented.waitUntilScheduledTurnPass();
        tIncremented.pause();

        assertEquals(p2, tIncremented.currentPlayer());
        assertDurationEquals(p2_initDuration.plus(increment).minus(extra), tIncremented.currentRemainingDuration());
        tIncremented.passTurn();
        assertEquals(p1, tIncremented.currentPlayer());
        assertEquals(increment, tIncremented.currentRemainingDuration());
    }

    @Test
    public void depleteTimer() throws Exception {
        t.start();
        t.cancelScheduledTurnPass();
        shiftTimerClock(t, t.currentRemainingDuration());
        t.passTurn();

        assertTrue(t.isDepleted());
    }
}