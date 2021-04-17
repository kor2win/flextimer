package com.kor2win.flextimer.timeBanking;

import com.kor2win.flextimer.timer.turnFlow.*;
import com.kor2win.flextimer.timer.timeConstraint.*;
import org.junit.jupiter.api.*;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlayersTimeBankTest {
    private static TimerTurn t1p1;
    private static TimerTurn t1p2;
    private static TimerTurn t2p1;

    private TimeBank bank;

    @BeforeAll
    public static void setUpTurns() {
        Player p1 = new Player("Anton");
        Player p2 = new Player("Max");
        GameRound t1 = GameRound.FIRST;
        GameRound t2 = new GameRound(2, 1);
        t1p1 = new TimerTurn(t1, p1);
        t1p2 = new TimerTurn(t1, p2);
        t2p1 = new TimerTurn(t2, p1);
    }

    @BeforeEach
    public void setUp() {
        bank = new PlayersTimeBank();
    }

    @Test
    public void canStoreSeveralPlayersTime() {
        final int s1 = 5;
        final int s2 = 7;

        bank.saveRemaining(t1p1, Duration.ofSeconds(s1));
        bank.saveRemaining(t1p2, Duration.ofSeconds(s2));

        assertEquals(Duration.ofSeconds(s1), bank.getAccumulated(t1p1));
        assertEquals(Duration.ofSeconds(s2), bank.getAccumulated(t1p2));
    }

    @Test
    public void zeroDurationForUnknownPlayer() {
        assertEquals(Duration.ZERO, bank.getAccumulated(t1p1));
    }

    @Test
    public void durationNotDependsOnGameTurn() {
        final int s1 = 5;

        bank.saveRemaining(t1p1, Duration.ofSeconds(s1));

        assertEquals(Duration.ofSeconds(s1), bank.getAccumulated(t2p1));
    }
}
