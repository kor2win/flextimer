package com.kor2win.flextimer.timer.ui;

import com.kor2win.flextimer.timer.turnFlow.*;
import com.kor2win.flextimer.timer.timeConstraint.*;

import java.time.*;

public class Timer {
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
    }

    public void passSimultaneousTurns() {
        if (isDepleted()) {
            throw new TimerDepleted();
        }

        if (isPaused()) {
            throw new PassTurnOnPause();
        }

        if (isStopped()) {
            throw new TimerNotLaunched();
        }

        getCurrentSimultaneousTurns().endAll();
        switchToNextTurns();

        if (!config.pauseOnTurnPass()) {
            getCurrentSimultaneousTurns().launchAll();
        }
    }

    private void switchToNextTurns() {
        SimultaneousTurns simultaneousTurns = turnFlow.nextRoundOfTurns(getCurrentSimultaneousTurns().lastTurn().timerTurn());
        constrainedSimultaneousTurns = timeConstraint.applyTo(simultaneousTurns, timerClock);
    }

    public void launch() {
        if (!isStopped()) {
            throw new TimerAlreadyLaunched();
        }

        isStopped = false;
        getCurrentSimultaneousTurns().launchAll();
    }

    public void pause() {
        if (isDepleted()) {
            throw new TimerDepleted();
        }

        if (isStopped()) {
            throw new TimerNotLaunched();
        }

        if (isPaused()) {
            throw new TimerPaused();
        }

        isPaused = true;
        getCurrentSimultaneousTurns().suppressAll();
    }

    public void resume() {
        if (isDepleted()) {
            throw new TimerDepleted();
        }

        if (isStopped()) {
            throw new TimerNotLaunched();
        }

        if (!isPaused()) {
            throw new TimerNotPaused();
        }

        isPaused = false;
        getCurrentSimultaneousTurns().resumeAll();
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
            throw new TimerNotLaunched();
        }

        getCurrentSimultaneousTurns().endAllDepleted();
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