package flextimer.turnFlow.strategy;

import flextimer.player.*;
import flextimer.player.exception.*;
import flextimer.turnFlow.*;
import flextimer.turnFlow.util.*;

public class RotatingTurnPassingStrategy extends TurnPassingStrategy {
    @Override
    public TimerTurn firstTurn(PlayersOrder playersOrder) {
        Player first = playersOrder.first();
        return buildTimerTurn(1, 1, first, first);
    }

    @Override
    public TimerTurn turnAfter(PlayersOrder playersOrder, TimerTurn current, int phasesCount) throws UnknownPlayer {
        RotatingTimerTurn c = (RotatingTimerTurn) current;

        int turnNumber = c.gameTurn.turnNumber;
        int phase = c.gameTurn.phase;
        Player player = c.player;
        Player firstPlayer = c.firstPlayerOnTurn;

        Player possibleNext = playersOrder.after(player);
        if (!firstPlayer.equals(possibleNext)) {
            player = possibleNext;
        } else if (phase + 1 < phasesCount) {
            phase++;
            player = firstPlayer;
        } else {
            turnNumber++;
            phase = 1;
            firstPlayer = playersOrder.after(firstPlayer);
            player = firstPlayer;
        }

        return buildTimerTurn(turnNumber, phase, player, firstPlayer);
    }

    private TimerTurn buildTimerTurn(int turnNumber, int phase, Player player, Player firstPlayerOnTurn) {
        return new RotatingTimerTurn(
                new GameTurn(turnNumber, phase),
                player,
                firstPlayerOnTurn
        );
    }

    private static class RotatingTimerTurn extends TimerTurn {
        public final Player firstPlayerOnTurn;

        public RotatingTimerTurn(GameTurn gameTurn, Player player, Player firstPlayerOnTurn) {
            super(gameTurn, player);
            this.firstPlayerOnTurn = firstPlayerOnTurn;
        }
    }
}
