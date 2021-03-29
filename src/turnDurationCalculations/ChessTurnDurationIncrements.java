package turnDurationCalculations;

import flextimer.turnFlow.*;

import java.time.*;
import java.util.*;

public class ChessTurnDurationIncrements implements ChessTurnDurationIncrementsReader {
    private final Hashtable<Integer, Duration> roundIncrement = new Hashtable<>();
    private final Hashtable<Integer, Duration> phaseIncrement = new Hashtable<>();

    public void setRoundIncrement(int roundNumber, Duration increment) throws NegativeIncrement {
        throwIfNegative(increment);

        roundIncrement.put(roundNumber, increment);
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

    public Duration increment(GameRound turn) {
        if (turn.phase == 1 && roundIncrement.containsKey(turn.roundNumber)) {
            return roundIncrement.get(turn.roundNumber);
        } else {
            return phaseIncrement.getOrDefault(turn.phase, Duration.ZERO);
        }
    }
}
