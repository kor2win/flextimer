package com.kor2win.flextimer.timer.ui;

import com.kor2win.flextimer.timer.turnFlow.*;
import com.kor2win.flextimer.timer.timeConstraint.*;
import com.kor2win.flextimer.turnPassingStrategies.*;
import com.kor2win.flextimer.timeBanking.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimerTest {
    private static final Duration increment = Duration.ofSeconds(1);
    private static final int PHASES_COUNT = 1;

    private static TurnPassingStrategy turnPassingStrategy;
    private static PlayersOrder playersOrder;
    private static Player p1;
    private static Player p2;
    private static Duration p1_initDuration;
    private static Duration p2_initDuration;

    private Timer t;
    private Timer tIncremented;


    @Mock private TimerConfig timerConfig;
    @Mock private TimeConstraintConfig timeConstraintConfig;
    @Mock private TurnFlowConfig turnFlowConfig;

    @BeforeAll
    public static void setUpPlayers() {
        turnPassingStrategy = new StraightTurnPassingStrategy(GameRound.FIRST);
        buildPlayers();
        p1_initDuration = Duration.ofSeconds(20);
        p2_initDuration = Duration.ofSeconds(30);
    }

    @BeforeEach
    public void setUpTimer() {
        buildConfig();

        t = buildTimer(buildTurnDurationCalculator(), buildTimeBank());
        t.launch();

        tIncremented = buildTimer(buildTurnDurationCalculatorWithIncrement(), buildTimeBank());
        tIncremented.launch();
    }

    private Timer buildTimer(TurnDurationCalculator turnDurationCalculator, TimeBank timeBank) {
        TurnFlow turnFlow = new TurnFlow(turnPassingStrategy, turnFlowConfig);
        TimeConstraint timeConstraint = new TimeConstraint(turnDurationCalculator, timeBank, turnFlow, timeConstraintConfig);

        Timer t = new Timer(turnFlow, timeConstraint, timerConfig);
        t.setClock(Clock.fixed(Instant.now(), ZoneId.systemDefault()));

        return t;
    }

    private Timer buildTimerWithEmptyBank() {
        Timer timer = buildTimer(buildTurnDurationCalculator(), new PlayersTimeBank());
        timer.launch();

        return timer;
    }

    private static void buildPlayers() {
        p1 = new Player("Anton");
        p2 = new Player("Max");

        playersOrder = new PlayersOrder(new Player[] {p1, p2});
    }

    private TurnDurationCalculator buildTurnDurationCalculator() {
        return (gameTurn, remainingBeforeStart) -> remainingBeforeStart;
    }

    private TurnDurationCalculator buildTurnDurationCalculatorWithIncrement() {
        return (gameTurn, remainingBeforeStart) -> remainingBeforeStart.plus(increment);
    }

    private TimeBank buildTimeBank() {
        PlayersTimeBank timeBank = new PlayersTimeBank();
        TimerTurn t1p1 = new TimerTurn(GameRound.FIRST, p1);
        TimerTurn t1p2 = new TimerTurn(GameRound.FIRST, p2);
        timeBank.saveRemaining(t1p1, p1_initDuration);
        timeBank.saveRemaining(t1p2, p2_initDuration);
        return timeBank;
    }

    private void shiftTimerClock(Timer timer, Duration shift) {
        timer.shiftClock(shift);
    }

    private void passBackToCurrentPlayer(Timer t) {
        t.passSimultaneousTurns();
        t.passSimultaneousTurns();
    }

    private void depleteTimer(Timer timer) {
        if (timer.isPaused()) {
            timer.resume();
        }

        ConstrainedTimerTurn turn = getFirstTurn(timer);
        shiftTimerClock(timer, turn.remaining());
        timer.syncWithClock();
    }

    private ConstrainedTimerTurn getFirstTurn(Timer t) {
        return t.getCurrentSimultaneousTurns().get(0);
    }

    private void buildConfig() {
        lenient().when(timerConfig.pauseOnTurnPass()).thenReturn(false);
        lenient().when(timeConstraintConfig.depleteOnZeroRemaining()).thenReturn(false);
        lenient().when(turnFlowConfig.phasesCount()).thenReturn(PHASES_COUNT);
        lenient().when(turnFlowConfig.playersOrder()).thenReturn(playersOrder);
    }

    @Test
    public void whenOperatingNotStartedTimer_thenExceptionThrown() {
        Timer t = buildTimer(buildTurnDurationCalculator(), buildTimeBank());

        assertThrows(TimerNotLaunched.class, t::resume);
        assertThrows(TimerNotLaunched.class, t::pause);
        assertThrows(TimerNotLaunched.class, t::passSimultaneousTurns);
        assertThrows(TimerNotLaunched.class, t::syncWithClock);
    }

    @Test
    public void whenStartStarted_thenExceptionThrown() {
        assertThrows(TimerAlreadyLaunched.class, t::launch);
    }

    @Test
    public void canPauseThenResumeThenPause() {
        t.pause();
        t.resume();
        t.pause();
    }

    @Test
    public void canPassTurn() {
        assertEquals(p1, getFirstTurn(t).player());
        t.passSimultaneousTurns();
        assertEquals(p2, getFirstTurn(t).player());
    }

    @Test
    public void remainingDurationDecreasesOverTimeOnStartedTurn() {
        ConstrainedTimerTurn turn = getFirstTurn(t);
        Duration total = turn.remaining();
        Duration elapsed = Duration.ofSeconds(3);

        shiftTimerClock(t, elapsed);

        assertEquals(total.minus(elapsed), turn.remaining());
    }

    @Test
    public void whenBankedDurationForOnePlayerIsZero_thenIsDepleted() {
        depleteTimer(t);

        assertTrue(t.isDepleted());
    }

    @Test
    public void whenPassOnPaused_thenExceptionThrown() {
        t.pause();

        assertThrows(PassTurnOnPause.class, t::passSimultaneousTurns);
    }

    @Test
    public void whenPauseOnPaused_thenExceptionThrown() {
        t.pause();

        assertThrows(TimerPaused.class, t::pause);
    }

    @Test
    public void whenResumeOnResumed_thenExceptionThrown() {
        assertThrows(TimerNotPaused.class, t::resume);
    }

    @Test
    public void whenTryingOperateDepletedTimer_thenExceptionThrown() {
        depleteTimer(t);

        assertThrows(TimerDepleted.class, t::pause);
        assertThrows(TimerDepleted.class, t::resume);
        assertThrows(TimerDepleted.class, t::passSimultaneousTurns);
    }

    @Test
    public void remainingDurationUnaffectedOverTimeOnPausedTimer() {
        ConstrainedTimerTurn turn = getFirstTurn(t);

        Duration total = turn.remaining();
        Duration elapsedOnPause = Duration.ofSeconds(4);

        t.pause();
        shiftTimerClock(t, elapsedOnPause);

        assertEquals(total, turn.remaining());
    }

    @Test
    public void remainingDurationUnaffectedOverTimeOnPausedTurn() {
        ConstrainedTimerTurn turn = getFirstTurn(t);

        Duration total = turn.remaining();
        Duration elapsedOnPause = Duration.ofSeconds(3);

        turn.pause();
        shiftTimerClock(t, elapsedOnPause);

        assertEquals(total, turn.remaining());
    }

    @Test
    public void remainingDurationStoredOnTurnPass() {
        Duration p1active = Duration.ofSeconds(5);
        Duration p2active = Duration.ofSeconds(4);

        ConstrainedTimerTurn t1 = getFirstTurn(t);

        shiftTimerClock(t, p1active);
        assertEquals(p1_initDuration.minus(p1active), t1.remaining());

        t.passSimultaneousTurns();
        ConstrainedTimerTurn t2 = getFirstTurn(t);
        shiftTimerClock(t, p2active);
        assertEquals(p2_initDuration.minus(p2active), t2.remaining());

        t.passSimultaneousTurns();
        assertEquals(p1_initDuration.minus(p1active), t1.remaining());
    }

    @Test
    public void whenEnabledStopOnTurnPass_thenTimerStoppedAfterTurnPass() {
        when(timerConfig.pauseOnTurnPass()).thenReturn(true);

        t.passSimultaneousTurns();

        ConstrainedTimerTurn turn = getFirstTurn(t);
        assertFalse(turn.isGoing());
    }

    @Test
    public void whenDepleteOnZeroRemainingEnabled_thenDepleted() {
        when(timeConstraintConfig.depleteOnZeroRemaining()).thenReturn(true);

        assertFalse(tIncremented.isDepleted());

        ConstrainedTimerTurn turn = getFirstTurn(tIncremented);

        shiftTimerClock(tIncremented, turn.remaining());
        tIncremented.syncWithClock();

        assertTrue(tIncremented.isDepleted());
    }

    @Test
    public void whenRunningMoreThanRemains_thenStoredRemainingIsZero() {
        Duration extra = Duration.ofSeconds(1);

        shiftTimerClock(tIncremented, p1_initDuration.plus(extra));

        ConstrainedTimerTurn turn = getFirstTurn(tIncremented);
        assertEquals(p1, turn.player());
        assertTrue(turn.remaining().isZero());

        passBackToCurrentPlayer(tIncremented);

        assertEquals(p1, turn.player());
        assertTrue(turn.remaining().isZero());
    }

    @Test
    public void timerWithEmptyBankIsDepleted() {
        Timer timer = buildTimerWithEmptyBank();

        assertTrue(timer.isDepleted());
    }

    @Test
    public void whenTimerPaused_thenCanNotStartTurn() {
        ConstrainedTimerTurn turn = getFirstTurn(t);
        assertTrue(turn.isGoing());

        t.pause();
        assertFalse(turn.isGoing());

        turn.start();
        assertFalse(turn.isGoing());

        t.resume();
        assertTrue(turn.isGoing());
    }

    @Test
    public void whenTimerPausedAndTurnPausedAndTimerResumed_thenTurnPaused() {
        ConstrainedTimerTurn turn = getFirstTurn(t);

        t.pause();
        assertFalse(turn.isGoing());

        turn.pause();
        assertFalse(turn.isGoing());

        t.resume();
        assertFalse(turn.isGoing());
    }

    @Test
    public void whenTurnPausedAndTimerPausedAndTurnStartedAndTimerResumed_thenTurnIsGoing() {
        ConstrainedTimerTurn turn = getFirstTurn(t);
        assertTrue(turn.isGoing());

        turn.pause();
        assertFalse(turn.isGoing());

        t.pause();
        turn.start();
        assertFalse(turn.isGoing());

        t.resume();
        assertTrue(turn.isGoing());
    }

    @Test
    public void remainingUnchangedOnResume() {
        ConstrainedTimerTurn turn = getFirstTurn(t);
        Duration total = turn.remaining();

        t.pause();
        shiftTimerClock(t, Duration.ofSeconds(2));

        t.resume();
        assertEquals(total, turn.remaining());
    }

    @Test
    public void alreadyPassedTurnNeverChanges() {
        ConstrainedTimerTurn turn = getFirstTurn(t);
        Duration remaining = turn.remaining();

        t.passSimultaneousTurns();
        shiftTimerClock(t, Duration.ofSeconds(5));

        assertFalse(turn.isGoing());
        assertTrue(turn.isEnded());
        assertEquals(remaining, turn.remaining());
    }

    @Test
    public void simultaneousRunOfSeveralNotPassedTurns() {
        TurnFlow turnFlow = new TurnFlow(new StraightTurnPassingStrategy(), turnFlowConfig);
        TimeConstraint timeConstraint = new TimeConstraint(buildTurnDurationCalculator(), buildTimeBank(), turnFlow, timeConstraintConfig);
        Timer timer = new Timer(turnFlow, timeConstraint, timerConfig);
        timer.setClock(Clock.fixed(Instant.now(), ZoneId.systemDefault()));

        timer.launch();

        ConstrainedSimultaneousTurns turns = timer.getCurrentSimultaneousTurns();

        ArrayList<Duration> before = new ArrayList<>(turns.size());
        Duration active = Duration.ofSeconds(10);

        for (int i = 0; i < turns.size(); i++) {
            before.add(i, turns.get(i).remaining());
        }

        shiftTimerClock(timer, active);

        for (int i = 0; i < turns.size(); i++) {
            Duration expected = before.get(i).minus(active);
            assertEquals(expected, turns.get(i).remaining());
        }
    }

    @Test
    public void turnInfo() {
        ConstrainedTimerTurn turn = getFirstTurn(t);
        TurnInfo turnInfo = turn;

        assertTrue(turnInfo.isGoing());
        assertFalse(turnInfo.isEnded());
        assertEquals(new TimerTurn(GameRound.FIRST, p1), turnInfo.timerTurn());
        assertEquals(GameRound.FIRST, turnInfo.gameTurn());
        assertEquals(1, turnInfo.roundNumber());
        assertEquals(1, turnInfo.phase());
        assertEquals(p1, turnInfo.player());



        t.passSimultaneousTurns();
        t.passSimultaneousTurns();
        t.passSimultaneousTurns();
        turn = getFirstTurn(t);
        turn.end();
        turnInfo = turn;

        assertFalse(turnInfo.isGoing());
        assertTrue(turnInfo.isEnded());
        GameRound round = new GameRound(2, 1);
        assertEquals(new TimerTurn(round, p2), turnInfo.timerTurn());
        assertEquals(round, turnInfo.gameTurn());
        assertEquals(2, turnInfo.roundNumber());
        assertEquals(1, turnInfo.phase());
        assertEquals(p2, turnInfo.player());
    }
}