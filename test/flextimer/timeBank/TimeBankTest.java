package flextimer.timeBank;

import flextimer.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TimeBankTest {
    private static Player player_one;
    private static Player player_two;

    private TimeBank bank;

    @BeforeAll
    public static void setUpPlayer() {
        player_one = new Player("Anton", 0x00FF00);
        player_two = new Player("Max", 0xFF0000);
    }

    @BeforeEach
    public void setUp() {
        bank = new TimeBank();
    }

    @Test
    public void canStoreSeveralPlayersTime() throws TimeBankException {
        final int s1 = 5;
        final int s2 = 7;

        bank.playerTime(player_one, Duration.ofSeconds(s1));
        bank.playerTime(player_two, Duration.ofSeconds(s2));

        assertEquals(Duration.ofSeconds(s1), bank.remainingTime(player_one));
        assertEquals(Duration.ofSeconds(s2), bank.remainingTime(player_two));
    }

    @Test
    public void whenUnknownPlayerRequested_thenExceptionThrown() {
        assertThrows(UnknownPlayer.class, () -> bank.remainingTime(player_one));
    }
}
