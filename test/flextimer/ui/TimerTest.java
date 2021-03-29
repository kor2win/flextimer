package flextimer.ui;

import flextimer.timeConstraint.*;
import flextimer.turnFlow.*;
import timeBanking.*;
import turnPassingStrategies.*;
import org.junit.jupiter.api.*;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeAll
    public static void setUpPlayers() throws Exception {
        turnPassingStrategy = new StraightTurnPassingStrategy(GameRound.FIRST);
        playersOrder = buildPlayers();
        p1 = playersOrder.first();
        p2 = playersOrder.after(p1);
        p1_initDuration = Duration.ofSeconds(20);
        p2_initDuration = Duration.ofSeconds(30);
    }

    @BeforeEach
    public void setUpTimer() {
        Config config = new Config();

        t = buildTimer(buildTurnDurationCalculator(), buildTimeBank(), config);
        tIncremented = buildTimer(buildTurnDurationCalculatorWithIncrement(), buildTimeBank(), config);
    }

    private Timer buildTimer(TurnDurationCalculator turnDurationCalculator, TimeBank timeBank, Config config) {
        TurnFlow turnFlow = new TurnFlow(playersOrder, turnPassingStrategy, PHASES_COUNT);
        TimeConstraint timeConstraint = new TimeConstraint(turnDurationCalculator, timeBank, turnFlow, config);

        Timer t = new Timer(turnFlow, timeConstraint, config);
        t.setClock(Clock.fixed(Instant.now(), ZoneId.systemDefault()));

        return t;
    }

    private Timer buildTimerWithEmptyBank() {
        return buildTimer(buildTurnDurationCalculator(), new PlayersTimeBank(), new Config());
    }

    private Timer buildTimerWithConfig(TurnDurationCalculator turnDurationCalculator, Config config) {
        return buildTimer(turnDurationCalculator, buildTimeBank(), config);
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
        TimerTurn t1p1 = new TimerTurn(GameRound.FIRST, p1);
        TimerTurn t1p2 = new TimerTurn(GameRound.FIRST, p2);
        timeBank.saveRemainingDuration(t1p1, p1_initDuration);
        timeBank.saveRemainingDuration(t1p2, p2_initDuration);
        return timeBank;
    }

    private void shiftTimerClock(Timer timer, Duration shift) {
        timer.shiftClock(shift);
    }

    private void passBackToCurrentPlayer(Timer t) throws Exception {
        t.passSimultaneousTurns();
        t.passSimultaneousTurns();
    }

    private void depleteTimer(Timer timer) throws Exception {
        if (timer.isPaused()) {
            timer.resume();
        }

        ConstrainedTimerTurn turn = getFirstTurn(timer);
        shiftTimerClock(timer, turn.remaining());
        timer.syncWithClock();
    }

    private ConstrainedTimerTurn getFirstTurn(Timer t) {
        return t.getConstrainedSimultaneousTurns().get(0);
    }

    @Test
    public void canStartThenPauseThenResume() throws Exception {
        t.pause();
        t.resume();
        t.pause();
    }

    @Test
    public void canPassTurn() throws Exception {
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
    public void whenBankedDurationForOnePlayerIsZero_thenIsDepleted() throws Exception {
        depleteTimer(t);

        assertTrue(t.isDepleted());
    }

    @Test
    public void whenPassOnPaused_thenExceptionThrown() throws Exception {
        t.pause();

        assertThrows(PassTurnOnPause.class, t::passSimultaneousTurns);
    }

    @Test
    public void whenTryingOperateDepletedTimer_thenExceptionThrown() throws Exception {
        depleteTimer(t);

        assertThrows(Depleted.class, t::pause);
        assertThrows(Depleted.class, t::resume);
        assertThrows(Depleted.class, t::passSimultaneousTurns);
    }

    @Test
    public void remainingDurationUnaffectedOverTimeOnPausedTimer() throws Exception {
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
    public void remainingDurationStoredOnTurnPass() throws Exception {
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
    public void whenEnabledStopOnTurnPass_thenTimerStoppedAfterTurnPass() throws Exception {
        Config config = new Config();
        config.setPauseOnTurnPass(true);
        Timer timer = buildTimerWithConfig(buildTurnDurationCalculator(), config);

        timer.passSimultaneousTurns();

        ConstrainedTimerTurn turn = getFirstTurn(timer);
        assertFalse(turn.isGoing());
    }

    @Test
    public void whenDepleteOnZeroRemainingEnabled_thenDepleted() {
        Config config = new Config();
        config.setDepleteOnZeroRemaining(true);
        Timer tIncremented = buildTimerWithConfig(buildTurnDurationCalculatorWithIncrement(), config);

        assertFalse(tIncremented.isDepleted());

        ConstrainedTimerTurn turn = getFirstTurn(tIncremented);

        shiftTimerClock(tIncremented, turn.remaining());
        tIncremented.syncWithClock();

        assertTrue(tIncremented.isDepleted());
    }

    @Test
    public void whenRunningMoreThanRemains_thenStoredRemainingIsZero() throws Exception {
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
        var timer = buildTimerWithEmptyBank();

        assertTrue(timer.isDepleted());
    }

    @Test
    public void whenTimerPaused_thenCanNotStartTurn() throws Exception {
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
    public void whenTimerPausedAndTurnPausedAndTimerResumed_thenTurnPaused() throws Exception {
        ConstrainedTimerTurn turn = getFirstTurn(t);

        t.pause();
        assertFalse(turn.isGoing());

        turn.pause();
        assertFalse(turn.isGoing());

        t.resume();
        assertFalse(turn.isGoing());
    }

    @Test
    public void whenTurnPausedAndTimerPausedAndTurnStartedAndTimerResumed_thenTurnIsGoing() throws Exception {
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
    public void remainingUnchangedOnResume() throws Exception {
        ConstrainedTimerTurn turn = getFirstTurn(t);
        Duration total = turn.remaining();

        t.pause();
        shiftTimerClock(t, Duration.ofSeconds(2));

        t.resume();
        assertEquals(total, turn.remaining());
    }

    @Test
    public void alreadyPassedTurnNeverChanges() throws Exception {
        ConstrainedTimerTurn turn = getFirstTurn(t);
        Duration remaining = turn.remaining();

        t.passSimultaneousTurns();
        shiftTimerClock(t, Duration.ofSeconds(5));

        assertFalse(turn.isGoing());
        assertTrue(turn.isEnded());
        assertEquals(remaining, turn.remaining());
    }

    @Test
    public void simultaneousRunOfTwoNotPassedTurns() {
        TurnFlow turnFlow = new TurnFlow(playersOrder, new StraightTurnPassingStrategy(), PHASES_COUNT);
        Config config = new Config();
        TimeConstraint timeConstraint = new TimeConstraint(buildTurnDurationCalculator(), buildTimeBank(), turnFlow, config);
        Timer timer = new Timer(turnFlow, timeConstraint, config);

        ConstrainedSimultaneousTurns turns = timer.getConstrainedSimultaneousTurns();

        ArrayList<Duration> before = new ArrayList<>(turns.size());
        Duration active = Duration.ofSeconds(10);

        for (int i = 0; i < turns.size(); i++) {
            before.add(i, turns.get(i).remaining());
        }

        shiftTimerClock(timer, active);

        for (int i = 0; i < turns.size(); i++) {
            var expected = before.get(i).minus(active);
            assertEquals(expected, turns.get(i).remaining());
        }
    }

    @Test
    public void turnInfo() throws Exception {
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