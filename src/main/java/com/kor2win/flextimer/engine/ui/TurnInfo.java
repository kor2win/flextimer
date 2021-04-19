package com.kor2win.flextimer.engine.ui;

import com.kor2win.flextimer.engine.turnFlow.*;

public interface TurnInfo {
    boolean isGoing();

    boolean isEnded();

    TimerTurn timerTurn();

    GameRound gameTurn();

    int roundNumber();

    int phase();

    Player player();
}
