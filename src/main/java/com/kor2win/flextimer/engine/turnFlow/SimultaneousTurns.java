package com.kor2win.flextimer.engine.turnFlow;

import java.util.*;

public class SimultaneousTurns {
    private final List<TimerTurn> turns;
    private final TimerTurn last;

    public SimultaneousTurns(List<TimerTurn> turns) {
        this.turns = turns;
        last = turns.get(turns.size() - 1);
    }

    public TimerTurn lastTurn() {
        return last;
    }

    public int size() {
        return turns.size();
    }

    public TimerTurn get(int index) {
        return turns.get(index);
    }
}
