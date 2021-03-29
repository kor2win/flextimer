package turnPassingStrategies;

import flextimer.turnFlow.*;

import java.util.*;

public abstract class RoundCanPlayedSimultaneously extends TurnPassingStrategy {
    private final GameRound simultaneousUntil;

    public RoundCanPlayedSimultaneously() {
        simultaneousUntil = null;
    }

    public RoundCanPlayedSimultaneously(GameRound simultaneousUntil) {
        this.simultaneousUntil = simultaneousUntil;
    }

    @Override
    public SimultaneousTurns firstSimultaneousTurns(PlayersOrder playersOrder, int phasesCount) {
        if (isSimultaneousTurnsEnabled(GameRound.FIRST)) {
            return buildFirstRound(playersOrder, phasesCount);
        } else {
            ArrayList<TimerTurn> turns = new ArrayList<>(1);
            turns.add(firstTurn(playersOrder));

            return new SimultaneousTurns(turns);
        }
    }

    @Override
    public SimultaneousTurns simultaneousTurnsAfterTurn(PlayersOrder playersOrder, TimerTurn lastPlayed, int phasesCount) throws UnknownPlayer {
        var from = nextTurn(playersOrder, lastPlayed, phasesCount);
        if (isSimultaneousTurnsEnabled(from.gameRound)) {
            return buildRound(playersOrder, from, phasesCount);
        } else {
            ArrayList<TimerTurn> turns = new ArrayList<>(1);
            turns.add(from);

            return new SimultaneousTurns(turns);
        }
    }

    private boolean isSimultaneousTurnsEnabled(GameRound round) {
        if (simultaneousUntil == null) {
            return true;
        }

        boolean isRoundPassed = round.roundNumber > simultaneousUntil.roundNumber;
        boolean isRoundSame = round.roundNumber == simultaneousUntil.roundNumber;
        boolean isPhasePassedOrSame = round.phase >= simultaneousUntil.phase;

        return !(isRoundPassed || (isRoundSame && isPhasePassedOrSame));
    }

    private SimultaneousTurns buildFirstRound(PlayersOrder playersOrder, int phasesCount) {
        List<TimerTurn> turns = new ArrayList<>();

        TimerTurn t = firstTurn(playersOrder);
        turns.add(t);

        try {
            t = nextTurn(playersOrder, t, phasesCount);
        } catch (UnknownPlayer ignored) {
        }

        while (t.roundNumber() == 1 && t.phase() == 1) {
            turns.add(t);

            try {
                t = nextTurn(playersOrder, t, phasesCount);
            } catch (UnknownPlayer ignored) {
            }
        }

        return new SimultaneousTurns(turns);
    }

    private SimultaneousTurns buildRound(PlayersOrder playersOrder, TimerTurn from, int phasesCount) throws UnknownPlayer {
        List<TimerTurn> turns = new ArrayList<>();

        int roundNumber = from.roundNumber();
        int phase = from.phase();

        while (from.roundNumber() == roundNumber && from.phase() == phase) {
            turns.add(from);
            from = nextTurn(playersOrder, from, phasesCount);
        }

        return new SimultaneousTurns(turns);
    }
}
