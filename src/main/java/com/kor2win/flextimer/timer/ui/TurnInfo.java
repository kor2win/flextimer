package com.kor2win.flextimer.timer.ui;

import com.kor2win.flextimer.timer.turnFlow.*;

public interface TurnInfo {
    boolean isGoing();

    boolean isEnded();

    TimerTurn timerTurn();

    GameRound gameTurn();

    int roundNumber();

    int phase();

    Player player();
}
