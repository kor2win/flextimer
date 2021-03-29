package flextimer.ui;

import flextimer.turnFlow.*;

public interface TurnInfo {
    boolean isGoing();

    boolean isEnded();

    TimerTurn timerTurn();

    GameRound gameTurn();

    int roundNumber();

    int phase();

    Player player();
}
