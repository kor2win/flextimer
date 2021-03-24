package flextimer.timeConstraint;

import flextimer.turnFlow.*;

import java.time.*;
import java.util.concurrent.*;

public class TimeConstraint {
    protected final TurnDurationCalculator turnDurationCalculator;
    protected final TimeBank timeBank;
    protected final TurnFlow turnFlow;

    private final ConcurrentHashMap<TimerTurn, Duration> debts = new ConcurrentHashMap<>();
    private boolean isDepleted = false;
    private boolean depleteOnZeroRemaining = false;

    public TimeConstraint(TurnDurationCalculator turnDurationCalculator, TimeBank timeBank, TurnFlow turnFlow) {
        this.turnDurationCalculator = turnDurationCalculator;
        this.timeBank = timeBank;
        this.turnFlow = turnFlow;
    }

    public ConstrainedTimerTurn applyTo(TimerTurn timerTurn) {
        return new ConstrainedTimerTurn(timerTurn, this);
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

    protected void deplete() {
        isDepleted = true;
    }

    protected boolean depleteOnZeroRemaining() {
        return depleteOnZeroRemaining;
    }

    protected Duration popDebt(TimerTurn timerTurn) {
        Duration r = debts.getOrDefault(timerTurn, Duration.ZERO);
        debts.remove(timerTurn);

        return r;
    }

    protected void rememberDebt(TimerTurn timerTurn, Duration debt) {
        debts.put(timerTurn, debt);
    }

    /**
     * Override to disable remaining recalculation by debt from previous turn.
     * For instance, this could be useful for simultaneous turns.
     * @return boolean
     */
    protected boolean isDebtsApplyingEnabled() {
        return true;
    }
}
