package flextimer.turnFlow.strategy;

import flextimer.player.*;
import flextimer.player.exception.*;
import flextimer.turnFlow.*;
import flextimer.turnFlow.util.*;

public class StraightTurnPassingStrategy extends TurnPassingStrategy {
    @Override
    public TimerTurn turnAfter(PlayersOrder playersOrder, TimerTurn current, int phasesCount) throws UnknownPlayer {
        int turnNumber = current.gameTurn.turnNumber;
        int phase = current.gameTurn.phase;
        Player player = current.player;

        if (!player.equals(playersOrder.last())) {
            player = playersOrder.after(player);
        } else if (phase + 1 < phasesCount) {
            phase++;
            player = playersOrder.first();
        } else {
            turnNumber++;
            phase = 1;
            player = playersOrder.first();
        }

        return buildTimerTurn(turnNumber, phase, player);
    }

    private TimerTurn buildTimerTurn(int turnNumber, int phase, Player player) {
        return new TimerTurn(
                new GameTurn(turnNumber, phase),
                player
        );
    }
}