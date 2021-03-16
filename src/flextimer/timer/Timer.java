package flextimer.timer;

import flextimer.exception.PauseWhenPaused;
import flextimer.exception.StartWhenStarted;
import flextimer.timeBank.TimeBank;
import flextimer.timerTurnFlow.util.TimerTurn;
import flextimer.timerTurnFlow.TimerTurnFlow;
import flextimer.turnDurationCalculator.TurnDurationCalculator;
import flextimer.turnDurationFlow.util.ContinuousTurn;
import flextimer.turnDurationFlow.exception.SwitchingToNewTurnWithoutEndingCurrent;
import flextimer.turnDurationFlow.TurnDurationFlow;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class Timer {
    protected Clock clock = Clock.system(ZoneId.systemDefault());
    protected PassTimeScheduler passTimeScheduler = new PassTimeScheduler();

    private final TimerTurnFlow timerTurnFlow;
    private final TurnDurationFlow turnDurationFlow;

    public Timer(TimerTurnFlow timerTurnFlow, TurnDurationCalculator turnDurationCalculator, TimeBank timeBank) {
        this.timerTurnFlow = timerTurnFlow;
        this.turnDurationFlow = new TurnDurationFlow(turnDurationCalculator, timeBank, currentTurn());
    }

    public void start() throws StartWhenStarted {
        if (isStarted()) {
            throw new StartWhenStarted();
        }

        continuousTurn().startTime(clock.instant());
        passTimeScheduler.scheduleTurnPass(continuousTurn().depletedAt());
    }

    public void passTurn() {
        passTimeScheduler.clearScheduler();
        continuousTurn().end(clock.instant());

        timerTurnFlow.switchToNextTurn();
        try {
            turnDurationFlow.switchToNewTurn(currentTurn(), clock.instant());
        } catch (SwitchingToNewTurnWithoutEndingCurrent ignored) {
        }

        if (continuousTurn().isTimerGoing()) {
            passTimeScheduler.scheduleTurnPass(continuousTurn().depletedAt());
        }
    }

    public void pause() throws PauseWhenPaused {
        if (!isStarted()) {
            throw new PauseWhenPaused();
        }

        passTimeScheduler.clearScheduler();
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

    public void setPauseOnTurnPass(boolean pauseOnTurnPass) {
        turnDurationFlow.setPauseOnTurnPass(pauseOnTurnPass);
    }

    private ContinuousTurn continuousTurn() {
        return turnDurationFlow.continuousTurn();
    }

    protected class PassTimeScheduler {
        protected Thread thread = new Thread();

        protected void waitUntilScheduled() {
            if (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException ignored) {
                }
            }
        }

        private void scheduleTurnPass(Instant instant) {
            clearScheduler();

            Runnable passTurn = () -> {
                while (true) {
                    if (!clock.instant().isBefore(instant)) {
                        passTurn();

                        break;
                    }
                }
            };

            thread = new Thread(passTurn);
            thread.start();
        }

        private void clearScheduler() {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
    }
}