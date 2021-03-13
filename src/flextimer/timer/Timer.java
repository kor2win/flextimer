package flextimer.timer;

import flextimer.exception.PauseWhenPaused;
import flextimer.exception.StartWhenStarted;
import flextimer.player.UnknownPlayer;
import flextimer.timeBank.TimeBank;
import flextimer.timerTurnFlow.util.TimerTurn;
import flextimer.timerTurnFlow.TimerTurnFlow;
import flextimer.turnDurationFlow.TurnDurationFlow;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class Timer {
    protected Clock clock = Clock.system(ZoneId.systemDefault());

    private final TimerTurnFlow timerTurnFlow;
    private final TurnDurationFlow durationFlow;
    private final TimeBank timeBank;

    private boolean isStarted = false;
    private Instant turnStartTime = null;
    private Duration currentTurnRemaining;

    public Timer(TimerTurnFlow timerTurnFlow, TurnDurationFlow durationFlow, TimeBank timeBank) {
        this.timerTurnFlow = timerTurnFlow;
        this.durationFlow = durationFlow;
        this.timeBank = timeBank;

        initRemainingOnStart();
    }

    public void start() throws StartWhenStarted {
        if (isStarted) {
            throw new StartWhenStarted();
        }

        isStarted = true;
        turnStartTime = clock.instant();
    }

    public void passTurn() throws UnknownPlayer {
        timeBank.playerTime(currentTurn().player, remainingDuration());
        timerTurnFlow.passTurn();

        turnStartTime = clock.instant();
        initRemainingOnStart();
    }

    private void initRemainingOnStart() {
        currentTurnRemaining = durationFlow.remainingAfterTurnStart(
                currentTurn().gameTurn,
                timeBank.remainingTime(currentTurn().player)
        );
    }

    public void pause() throws PauseWhenPaused {
        if (!isStarted) {
            throw new PauseWhenPaused();
        }

        isStarted = false;
        currentTurnRemaining = remainingDuration();
        turnStartTime = null;
    }

    public TimerTurn currentTurn() {
        return timerTurnFlow.timerTurn();
    }

    public Duration remainingDuration() {
        Duration elapsed = turnStartTime != null
                ? Duration.between(turnStartTime, clock.instant())
                : Duration.ZERO;

        return currentTurnRemaining.minus(elapsed);
    }
}

