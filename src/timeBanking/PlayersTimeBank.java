package timeBanking;

import flextimer.timeConstraint.*;
import flextimer.turnFlow.*;

import java.time.*;
import java.util.*;

public class PlayersTimeBank implements TimeBank {
    private final HashMap<Player, Duration> bank = new HashMap<>();

    @Override
    public Duration getAccumulated(TimerTurn timerTurn) {
        return bank.getOrDefault(timerTurn.player, Duration.ZERO);
    }

    @Override
    public void saveRemainingDuration(TimerTurn timerTurn, Duration remaining) {
        bank.put(timerTurn.player, remaining);
    }
}
