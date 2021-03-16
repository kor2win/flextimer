package flextimer.timerTurnFlow.strategy;

import flextimer.player.Player;
import flextimer.player.PlayersOrder;
import flextimer.player.exception.UnknownPlayer;
import flextimer.timerTurnFlow.TimerTurnFlow;

public class StraightTimerTurnFlow extends TimerTurnFlow {
    public StraightTimerTurnFlow(PlayersOrder playersOrder, int maxPhases) {
        super(playersOrder, maxPhases);
    }

    protected boolean isLastPhase() {
        return phase == maxPhases;
    }

    protected boolean isLastPlayer() {
        Player lastPlayer = playersOrder.last();
        return player.equals(lastPlayer);
    }

    protected void nextTurn() {
        turnNumber++;
        phase = 1;
        player = playersOrder.first();
    }

    protected void nextPhase() {
        phase++;
        player = playersOrder.first();
    }

    protected void nextPlayer() {
        try {
            player = playersOrder.after(player);
        } catch (UnknownPlayer ignored) {
        }
    }
}