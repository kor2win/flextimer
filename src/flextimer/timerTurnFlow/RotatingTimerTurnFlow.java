package flextimer.timerTurnFlow;

import flextimer.player.Player;
import flextimer.player.PlayersOrder;
import flextimer.player.UnknownPlayer;

public class RotatingTimerTurnFlow extends TimerTurnFlow {
    private Player firstPlayer;

    public RotatingTimerTurnFlow(PlayersOrder playersOrder, int maxPhases) {
        super(playersOrder, maxPhases);

        player = playersOrder.first();
        firstPlayer = playersOrder.first();
    }

    protected boolean isLastPhase() {
        return phase == maxPhases;
    }

    protected boolean isLastPlayer() throws UnknownPlayer {
        Player lastPlayer = playersOrder.before(firstPlayer);
        return player.equals(lastPlayer);
    }

    protected void nextTurn() throws UnknownPlayer {
        firstPlayer = playersOrder.after(firstPlayer);

        turnNumber++;
        phase = 1;
        player = firstPlayer;
    }

    protected void nextPhase() {
        phase++;
        player = firstPlayer;
    }

    protected void nextPlayer() throws UnknownPlayer {
        player = playersOrder.after(player);
    }
}
