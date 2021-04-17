package com.kor2win.flextimer.timer.timeConstraint;

import com.kor2win.flextimer.timer.turnFlow.*;
import com.kor2win.flextimer.timer.ui.*;

import java.time.*;
import java.util.*;

public class TimeConstraint {
    protected final TurnDurationCalculator turnDurationCalculator;
    protected final TimeBank timeBank;
    protected final TurnFlow turnFlow;
    protected final TimeConstraintConfig config;

    private boolean isDepleted = false;

    public TimeConstraint(TurnDurationCalculator turnDurationCalculator, TimeBank timeBank, TurnFlow turnFlow, TimeConstraintConfig config) {
        this.turnDurationCalculator = turnDurationCalculator;
        this.timeBank = timeBank;
        this.turnFlow = turnFlow;
        this.config = config;
    }

    public ConstrainedTimerTurn applyTo(TimerTurn timerTurn, TimerClock timerClock) {
        return new ConstrainedTimerTurn(timerTurn, timerClock, this);
    }

    public ConstrainedSimultaneousTurns applyTo(SimultaneousTurns simultaneousTurns, TimerClock timerClock) {
        List<ConstrainedTimerTurn> constrainedTurns = new ArrayList<>(simultaneousTurns.size());

        for (int i = 0; i < simultaneousTurns.size(); i++) {
            constrainedTurns.add(applyTo(simultaneousTurns.get(i), timerClock));
        }

        return new ConstrainedSimultaneousTurns(constrainedTurns);
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
        return config.depleteOnZeroRemaining();
    }
}
