package flextimer.turnFlow;

import flextimer.Player;

public class StraightTurnFlow extends TurnFlow {
    public StraightTurnFlow(Player[] players, int maxPhases) {
        super(players, maxPhases);
    }

    protected boolean isLastPhase() {
        return phase == maxPhases;
    }

    protected boolean isLastPlayer() {
        return playerIndex + 1 == players.length;
    }

    protected void nextTurn() {
        turnNumber++;
        phase = 1;
        playerIndex = 0;
    }

    protected void nextPhase() {
        phase++;
        playerIndex = 0;
    }

    protected void nextPlayer() {
        playerIndex++;
    }
}