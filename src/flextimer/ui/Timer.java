package flextimer.ui;

import flextimer.turnFlow.*;
import flextimer.timeConstraint.*;

import java.time.*;

public class Timer {
    protected Clock clock = Clock.system(ZoneId.systemDefault());

    private final PassTimeScheduler passTimeScheduler = new PassTimeScheduler();
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
        passTimeScheduler.schedule(constrainedTurn.depletedAt());
    }

    public void passTurn() throws Depleted {
        if (isDepleted()) {
            throw new Depleted();
        }

        boolean isGoing = constrainedTurn.isGoing();

        passTimeScheduler.cancel();
        constrainedTurn.end(clock.instant());

        TimerTurn current = constrainedTurn.timerTurn();
        TimerTurn nextTurn = turnFlow.nextTurn(current);
        constrainedTurn = timeConstraint.applyTo(nextTurn);

        if (isGoing && !pauseOnTurnPass) {
            constrainedTurn.start(clock.instant());
            passTimeScheduler.schedule(constrainedTurn.depletedAt());
        }
    }

    public void pause() throws PauseWhenPaused, Depleted {
        if (isDepleted()) {
            throw new Depleted();
        }

        if (!isStarted()) {
            throw new PauseWhenPaused();
        }

        passTimeScheduler.cancel();
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

    protected void cancelScheduledTurnPass() {
        passTimeScheduler.cancel();
    }

    protected void waitUntilScheduledTurnPass() {
        passTimeScheduler.waitUntilScheduledTurnPass();
    }

    public Player currentPlayer() {
        return constrainedTurn.player();
    }

    protected class PassTimeScheduler {
        protected Thread thread = new Thread();

        protected void waitUntilScheduledTurnPass() {
            if (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException ignored) {
                }
            }
        }

        private void schedule(Instant instant) {
            cancel();

            Runnable passTurn = () -> {
                while (true) {
                    if (!clock.instant().isBefore(instant)) {
                        tryPassTurn();

                        break;
                    }
                }
            };

            thread = new Thread(passTurn);
            thread.start();
        }

        private void tryPassTurn() {
            try {
                passTurn();
            } catch (Exception ignored) {
            }
        }

        private void cancel() {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
    }
}