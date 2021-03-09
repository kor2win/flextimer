package flextimer;

import flextimer.exception.*;
import flextimer.turnFlow.*;

public class Timer {
    private boolean isStarted = false;
    private TurnFlow turnFlow;

    public Timer(Player[] players) {
        turnFlow = new StraightTurnFlow(players, 1);
    }

    public void start() throws StartWhenStarted {
        if (isStarted) {
            throw new StartWhenStarted();
        }

        isStarted = true;
    }

    public void passTurn() throws PassTurnWhenPaused {
        if (!isStarted) {
            throw new PassTurnWhenPaused();
        }

        turnFlow.passTurn();
    }

    public void pause() throws PauseWhenPaused {
        if (!isStarted) {
            throw new PauseWhenPaused();
        }

        isStarted = false;
    }

    public Turn currentTurn() {
        return turnFlow.currentTurn();
    }
}

