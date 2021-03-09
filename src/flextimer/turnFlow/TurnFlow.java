package flextimer.turnFlow;

import flextimer.Player;

public abstract class TurnFlow {
    protected final Player[] players;
    protected final int maxPhases;

    protected int turnNumber = 1;
    protected int phase = 1;
    protected int playerIndex = 0;

    public TurnFlow(Player[] players, int maxPhases) {
        this.players = players;
        this.maxPhases = maxPhases;
    }

    public void passTurn() {
        if (!isLastPlayer()) {
            nextPlayer();
        } else if (!isLastPhase()) {
            nextPhase();
        } else {
            nextTurn();
        }
    }

    abstract protected boolean isLastPhase();
    abstract protected boolean isLastPlayer();
    abstract protected void nextTurn();
    abstract protected void nextPhase();
    abstract protected void nextPlayer();

    public Turn currentTurn() {
        var _turnNumber = this.turnNumber;
        var _phase = this.phase;
        var _player = this.players[this.playerIndex];

        return new Turn() {
            public final int number = _turnNumber;
            public final int phase = _phase;
            public final Player player = _player;

            public int number() {
                return number;
            }

            public int phase() {
                return phase;
            }

            public Player player() {
                return player;
            }
        };
    }
}