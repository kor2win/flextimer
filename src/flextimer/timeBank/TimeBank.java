package flextimer.timeBank;

import flextimer.player.Player;

import java.time.Duration;
import java.util.Hashtable;

public class TimeBank {
    private final Hashtable<Player, Duration> playerDuration = new Hashtable<>();

    public void playerTime(Player player, Duration duration) {
        playerDuration.put(player, duration);
    }

    public Duration remainingTime(Player player) {
        return playerDuration.getOrDefault(player, Duration.ZERO);
    }
}
