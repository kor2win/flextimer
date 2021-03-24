package turnDurationCalculations;

import flextimer.turnFlow.*;

import java.time.*;

public interface ChessTurnDurationIncrementsReader {
    Duration increment(GameTurn turn);
}
