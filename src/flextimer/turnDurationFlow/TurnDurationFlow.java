package flextimer.turnDurationFlow;

import flextimer.timeBank.TimeBank;
import flextimer.timerTurnFlow.util.TimerTurn;
import flextimer.turnDurationCalculator.TurnDurationCalculator;
import flextimer.turnDurationFlow.exception.SwitchingToNewTurnWithoutEndingCurrent;
import flextimer.turnDurationFlow.util.ContinuousTurn;

import java.time.Duration;
import java.time.Instant;

public class TurnDurationFlow {
    protected final TurnDurationCalculator turnDurationCalculator;
    protected final TimeBank timeBank;

    private MyContinuousTurn currentContinuousTurn;
    private boolean pauseOnTurnPass = false;

    public TurnDurationFlow(TurnDurationCalculator turnDurationCalculator, TimeBank timeBank, TimerTurn firstTurn) {
        this.turnDurationCalculator = turnDurationCalculator;
        this.timeBank = timeBank;
        this.currentContinuousTurn = new MyContinuousTurn(firstTurn);
    }

    public void switchToNewTurn(TimerTurn timerTurn, Instant instant) throws SwitchingToNewTurnWithoutEndingCurrent {
        if (!currentContinuousTurn.isEnded()) {
            throw new SwitchingToNewTurnWithoutEndingCurrent();
        }

        boolean isTimerGoing = currentContinuousTurn.isTimerGoing();

        currentContinuousTurn = new MyContinuousTurn(timerTurn);

        if (isTimerGoing && !pauseOnTurnPass) {
            currentContinuousTurn.startTime(instant);
        }
    }

    public ContinuousTurn continuousTurn() {
        return currentContinuousTurn;
    }

    public void setPauseOnTurnPass(boolean pauseOnTurnPass) {
        this.pauseOnTurnPass = pauseOnTurnPass;
    }

    private class MyContinuousTurn implements ContinuousTurn {
        private final TimerTurn timerTurn;
        private Duration remaining;
        private boolean isTimerGoing = false;
        private Instant startTime;
        private boolean isEnded = false;

        public MyContinuousTurn(TimerTurn timerTurn) {
            this.timerTurn = timerTurn;
            this.remaining = turnDurationCalculator.remainingAfterTurnStart(
                    timerTurn.gameTurn,
                    timeBank.remainingTime(timerTurn.player)
            );
        }

        public void startTime(Instant instant) {
            isTimerGoing = true;
            startTime = instant;
        }

        public void end(Instant instant) {
            if (isEnded() || !isTimerGoing()) {
                return;
            }

            isEnded = true;

            Duration elapsed = Duration.between(startTime, instant);
            remaining = remaining.minus(elapsed);
            timeBank.playerTime(timerTurn.player, remaining);
        }

        public void pauseTime(Instant instant) {
            remaining = remaining(instant);
            isTimerGoing = false;
        }

        public Instant depletedAt() {
            if (!isTimerGoing()) {
                return Instant.MAX;
            }

            return startTime.plus(remaining);
        }

        public Duration remaining(Instant instant) {
            Duration elapsed = isTimerGoing()
                    ? Duration.between(startTime, instant)
                    : Duration.ZERO;

            return remaining.minus(elapsed);
        }

        public boolean isTimerGoing() {
            return isTimerGoing;
        }

        public boolean isEnded() {
            return isEnded;
        }
    }
}
