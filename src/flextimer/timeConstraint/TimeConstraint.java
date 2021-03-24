package flextimer.timeConstraint;

import flextimer.turnFlow.*;

import java.time.*;

public class TimeConstraint {
    protected final TurnDurationCalculator turnDurationCalculator;
    protected final TimeBank timeBank;
    protected final FutureTurnAccessor futureTurnAccessor;
    protected Duration debt = Duration.ZERO;
    protected boolean isDepleted = false;
    protected boolean depleteOnZeroRemaining = false;

    public TimeConstraint(TurnDurationCalculator turnDurationCalculator, TimeBank timeBank, FutureTurnAccessor futureTurnAccessor) {
        this.turnDurationCalculator = turnDurationCalculator;
        this.timeBank = timeBank;
        this.futureTurnAccessor = futureTurnAccessor;
    }

    public ConstrainedTimerTurn applyTo(TimerTurn timerTurn) {
        return new MyConstrainedTimerTurn(timerTurn);
    }

    public void setDepleteOnZeroRemaining(boolean depleteOnZeroRemaining) {
        this.depleteOnZeroRemaining = depleteOnZeroRemaining;
    }

    public Duration bankedRemaining(TimerTurn timerTurn) {
        return timeBank.getAccumulated(timerTurn);
    }

    public boolean isDepleted() {
        return isDepleted;
    }

    private class MyConstrainedTimerTurn implements ConstrainedTimerTurn {
        private final TimerTurn timerTurn;
        private Duration remaining;
        private boolean isTimerGoing = false;
        private Instant startTime;
        private boolean isEnded = false;

        public MyConstrainedTimerTurn(TimerTurn timerTurn) {
            this.timerTurn = timerTurn;

            remaining = turnDurationCalculator.totalTurnDuration(
                    timerTurn.gameTurn,
                    timeBank.getAccumulated(timerTurn)
            );

            if (remaining.isZero()) {
                depleteBank();
            } else {
                saveNewRemaining(remaining.minus(debt));

                if (remaining.isZero()) {
                    isEnded = true;
                }

                if (isEnoughToDepleteTimer()) {
                    TimeConstraint.this.isDepleted = true;
                }
            }
        }

        private void depleteBank() {
            isEnded = true;
            TimeConstraint.this.isDepleted = true;
            remaining = Duration.ZERO;
            timeBank.saveRemainingDuration(timerTurn, Duration.ZERO);
        }

        @Override
        public void start(Instant instant) {
            if (isEnded()) {
                return;
            }

            isTimerGoing = true;
            startTime = instant;
        }

        @Override
        public void end(Instant instant) {
            if (isEnded()) {
                return;
            }

            isEnded = true;

            saveNewRemaining(remaining(instant));

            if (isEnoughToDepleteTimer()) {
                TimeConstraint.this.isDepleted = true;
            }
        }

        private void saveNewRemaining(Duration newRemaining) {
            if (newRemaining.isNegative() || newRemaining.isZero()) {
                TimeConstraint.this.debt = newRemaining.negated();
                newRemaining = Duration.ZERO;
            } else {
                TimeConstraint.this.debt = Duration.ZERO;
            }

            remaining = newRemaining;
            timeBank.saveRemainingDuration(timerTurn, remaining);
        }

        private boolean isEnoughToDepleteTimer() {
            if (!remaining.isZero()) {
                return false;
            }

            if (depleteOnZeroRemaining) {
                return true;
            } else {
                TimerTurn futureTurn = futureTurnAccessor.nextTurnOfSamePlayer(timerTurn);
                Duration futureDuration = turnDurationCalculator.totalTurnDuration(futureTurn.gameTurn, Duration.ZERO);

                return futureDuration.isZero();
            }
        }

        @Override
        public void pause(Instant instant) {
            if (isEnded()) {
                return;
            }

            remaining = remaining(instant);
            isTimerGoing = false;
        }

        @Override
        public Instant depletedAt() {
            return isGoing()
                    ? startTime.plus(remaining)
                    : Instant.MAX;
        }

        @Override
        public Duration remaining(Instant instant) {
            Duration elapsed = isGoing()
                    ? Duration.between(startTime, instant)
                    : Duration.ZERO;

            return remaining.minus(elapsed);
        }

        @Override
        public boolean isGoing() {
            return isTimerGoing;
        }

        @Override
        public boolean isEnded() {
            return isEnded;
        }

        @Override
        public TimerTurn timerTurn() {
            return timerTurn;
        }

        @Override
        public Player player() {
            return timerTurn.player;
        }
    }
}
