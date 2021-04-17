package com.kor2win.flextimer.timer.timeConstraint;

import java.util.*;

public class ConstrainedSimultaneousTurns {
    private final List<ConstrainedTimerTurn> constrainedTurns;

    public ConstrainedSimultaneousTurns(List<ConstrainedTimerTurn> constrainedTurns) {
        this.constrainedTurns = constrainedTurns;
    }

    public int size() {
        return constrainedTurns.size();
    }

    public ConstrainedTimerTurn get(int index) throws IndexOutOfBoundsException {
        return constrainedTurns.get(index);
    }

    public void endAll() {
        constrainedTurns.forEach(ConstrainedTimerTurn::end);
    }

    public void startAll() {
        constrainedTurns.forEach(ConstrainedTimerTurn::start);
    }

    public void suppressAll() {
        constrainedTurns.forEach(ConstrainedTimerTurn::suppress);
    }

    public void resumeAll() {
        constrainedTurns.forEach(ConstrainedTimerTurn::resume);
    }

    public void endAllDepleted() {
        constrainedTurns.forEach(t -> {
            if (t.remaining().isZero()) {
                t.end();
            }
        });
    }
}
