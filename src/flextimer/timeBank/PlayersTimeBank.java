package flextimer.timeBank;

import flextimer.player.Player;
import flextimer.turnFlow.util.TimerTurn;

import java.time.Duration;
import java.util.HashMap;

public class PlayersTimeBank implements TimeBank{
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
