package flextimer.timerTurnFlow.strategy;

import flextimer.player.Player;
import flextimer.player.PlayersOrder;
import flextimer.player.exception.UnknownPlayer;
import flextimer.timerTurnFlow.TimerTurnFlow;

public class RotatingTimerTurnFlow extends TimerTurnFlow {
    private Player firstPlayer;

    public RotatingTimerTurnFlow(PlayersOrder playersOrder, int maxPhases) {
        super(playersOrder, maxPhases);

        firstPlayer = playersOrder.first();
    }

    protected boolean isLastPhase() {
        return phase == maxPhases;
    }

    protected boolean isLastPlayer() {
        try {
            Player lastPlayer = playersOrder.before(firstPlayer);
            return player.equals(lastPlayer);
        } catch (UnknownPlayer unknownPlayer) {
            return false;
        }
    }

    protected void nextTurn() {
        try {
            firstPlayer = playersOrder.after(firstPlayer);
        } catch (UnknownPlayer ignored) {
        }

        turnNumber++;
        phase = 1;
        player = firstPlayer;
    }

    protected void nextPhase() {
        phase++;
        player = firstPlayer;
    }

    protected void nextPlayer() {
        try {
            player = playersOrder.after(player);
        } catch (UnknownPlayer ignored) {
        }
    }

    protected TimerTurnFlow newInstance() {
        return new RotatingTimerTurnFlow(playersOrder, maxPhases);
    }
}
