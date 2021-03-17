package flextimer.timer;

import flextimer.exception.PauseWhenPaused;
import flextimer.exception.StartWhenStarted;
import flextimer.player.Player;
import flextimer.timeBank.TimeBank;
import flextimer.timerTurnFlow.util.TimerTurn;
import flextimer.timerTurnFlow.TimerTurnFlow;
import flextimer.turnDurationCalculator.TurnDurationCalculator;
import flextimer.turnDurationFlow.exception.TimerDepleted;
import flextimer.turnDurationFlow.util.ContinuousTurn;
import flextimer.turnDurationFlow.exception.SwitchingToNewTurnWithoutEndingCurrent;
import flextimer.turnDurationFlow.TurnDurationFlow;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class Timer {
    protected Clock clock = Clock.system(ZoneId.systemDefault());

    private final PassTimeScheduler passTimeScheduler = new PassTimeScheduler();
    private final TimerTurnFlow timerTurnFlow;
    private final TurnDurationFlow turnDurationFlow;

    public Timer(TimerTurnFlow timerTurnFlow, TurnDurationCalculator turnDurationCalculator, TimeBank timeBank) {
        this.timerTurnFlow = timerTurnFlow;
        this.turnDurationFlow = new TurnDurationFlow(turnDurationCalculator, timeBank, currentTurn(), timerTurnFlow);
    }

    public void start() throws StartWhenStarted {
        if (isStarted()) {
            throw new StartWhenStarted();
        }

        if (turnDurationFlow.isDepleted()) {
            return;
        }

        continuousTurn().startTime(clock.instant());
        passTimeScheduler.schedule(continuousTurn().depletedAt());
    }

    public void passTurn() throws TimerDepleted, SwitchingToNewTurnWithoutEndingCurrent {
        passTimeScheduler.cancel();
        continuousTurn().end(clock.instant());

        if (turnDurationFlow.isDepleted()) {
            return;
        }

        timerTurnFlow.switchToNextTurn();
        turnDurationFlow.switchToNewTurn(currentTurn(), clock.instant());

        if (continuousTurn().isTimerGoing()) {
            passTimeScheduler.schedule(continuousTurn().depletedAt());
        }
    }

    public void pause() throws PauseWhenPaused {
        if (!isStarted()) {
            throw new PauseWhenPaused();
        }

        if (turnDurationFlow.isDepleted()) {
            return;
        }

        passTimeScheduler.cancel();
        continuousTurn().pauseTime(clock.instant());
    }

    public boolean isStarted() {
        return continuousTurn().isTimerGoing();
    }

    public TimerTurn currentTurn() {
        return timerTurnFlow.timerTurn();
    }

    public Duration remainingDuration() {
        return continuousTurn().remaining(clock.instant());
    }

    private ContinuousTurn continuousTurn() {
        return turnDurationFlow.continuousTurn();
    }

    public Duration playerRemainingDuration(Player player) {
        return turnDurationFlow.playerRemainingDuration(player, clock.instant());
    }

    public boolean isDepleted() {
        return turnDurationFlow.isDepleted();
    }

    protected void setPauseOnTurnPass(boolean pauseOnTurnPass) {
        turnDurationFlow.setPauseOnTurnPass(pauseOnTurnPass);
    }

    protected void setDepleteOnZeroRemaining(boolean depleteOnZeroRemaining) {
        turnDurationFlow.setDepleteOnZeroRemaining(depleteOnZeroRemaining);
    }

    protected void cancelScheduledTurnPass() {
        passTimeScheduler.cancel();
    }

    protected void waitUntilScheduledTurnPass() {
        passTimeScheduler.waitUntilScheduledTurnPass();
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