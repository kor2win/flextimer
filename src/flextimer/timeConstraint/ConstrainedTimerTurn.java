package flextimer.timeConstraint;

import flextimer.turnFlow.*;

import java.time.*;

public class ConstrainedTimerTurn {
    private final TimerTurn timerTurn;
    private final TimeConstraint timeConstraint;
    private Duration remaining;
    private boolean isTimerGoing = false;
    private Instant startTime;
    private boolean isEnded = false;

    public ConstrainedTimerTurn(TimerTurn timerTurn, TimeConstraint timeConstraint) {
        this.timerTurn = timerTurn;
        this.timeConstraint = timeConstraint;

        remaining = timeConstraint.turnDurationCalculator.totalTurnDuration(
                timerTurn.gameTurn,
                timeConstraint.bankedRemaining(timerTurn)
        );

        if (remaining.isZero()) {
            depleteBank();
        } else if (timeConstraint.isDebtsApplyingEnabled()) {
            Duration debt = timeConstraint.popDebt(timerTurn);
            saveNewRemaining(remaining.minus(debt));

            if (remaining.isZero()) {
                isEnded = true;
            }

            if (isEnoughToDepleteTimer()) {
                timeConstraint.deplete();
            }
        }
    }

    private void depleteBank() {
        isEnded = true;
        timeConstraint.deplete();
        remaining = Duration.ZERO;
        timeConstraint.timeBank.saveRemainingDuration(timerTurn, Duration.ZERO);
    }

    public void start(Instant instant) {
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
            timeConstraint.deplete();
        }
    }

    private void saveNewRemaining(Duration newRemaining) {
        boolean remainingNotPositive = newRemaining.isNegative() || newRemaining.isZero();
        if (timeConstraint.isDebtsApplyingEnabled() && remainingNotPositive) {
            TimerTurn nextTurn = timeConstraint.turnFlow.nextTurn(timerTurn);
            timeConstraint.rememberDebt(nextTurn, newRemaining.negated());
            newRemaining = Duration.ZERO;
        }

        remaining = newRemaining;
        timeConstraint.timeBank.saveRemainingDuration(timerTurn, remaining);
    }

    private boolean isEnoughToDepleteTimer() {
        if (!remaining.isZero()) {
            return false;
        }

        if (timeConstraint.depleteOnZeroRemaining()) {
            return true;
        } else {
            TimerTurn futureTurn = timeConstraint.turnFlow.nextTurnOfSamePlayer(timerTurn);
            Duration futureDuration = timeConstraint.turnDurationCalculator.totalTurnDuration(futureTurn.gameTurn, Duration.ZERO);

            return futureDuration.isZero();
        }
    }

    public void pause(Instant instant) {
        if (isEnded()) {
            return;
        }

        remaining = remaining(instant);
        isTimerGoing = false;
    }

    public Duration remaining(Instant instant) {
        Duration elapsed = isGoing()
                ? Duration.between(startTime, instant)
                : Duration.ZERO;

        return remaining.minus(elapsed);
    }

    public boolean isDepletedAt(Instant instant) {
        if (!isGoing()) {
            return isEnded;
        }

        Duration r = remaining(instant);
        return r.isNegative() || r.isZero();
    }

    public boolean isGoing() {
        return isTimerGoing;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public TimerTurn timerTurn() {
        return timerTurn;
    }

    public Player player() {
        return timerTurn.player;
    }
}
