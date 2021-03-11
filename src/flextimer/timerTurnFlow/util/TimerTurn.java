package flextimer.timerTurnFlow.util;

import flextimer.player.Player;

public class TimerTurn {
    public final GameTurn gameTurn;
    public final Player player;

    public TimerTurn(GameTurn gameTurn, Player player) {
        this.gameTurn = gameTurn;
        this.player = player;
    }
}
