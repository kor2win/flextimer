package com.kor2win.flextimer.engine.ui;

import com.kor2win.flextimer.engine.timeConstraint.*;

import java.util.*;

public class ObservableTimer {
    private final List<TimerObserver> observers = new ArrayList<>();

    public void register(TimerObserver observer) {
        observers.add(observer);
    }

    public void notifyLaunched() {
        observers.forEach(TimerObserver::handleLaunch);
    }

    public void notifyPass() {
        observers.forEach(TimerObserver::handlePass);
    }

    public void notifyPaused() {
        observers.forEach(TimerObserver::handlePause);
    }

    public void notifyResumed() {
        observers.forEach(TimerObserver::handleResume);
    }

    public void notifySync() {
        observers.forEach(TimerObserver::handleSync);
    }

    public void notifyDepleted() {
        observers.forEach(TimerObserver::handleDeplete);
    }

    public void notifyTurnStarted(TurnKey turn) {
        observers.forEach(o -> o.handleTurnStart(turn));
    }

    public void notifyTurnPaused(TurnKey turn) {
        observers.forEach(o -> o.handleTurnPause(turn));
    }

    public void notifyTurnSuppressed(TurnKey turn) {
        observers.forEach(o -> o.handleTurnSuppressed(turn));
    }

    public void notifyTurnResumed(TurnKey turn) {
        observers.forEach(o -> o.handleTurnResumed(turn));
    }

    public void notifyTurnEnded(TurnKey turn) {
        observers.forEach(o -> o.handleTurnEnd(turn));
    }
}
