package flextimer.ui;

import flextimer.turnFlow.*;
import flextimer.timeConstraint.*;
import org.jetbrains.annotations.*;

import java.time.*;

public class Timer {
    private final TimerClock timerClock = new TimerClock(Clock.system(ZoneId.systemDefault()));

    private final TurnFlow turnFlow;
    private final TimeConstraint timeConstraint;
    private final TimerConfig config;

    private SimultaneousTurns simultaneousTurns;
    private ConstrainedSimultaneousTurns constrainedSimultaneousTurns;
    private boolean isPaused = false;

    public Timer(
            TurnFlow turnFlow,
            TimeConstraint timeConstraint,
            TimerConfig config
    ) {
        this.turnFlow = turnFlow;
        this.timeConstraint = timeConstraint;
        this.config = config;

        simultaneousTurns = turnFlow.firstSimultaneousTurns();
        constrainedSimultaneousTurns = timeConstraint.applyTo(simultaneousTurns, timerClock);
        constrainedSimultaneousTurns.startAll();
    }

    public void passSimultaneousTurns() throws Depleted, PassTurnOnPause {
        if (isDepleted()) {
            throw new Depleted();
        }

        if (isPaused()) {
            throw new PassTurnOnPause();
        }

        constrainedSimultaneousTurns.endAll();

        switchToNextTurns();

        if (!config.pauseOnTurnPass()) {
            constrainedSimultaneousTurns.startAll();
        }
    }

    private void switchToNextTurns() {
        try {
            simultaneousTurns = turnFlow.nextRoundOfTurns(simultaneousTurns.lastTurn());
        } catch (UnknownPlayer ignored) {
        }
        constrainedSimultaneousTurns = timeConstraint.applyTo(simultaneousTurns, timerClock);
    }

    public void pause() throws Depleted {
        if (isDepleted()) {
            throw new Depleted();
        }

        if (!isPaused()) {
            isPaused = true;
            constrainedSimultaneousTurns.suppressAll();
        }
    }

    public void resume() throws Depleted {
        if (isDepleted()) {
            throw new Depleted();
        }

        if (isPaused()) {
            isPaused = false;
            constrainedSimultaneousTurns.resumeAll();
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isDepleted() {
        return timeConstraint.isDepleted();
    }

    public void syncWithClock() {
        if (isDepleted()) {
            return;
        }

        constrainedSimultaneousTurns.endAllDepleted();
    }

    public ConstrainedSimultaneousTurns getConstrainedSimultaneousTurns() {
        return constrainedSimultaneousTurns;
    }

    @TestOnly
    protected void setClock(Clock clock) {
        timerClock.setClock(clock);
    }

    @TestOnly
    protected void shiftClock(Duration shift) {
        timerClock.shiftClock(shift);
    }
}