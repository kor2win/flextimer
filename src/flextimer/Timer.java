package flextimer;

import flextimer.exception.*;
import flextimer.turnFlow.*;

public class Timer {
    private boolean isStarted = false;
    private final TurnFlow turnFlow;

    public Timer(TurnFlow turnFlow) {
        this.turnFlow = turnFlow;
    }

    public void start() throws StartWhenStarted {
        if (isStarted) {
            throw new StartWhenStarted();
        }

        isStarted = true;
    }

    public void passTurn() {
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

