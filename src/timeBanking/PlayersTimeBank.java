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
    public void saveRemaining(TimerTurn timerTurn, Duration remaining) {
        savePlayerRemaining(timerTurn.player, remaining);
    }

    public void savePlayerRemaining(Player player, Duration remaining) {
        bank.put(player, remaining);
    }
}
