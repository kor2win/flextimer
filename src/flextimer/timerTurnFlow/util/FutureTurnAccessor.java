package flextimer.timerTurnFlow.util;

import flextimer.player.Player;

public interface FutureTurnAccessor {
    GameTurn nextTurnForPlayer(Player player, GameTurn gameTurn);
}
