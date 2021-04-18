package com.kor2win.flextimer.turnPassingStrategies;

import com.kor2win.flextimer.timer.turnFlow.*;

public class RotatingTurnPassingStrategy extends RoundCanPlayedSimultaneously {
    public RotatingTurnPassingStrategy() {
        super();
    }

    public RotatingTurnPassingStrategy(GameRound simultaneousUntil) {
        super(simultaneousUntil);
    }

    @Override
    public TimerTurn firstTurn(PlayersOrder playersOrder) {
        Player first = playersOrder.first();
        return buildTimerTurn(1, 1, first, first);
    }

    @Override
    public TimerTurn nextTurn(PlayersOrder playersOrder, TimerTurn current, int phasesCount) {
        RotatingTimerTurn c = (RotatingTimerTurn) current;

        int roundNumber = c.gameRound.roundNumber;
        int phase = c.gameRound.phase;
        Player player = c.player;
        Player firstPlayer = c.firstPlayerForRound;

        Player possibleNext = playersOrder.after(player);
        if (!firstPlayer.equals(possibleNext)) {
            player = possibleNext;
        } else if (phase + 1 < phasesCount) {
            phase++;
            player = firstPlayer;
        } else {
            roundNumber++;
            phase = 1;
            firstPlayer = playersOrder.after(firstPlayer);
            player = firstPlayer;
        }

        return buildTimerTurn(roundNumber, phase, player, firstPlayer);
    }

    private TimerTurn buildTimerTurn(int roundNumber, int phase, Player player, Player firstPlayerOnTurn) {
        return new RotatingTimerTurn(
                new GameRound(roundNumber, phase),
                player,
                firstPlayerOnTurn
        );
    }

    private static class RotatingTimerTurn extends TimerTurn {
        public final Player firstPlayerForRound;

        public RotatingTimerTurn(GameRound gameRound, Player player, Player firstPlayerForRound) {
            super(gameRound, player);
            this.firstPlayerForRound = firstPlayerForRound;
        }
    }
}
