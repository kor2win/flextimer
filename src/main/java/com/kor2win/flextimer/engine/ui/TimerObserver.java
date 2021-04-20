package com.kor2win.flextimer.engine.ui;

import com.kor2win.flextimer.engine.timeConstraint.*;

public interface TimerObserver {
    void handleLaunch();

    void handlePause();

    void handleResume();

    void handlePass();

    void handleDeplete();

    void handleSync();

    void handleTurnStart(TurnKey turn);

    void handleTurnPause(TurnKey turn);

    void handleTurnEnd(TurnKey turn);

    void handleTurnSuppressed(TurnKey turn);

    void handleTurnResumed(TurnKey turn);
}