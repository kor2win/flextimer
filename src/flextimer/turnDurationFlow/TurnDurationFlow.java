package flextimer.turnDurationFlow;

import flextimer.player.Player;
import flextimer.timeBank.TimeBank;
import flextimer.timerTurnFlow.util.FutureTurnAccessor;
import flextimer.timerTurnFlow.util.GameTurn;
import flextimer.timerTurnFlow.util.TimerTurn;
import flextimer.turnDurationCalculator.TurnDurationCalculator;
import flextimer.turnDurationFlow.exception.SwitchingToNewTurnWithoutEndingCurrent;
import flextimer.turnDurationFlow.exception.TimerDepleted;
import flextimer.turnDurationFlow.util.ContinuousTurn;

import java.time.Duration;
import java.time.Instant;

public class TurnDurationFlow {
    protected final TurnDurationCalculator turnDurationCalculator;
    protected final TimeBank timeBank;
    protected final FutureTurnAccessor futureTurnAccessor;
    protected Duration debt = Duration.ZERO;
    protected boolean isDepleted = false;
    protected boolean depleteOnZeroRemaining = false;

    private MyContinuousTurn currentContinuousTurn;
    private boolean pauseOnTurnPass = false;

    public TurnDurationFlow(TurnDurationCalculator turnDurationCalculator, TimeBank timeBank, TimerTurn firstTurn, FutureTurnAccessor futureTurnAccessor) {
        this.turnDurationCalculator = turnDurationCalculator;
        this.timeBank = timeBank;
        this.currentContinuousTurn = new MyContinuousTurn(firstTurn);
        this.futureTurnAccessor = futureTurnAccessor;
    }

    public void switchToNewTurn(TimerTurn timerTurn, Instant instant) throws SwitchingToNewTurnWithoutEndingCurrent, TimerDepleted {
        if (!currentContinuousTurn.isEnded()) {
            throw new SwitchingToNewTurnWithoutEndingCurrent();
        }

        if (isDepleted()) {
            throw new TimerDepleted();
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

    public void setDepleteOnZeroRemaining(boolean depleteOnZeroRemaining) {
        this.depleteOnZeroRemaining = depleteOnZeroRemaining;
    }

    public Duration playerRemainingDuration(Player player, Instant instant) {
        return currentContinuousTurn.timerTurn.player == player
                ? currentContinuousTurn.remaining(instant)
                : timeBank.remainingTime(player);
    }

    public boolean isDepleted() {
        return isDepleted;
    }

    private class MyContinuousTurn implements ContinuousTurn {
        private final TimerTurn timerTurn;
        private Duration remaining;
        private boolean isTimerGoing = false;
        private Instant startTime;
        private boolean isEnded = false;

        public MyContinuousTurn(TimerTurn timerTurn) {
            this.timerTurn = timerTurn;

            remaining = turnDurationCalculator.remainingAfterTurnStart(
                    timerTurn.gameTurn,
                    timeBank.remainingTime(timerTurn.player)
            );

            if (remaining.isZero()) {
                depleteTurn();
            } else {
                Duration adjustedRemaining = remaining.minus(debt);
                saveNewRemaining(adjustedRemaining);

                if (remaining.isZero()) {
                    isEnded = true;
                }

                if (isEnoughToDepleteTimer()) {
                    TurnDurationFlow.this.isDepleted = true;
                }
            }
        }

        private void depleteTurn() {
            isEnded = true;
            TurnDurationFlow.this.isDepleted = true;
            remaining = Duration.ZERO;
            timeBank.playerTime(timerTurn.player, Duration.ZERO);
        }

        public void startTime(Instant instant) {
            if (isEnded()) {
                return;
            }

            isTimerGoing = true;
            startTime = instant;
        }

        public void end(Instant instant) {
            if (isEnded()) {
                return;
            }

            isEnded = true;

            saveNewRemaining(remaining(instant));

            if (isEnoughToDepleteTimer()) {
                TurnDurationFlow.this.isDepleted = true;
            }
        }

        private void saveNewRemaining(Duration newRemaining) {
            if (newRemaining.isNegative() || newRemaining.isZero()) {
                TurnDurationFlow.this.debt = newRemaining.negated();
                newRemaining = Duration.ZERO;
            } else {
                TurnDurationFlow.this.debt = Duration.ZERO;
            }

            remaining = newRemaining;
            timeBank.playerTime(timerTurn.player, remaining);
        }

        private boolean isEnoughToDepleteTimer() {
            if (!remaining.isZero()) {
                return false;
            }

            if (depleteOnZeroRemaining) {
                return true;
            } else {
                GameTurn futureTurn = futureTurnAccessor.nextTurnForPlayer(timerTurn.player, timerTurn.gameTurn);
                Duration futureDuration = turnDurationCalculator.remainingAfterTurnStart(futureTurn, Duration.ZERO);

                return futureDuration.isZero();
            }
        }

        public void pauseTime(Instant instant) {
            if (isEnded()) {
                return;
            }

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
