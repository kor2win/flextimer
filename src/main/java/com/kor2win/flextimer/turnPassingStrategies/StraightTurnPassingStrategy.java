package com.kor2win.flextimer.turnPassingStrategies;

import com.kor2win.flextimer.timer.turnFlow.*;

public class StraightTurnPassingStrategy extends RoundCanPlayedSimultaneously {
    public StraightTurnPassingStrategy() {
        super();
    }

    public StraightTurnPassingStrategy(GameRound simultaneousUntil) {
        super(simultaneousUntil);
    }

    @Override
    public TimerTurn nextTurn(PlayersOrder playersOrder, TimerTurn current, int phasesCount) throws UnknownPlayer {
        int roundNumber = current.gameRound.roundNumber;
        int phase = current.gameRound.phase;
        Player player = current.player;

        if (!player.equals(playersOrder.last())) {
            player = playersOrder.after(player);
        } else if (phase + 1 < phasesCount) {
            phase++;
            player = playersOrder.first();
        } else {
            roundNumber++;
            phase = 1;
            player = playersOrder.first();
        }

        return buildTimerTurn(roundNumber, phase, player);
    }

    private TimerTurn buildTimerTurn(int roundNumber, int phase, Player player) {
        return new TimerTurn(
                new GameRound(roundNumber, phase),
                player
        );
    }
}