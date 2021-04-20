package com.kor2win.flextimer.engine.timeConstraint;

import com.kor2win.flextimer.engine.turnFlow.*;
import com.kor2win.flextimer.engine.timer.*;
import com.kor2win.flextimer.engine.ui.*;

import java.time.*;
import java.util.*;

public class TimeConstraint {
    protected final TurnDurationCalculator turnDurationCalculator;
    protected final TimeBank timeBank;
    protected final FutureTurnAccess futureTurnAccess;
    protected final TimeConstraintConfig config;

    private boolean isDepleted = false;

    protected ObservableTimer observableTimer;

    public TimeConstraint(TurnDurationCalculator turnDurationCalculator, TimeBank timeBank, FutureTurnAccess futureTurnAccess, TimeConstraintConfig config) {
        this.turnDurationCalculator = turnDurationCalculator;
        this.timeBank = timeBank;
        this.futureTurnAccess = futureTurnAccess;
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

        if (observableTimer != null) {
            observableTimer.notifyDepleted();
        }
    }

    protected boolean depleteOnZeroRemaining() {
        return config.depleteOnZeroRemaining();
    }

    public void linkWith(ObservableTimer timer) {
        observableTimer = timer;
    }
}
