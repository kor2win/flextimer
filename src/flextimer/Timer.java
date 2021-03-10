package flextimer;

import flextimer.exception.PauseWhenPaused;
import flextimer.exception.StartWhenStarted;
import flextimer.player.PlayersOrder;
import flextimer.player.UnknownPlayer;
import flextimer.timerTurnFlow.TimerTurn;
import flextimer.timerTurnFlow.TimerTurnFlow;

public class Timer {
    private final PlayersOrder playersOrder;
    private final TimerTurnFlow timerTurnFlow;

    private boolean isStarted = false;

    public Timer(PlayersOrder playersOrder, TimerTurnFlow timerTurnFlow) {
        this.playersOrder = playersOrder;
        this.timerTurnFlow = timerTurnFlow;
    }

    public void start() throws StartWhenStarted {
        if (isStarted) {
            throw new StartWhenStarted();
        }

        isStarted = true;
    }

    public void passTurn() throws UnknownPlayer {
        timerTurnFlow.passTurn();
    }

    public void pause() throws PauseWhenPaused {
        if (!isStarted) {
            throw new PauseWhenPaused();
        }

        isStarted = false;
    }

    public TimerTurn currentTurn() {
        return timerTurnFlow.timerTurn();
    }
}

