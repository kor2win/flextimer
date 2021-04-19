package com.kor2win.flextimer.engine.ui;

import com.kor2win.flextimer.engine.turnFlow.*;

public abstract class TurnKey {
    public abstract TimerTurn timerTurn();

    public int hashCode() {
        return timerTurn().hashCode();
    }

    public boolean equals(TurnKey k) {
        return timerTurn().equals(k.timerTurn());
    }

    public boolean equals(Object obj) {
        return obj instanceof TurnKey
                ? this.equals((TurnKey) obj)
                : super.equals(obj);
    }
}
