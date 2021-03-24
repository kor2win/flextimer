package flextimer.ui;

import flextimer.turnFlow.*;
import flextimer.timeConstraint.*;

import java.time.*;

public class Timer {
    protected Clock clock = Clock.system(ZoneId.systemDefault());

    private final TurnFlow turnFlow;
    private final TimeConstraint timeConstraint;

    private ConstrainedTimerTurn constrainedTurn;
    private boolean pauseOnTurnPass = false;

    public Timer(TurnFlow turnFlow, TimeConstraint timeConstraint) {
        this.turnFlow = turnFlow;
        this.timeConstraint = timeConstraint;

        this.constrainedTurn = timeConstraint.applyTo(turnFlow.firstTurn());
    }

    public void start() throws StartWhenStarted, Depleted {
        if (isDepleted()) {
            throw new Depleted();
        }

        if (isStarted()) {
            throw new StartWhenStarted();
        }

        constrainedTurn.start(clock.instant());
    }

    public void passTurn() throws Depleted {
        if (isDepleted()) {
            throw new Depleted();
        }

        boolean isGoing = constrainedTurn.isGoing();

        constrainedTurn.end(clock.instant());

        TimerTurn current = constrainedTurn.timerTurn();
        TimerTurn nextTurn = turnFlow.nextTurn(current);
        constrainedTurn = timeConstraint.applyTo(nextTurn);

        if (isGoing && !pauseOnTurnPass) {
            constrainedTurn.start(clock.instant());
        }
    }

    public void pause() throws PauseWhenPaused, Depleted {
        if (isDepleted()) {
            throw new Depleted();
        }

        if (!isStarted()) {
            throw new PauseWhenPaused();
        }

        constrainedTurn.pause(clock.instant());
    }

    public boolean isStarted() {
        return constrainedTurn.isGoing();
    }

    public TimerTurn currentTurn() {
        return constrainedTurn.timerTurn();
    }

    public Duration currentRemainingDuration() {
        return constrainedTurn.remaining(clock.instant());
    }

    public Duration remainingDuration(TimerTurn timerTurn) {
        return timerTurn.equals(constrainedTurn.timerTurn())
                ? currentRemainingDuration()
                : timeConstraint.bankedRemaining(timerTurn);
    }

    public boolean isDepleted() {
        return timeConstraint.isDepleted();
    }

    public void setPauseOnTurnPass(boolean pauseOnTurnPass) {
        this.pauseOnTurnPass = pauseOnTurnPass;
    }

    public void setDepleteOnZeroRemaining(boolean depleteOnZeroRemaining) {
        timeConstraint.setDepleteOnZeroRemaining(depleteOnZeroRemaining);
    }

    public Player currentPlayer() {
        return constrainedTurn.player();
    }

    public void syncWithClock() {
        if (isDepleted()) {
            return;
        }

        if (constrainedTurn.isDepletedAt(clock.instant())) {
            tryPassTurn();
        }
    }

    private void tryPassTurn() {
        try {
            passTurn();
        } catch (Depleted ignored) {
        }
    }
}