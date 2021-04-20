package com.kor2win.flextimer.engine.timer;

import com.kor2win.flextimer.engine.turnFlow.*;
import com.kor2win.flextimer.engine.timeConstraint.*;
import com.kor2win.flextimer.engine.ui.*;
import com.kor2win.flextimer.turnPassingStrategies.*;
import com.kor2win.flextimer.timeBanking.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.stubbing.*;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimerTest {
    private static final Player p1 = new Player("Anton");
    private static final Player p2 = new Player("Max");
    private static final PlayersOrder playersOrder = new PlayersOrder(Arrays.asList(p1, p2));
    private static final Duration increment = Duration.ofSeconds(1);
    private static final Duration p1_initDuration = Duration.ofSeconds(20);
    private static final Duration p2_initDuration = Duration.ofSeconds(30);
    private static final int PHASES_COUNT = 1;

    private Timer t;

    private PlayersTimeBank timeBank;
    private TurnPassingStrategy turnPassingStrategy;
    @Mock private TurnDurationCalculator turnDurationCalculator;
    @Mock private TimerConfig timerConfig;
    @Mock private TimeConstraintConfig timeConstraintConfig;
    @Mock private TurnFlowConfig turnFlowConfig;
    @Mock private TimerObserver observer;

    @BeforeEach
    public void setUpTimer() {
        setUpConfig();
        setUpTimeBank();
        setUpTurnDurationCalculator();
        setUpTurnPassingStrategy();

        TurnFlow turnFlow = new TurnFlow(turnPassingStrategy, turnFlowConfig);
        TimeConstraint timeConstraint = new TimeConstraint(turnDurationCalculator, timeBank, turnFlow, timeConstraintConfig);

        t = new Timer(turnFlow, timeConstraint, timerConfig);
        t.setClock(Clock.fixed(Instant.now(), ZoneId.systemDefault()));

        t.register(observer);
    }

    private void setUpTimeBank() {
        timeBank = new PlayersTimeBank();
        TimerTurn t1p1 = new TimerTurn(GameRound.FIRST, p1);
        TimerTurn t1p2 = new TimerTurn(GameRound.FIRST, p2);
        timeBank.saveRemaining(t1p1, p1_initDuration);
        timeBank.saveRemaining(t1p2, p2_initDuration);
    }

    private void shiftTimerClock(Timer timer, Duration shift) {
        timer.shiftClock(shift);
    }

    private void passBackToCurrentPlayer(Timer t) {
        t.passSimultaneousTurns();
        t.passSimultaneousTurns();
    }

    private void depleteTimer(Timer timer) {
        ConstrainedTimerTurn turn = getFirstTurn(timer);
        shiftTimerClock(timer, turn.remaining());
        timer.syncWithClock();
    }

    private ConstrainedTimerTurn getFirstTurn(Timer t) {
        return t.getCurrentSimultaneousTurns().get(0);
    }

    private void setUpConfig() {
        lenient().when(timerConfig.pauseOnTurnPass()).thenReturn(false);
        lenient().when(timeConstraintConfig.depleteOnZeroRemaining()).thenReturn(false);
        lenient().when(turnFlowConfig.phasesCount()).thenReturn(PHASES_COUNT);
        lenient().when(turnFlowConfig.playersOrder()).thenReturn(playersOrder);
    }

    private void setUpTurnDurationCalculator() {
        lenient().when(turnDurationCalculator.totalTurnDuration(any(), any()))
                .thenAnswer((Answer<Duration>) invocation -> invocation.getArgument(1));
    }

    private void setUpTurnPassingStrategy() {
        turnPassingStrategy = new StraightTurnPassingStrategy(GameRound.FIRST);
    }

    private void depleteTimeBank(PlayersTimeBank timeBank) {
        timeBank.savePlayerRemaining(p1, Duration.ZERO);
        timeBank.savePlayerRemaining(p2, Duration.ZERO);
    }

    @Test
    public void whenOperatingNotStartedTimer_thenExceptionThrown() {
        assertThrows(InteractingWithNotLaunched.class, t::resume);
        assertThrows(InteractingWithNotLaunched.class, t::pause);
        assertThrows(InteractingWithNotLaunched.class, t::passSimultaneousTurns);
        assertThrows(InteractingWithNotLaunched.class, t::syncWithClock);
    }

    @Test
    public void whenStartStarted_thenExceptionThrown() {
        t.launch();
        assertThrows(LaunchingAlreadyLaunched.class, t::launch);
    }

    @Test
    public void canPauseThenResumeThenPause() {
        t.launch();
        t.pause();
        t.resume();
        t.pause();
    }

    @Test
    public void canPassTurn() {
        t.launch();
        assertEquals(p1, getFirstTurn(t).player());
        t.passSimultaneousTurns();
        assertEquals(p2, getFirstTurn(t).player());
    }

    @Test
    public void remainingDurationDecreasesOverTimeOnStartedTurn() {
        t.launch();
        ConstrainedTimerTurn turn = getFirstTurn(t);
        Duration total = turn.remaining();
        Duration elapsed = Duration.ofSeconds(3);

        shiftTimerClock(t, elapsed);

        assertEquals(total.minus(elapsed), turn.remaining());
    }

    @Test
    public void whenBankedDurationForOnePlayerIsZero_thenIsDepleted() {
        t.launch();

        depleteTimer(t);

        assertTrue(t.isDepleted());
    }

    @Test
    public void whenPassOnPaused_thenExceptionThrown() {
        t.launch();
        t.pause();

        assertThrows(PassTurnOnPause.class, t::passSimultaneousTurns);
    }

    @Test
    public void whenPauseOnPaused_thenExceptionThrown() {
        t.launch();
        t.pause();

        assertThrows(PausingPaused.class, t::pause);
    }

    @Test
    public void whenResumeOnResumed_thenExceptionThrown() {
        t.launch();
        assertThrows(ResumingNotPaused.class, t::resume);
    }

    @Test
    public void whenTryingOperateDepletedTimer_thenExceptionThrown() {
        t.launch();
        depleteTimer(t);

        assertThrows(InteractingWithDepleted.class, t::pause);
        assertThrows(InteractingWithDepleted.class, t::resume);
        assertThrows(InteractingWithDepleted.class, t::passSimultaneousTurns);
    }

    @Test
    public void remainingDurationUnaffectedOverTimeOnPausedTimer() {
        t.launch();
        ConstrainedTimerTurn turn = getFirstTurn(t);

        Duration total = turn.remaining();
        Duration elapsedOnPause = Duration.ofSeconds(4);

        t.pause();
        shiftTimerClock(t, elapsedOnPause);

        assertEquals(total, turn.remaining());
    }

    @Test
    public void remainingDurationUnaffectedOverTimeOnPausedTurn() {
        t.launch();
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

        t.launch();
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

        t.launch();
        t.passSimultaneousTurns();

        ConstrainedTimerTurn turn = getFirstTurn(t);
        assertFalse(turn.isGoing());
    }

    @Test
    public void whenDepleteOnZeroRemainingEnabled_thenDepleted() {
        when(timeConstraintConfig.depleteOnZeroRemaining()).thenReturn(true);
        when(turnDurationCalculator.totalTurnDuration(any(), any()))
                .thenAnswer((Answer<Duration>) invocation -> {
                    Duration remainingBeforeStart = invocation.getArgument(1);
                    return remainingBeforeStart.plus(increment);
                });

        t.launch();

        assertFalse(t.isDepleted());

        ConstrainedTimerTurn turn = getFirstTurn(t);

        shiftTimerClock(t, turn.remaining());
        t.syncWithClock();

        assertTrue(t.isDepleted());
    }

    @Test
    public void whenRunningMoreThanRemains_thenStoredRemainingIsZero() {
        when(turnDurationCalculator.totalTurnDuration(any(), any()))
                .thenAnswer((Answer<Duration>) invocation -> {
                    Duration remainingBeforeStart = invocation.getArgument(1);
                    return remainingBeforeStart.plus(increment);
                });

        Duration extra = Duration.ofSeconds(1);

        t.launch();

        shiftTimerClock(t, p1_initDuration.plus(extra));

        ConstrainedTimerTurn turn = getFirstTurn(t);
        assertEquals(p1, turn.player());
        assertTrue(turn.remaining().isZero());

        passBackToCurrentPlayer(t);

        assertEquals(p1, turn.player());
        assertTrue(turn.remaining().isZero());
    }

    @Test
    public void timerWithEmptyBankIsDepleted() {
        depleteTimeBank(timeBank);

        t.launch();

        assertTrue(t.isDepleted());
    }

    @Test
    public void whenTimerPaused_thenCanNotStartTurn() {
        t.launch();
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
        t.launch();
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
        t.launch();
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
        t.launch();
        ConstrainedTimerTurn turn = getFirstTurn(t);
        Duration total = turn.remaining();

        t.pause();
        shiftTimerClock(t, Duration.ofSeconds(2));

        t.resume();
        assertEquals(total, turn.remaining());
    }

    @Test
    public void alreadyPassedTurnNeverChanges() {
        t.launch();
        ConstrainedTimerTurn turn = getFirstTurn(t);
        Duration remaining = turn.remaining();

        t.passSimultaneousTurns();
        shiftTimerClock(t, Duration.ofSeconds(5));

        assertFalse(turn.isGoing());
        assertTrue(turn.isEnded());
        assertEquals(remaining, turn.remaining());
    }

    @Test
    public void simultaneousRunOfSeveralNotPassedTurns() throws Exception {
        FieldUtils.writeField(turnPassingStrategy, "simultaneousUntil", null, true);

        t.launch();

        ConstrainedSimultaneousTurns turns = t.getCurrentSimultaneousTurns();

        ArrayList<Duration> before = new ArrayList<>(turns.size());
        Duration active = Duration.ofSeconds(10);

        for (int i = 0; i < turns.size(); i++) {
            before.add(i, turns.get(i).remaining());
        }

        shiftTimerClock(t, active);

        for (int i = 0; i < turns.size(); i++) {
            Duration expected = before.get(i).minus(active);
            assertEquals(expected, turns.get(i).remaining());
        }
    }

    @Test
    public void turnInfo() {
        t.launch();
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

    @Test
    public void observeTimer() {
        t.launch();
        verify(observer, times(1)).handleLaunch();

        t.pause();
        verify(observer, times(1)).handlePause();

        t.resume();
        verify(observer, times(1)).handleResume();

        t.passSimultaneousTurns();
        verify(observer, times(1)).handlePass();

        t.syncWithClock();
        verify(observer, times(1)).handleSync();

        depleteTimer(t);
        verify(observer, times(1)).handleDeplete();
    }

    @Test
    public void observeTimerTurn() {
        ConstrainedTimerTurn turn = getFirstTurn(t);

        t.launch();
        verify(observer, times(1)).handleTurnStart(turn);

        turn.pause();
        verify(observer, times(1)).handleTurnPause(turn);

        turn.start();
        verify(observer, times(2)).handleTurnStart(turn);

        t.pause();
        verify(observer, times(1)).handleTurnSuppressed(turn);

        t.resume();
        verify(observer, times(1)).handleTurnResumed(turn);

        turn.end();
        verify(observer, times(1)).handleTurnEnd(turn);
    }
}