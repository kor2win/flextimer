package flextimer.turnDurationCalculator.util;

import flextimer.timerTurnFlow.util.GameTurn;
import flextimer.turnDurationCalculator.exception.NegativeIncrement;

import java.time.Duration;
import java.util.Hashtable;

public class ChessTurnDurationIncrements implements ChessTurnDurationIncrementsReader {
    private final Hashtable<Integer, Duration> turnIncrement = new Hashtable<>();
    private final Hashtable<Integer, Duration> phaseIncrement = new Hashtable<>();

    public void setTurnIncrement(int turnNumber, Duration increment) throws NegativeIncrement {
        throwIfNegative(increment);

        turnIncrement.put(turnNumber, increment);
    }

    public void setPhaseIncrement(int phase, Duration increment) throws NegativeIncrement {
        throwIfNegative(increment);

        phaseIncrement.put(phase, increment);
    }

    private void throwIfNegative(Duration increment) throws NegativeIncrement {
        if (increment.isNegative()) {
            throw new NegativeIncrement();
        }
    }

    public Duration increment(GameTurn turn) {
        if (turn.phase == 1 && turnIncrement.containsKey(turn.turnNumber)) {
            return turnIncrement.get(turn.turnNumber);
        } else {
            return phaseIncrement.getOrDefault(turn.phase, Duration.ZERO);
        }
    }
}
