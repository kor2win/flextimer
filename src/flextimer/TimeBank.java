package flextimer;

import flextimer.exception.UnknownPlayer;

import java.time.Duration;
import java.util.HashMap;

public class TimeBank {
    private final HashMap<Player, Duration> playerDuration = new HashMap<>();

    public void playerTime(Player player, Duration duration) {
        playerDuration.put(player, duration);
    }

    public Duration remainingTime(Player player) throws UnknownPlayer {
        if (!playerDuration.containsKey(player)) {
            throw new UnknownPlayer(player);
        }

        return playerDuration.get(player);
    }
}
