package com.kor2win.flextimer.turnDurationCalculations;

import com.kor2win.flextimer.timer.turnFlow.*;

import java.time.*;

public interface ChessTurnDurationIncrementsReader {
    Duration increment(GameRound turn);
}
