package flextimer.turnFlow;

import flextimer.Player;

public class RotatingTurnFlow extends TurnFlow {
    private int firstPlayerIndex = 0;

    public RotatingTurnFlow(Player[] players, int maxPhases) {
        super(players, maxPhases);
    }

    protected boolean isLastPhase() {
        return phase == maxPhases;
    }

    protected boolean isLastPlayer() {
        return firstPlayerIndex == 0
                ? playerIndex + 1 == players.length
                : playerIndex + 1 == firstPlayerIndex;
    }

    protected void nextTurn() {
        if (firstPlayerIndex + 1 == players.length) {
            firstPlayerIndex = 0;
        } else {
            firstPlayerIndex++;
        }

        turnNumber++;
        phase = 1;
        playerIndex = firstPlayerIndex;
    }

    protected void nextPhase() {
        phase++;
        playerIndex = firstPlayerIndex;
    }

    protected void nextPlayer() {
        if (playerIndex + 1 == players.length) {
            playerIndex = 0;
        } else {
            playerIndex++;
        }
    }
}
