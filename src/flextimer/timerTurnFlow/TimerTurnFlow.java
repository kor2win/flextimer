package flextimer.timerTurnFlow;

import flextimer.timerTurnFlow.util.GameTurn;
import flextimer.player.Player;
import flextimer.player.PlayersOrder;
import flextimer.timerTurnFlow.util.TimerTurn;

public abstract class TimerTurnFlow {
    protected final PlayersOrder playersOrder;
    protected final int maxPhases;

    protected int turnNumber = 1;
    protected int phase = 1;
    protected Player player;

    private GameTurn gameTurn;
    private TimerTurn timerTurn;

    public TimerTurnFlow(PlayersOrder playersOrder, int maxPhases) {
        this.playersOrder = playersOrder;
        this.maxPhases = maxPhases;
        this.player = playersOrder.first();

        gameTurn = buildGameTurn();
        timerTurn = buildTimerTurn();
    }

    abstract protected boolean isLastPhase();

    abstract protected boolean isLastPlayer();

    abstract protected void nextTurn();

    abstract protected void nextPhase();

    abstract protected void nextPlayer();

    public void switchToNextTurn() {
        if (!isLastPlayer()) {
            nextPlayer();
        } else if (!isLastPhase()) {
            nextPhase();
            gameTurn = buildGameTurn();
        } else {
            nextTurn();
            gameTurn = buildGameTurn();
        }

        timerTurn = buildTimerTurn();
    }

    private GameTurn buildGameTurn() {
        return new GameTurn(turnNumber, phase);
    }

    private TimerTurn buildTimerTurn() {
        return new TimerTurn(gameTurn, player);
    }

    public TimerTurn timerTurn() {
        return timerTurn;
    }

    public int turnNumber() {
        return turnNumber;
    }

    public int phase() {
        return phase;
    }

    public Player player() {
        return player;
    }
}