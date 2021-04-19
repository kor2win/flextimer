package com.kor2win.flextimer.engine.timer;

import com.kor2win.flextimer.engine.turnFlow.*;
import com.kor2win.flextimer.engine.timeConstraint.*;
import com.kor2win.flextimer.engine.ui.*;

import java.time.*;

public class Timer extends ObservableTimer {
    private final TimerClock timerClock = new TimerClock(Clock.system(ZoneId.systemDefault()));

    private final TurnFlow turnFlow;
    private final TimeConstraint timeConstraint;
    private final TimerConfig config;

    private ConstrainedSimultaneousTurns constrainedSimultaneousTurns;
    private boolean isStopped = true;
    private boolean isPaused = false;

    public Timer(
            TurnFlow turnFlow,
            TimeConstraint timeConstraint,
            TimerConfig config
    ) {
        this.turnFlow = turnFlow;
        this.timeConstraint = timeConstraint;
        this.config = config;

        timeConstraint.linkWith(this);
    }

    public void passSimultaneousTurns() {
        if (isDepleted()) {
            throw new InteractingWithDepleted();
        }

        if (isPaused()) {
            throw new PassTurnOnPause();
        }

        if (isStopped()) {
            throw new InteractingWithNotLaunched();
        }

        getCurrentSimultaneousTurns().endAll();
        switchToNextTurns();

        if (!config.pauseOnTurnPass()) {
            getCurrentSimultaneousTurns().launchAll();
        }

        notifyPass();
    }

    private void switchToNextTurns() {
        SimultaneousTurns simultaneousTurns = turnFlow.nextRoundOfTurns(getCurrentSimultaneousTurns().lastTimerTurn());
        constrainedSimultaneousTurns = timeConstraint.applyTo(simultaneousTurns, timerClock);
    }

    public void launch() {
        if (!isStopped()) {
            throw new LaunchingAlreadyLaunched();
        }

        isStopped = false;
        getCurrentSimultaneousTurns().launchAll();
        notifyLaunched();
    }

    public void pause() {
        if (isDepleted()) {
            throw new InteractingWithDepleted();
        }

        if (isStopped()) {
            throw new InteractingWithNotLaunched();
        }

        if (isPaused()) {
            throw new PausingPaused();
        }

        isPaused = true;
        getCurrentSimultaneousTurns().suppressAll();
        notifyPaused();
    }

    public void resume() {
        if (isDepleted()) {
            throw new InteractingWithDepleted();
        }

        if (isStopped()) {
            throw new InteractingWithNotLaunched();
        }

        if (!isPaused()) {
            throw new ResumingNotPaused();
        }

        isPaused = false;
        getCurrentSimultaneousTurns().resumeAll();
        notifyResumed();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public boolean isDepleted() {
        return timeConstraint.isDepleted();
    }

    public void syncWithClock() {
        if (isDepleted()) {
            return;
        }

        if (isStopped()) {
            throw new InteractingWithNotLaunched();
        }

        getCurrentSimultaneousTurns().endAllDepleted();
        notifySync();
    }

    public ConstrainedSimultaneousTurns getCurrentSimultaneousTurns() {
        if (constrainedSimultaneousTurns == null) {
            SimultaneousTurns simultaneousTurns = turnFlow.firstSimultaneousTurns();
            constrainedSimultaneousTurns = timeConstraint.applyTo(simultaneousTurns, timerClock);
        }

        return constrainedSimultaneousTurns;
    }

    protected void setClock(Clock clock) {
        timerClock.setClock(clock);
    }

    protected void shiftClock(Duration shift) {
        timerClock.shiftClock(shift);
    }
}