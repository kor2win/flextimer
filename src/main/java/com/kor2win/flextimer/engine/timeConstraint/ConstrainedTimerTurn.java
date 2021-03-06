package com.kor2win.flextimer.engine.timeConstraint;

import com.kor2win.flextimer.engine.turnFlow.*;
import com.kor2win.flextimer.engine.timer.*;
import com.kor2win.flextimer.engine.ui.*;

import java.time.*;

public class ConstrainedTimerTurn extends TurnKey implements TurnInfo {
    private final TimerTurn timerTurn;
    private final TimerClock timerClock;
    private final TimeConstraint timeConstraint;

    private Duration remaining;
    private Instant startTime;
    private boolean isPaused = true;
    private boolean isSuppressed = false;
    private boolean isEnded = false;

    public ConstrainedTimerTurn(TimerTurn timerTurn, TimerClock timerClock, TimeConstraint timeConstraint) {
        this.timerTurn = timerTurn;
        this.timerClock = timerClock;
        this.timeConstraint = timeConstraint;

        remaining = timeConstraint.turnDurationCalculator.totalTurnDuration(
                timerTurn.gameRound,
                timeConstraint.bankedRemaining(timerTurn)
        );

        if (remaining.isZero()) {
            depleteBank();
        }
    }

    private void depleteBank() {
        isEnded = true;
        timeConstraint.deplete();
        remaining = Duration.ZERO;
        timeConstraint.timeBank.saveRemaining(timerTurn, Duration.ZERO);
    }

    public void start() {
        if (isGoing() || isEnded()) {
            return;
        }

        syncRemaining();
        isPaused = false;

        if (isGoing()) {
            startTime = timerClock.instant();
        }

        timeConstraint.observableTimer.notifyTurnStarted(this);
    }

    public void pause() {
        if (isEnded()) {
            return;
        }

        syncRemaining();
        isPaused = true;

        timeConstraint.observableTimer.notifyTurnPaused(this);
    }

    public void end() {
        if (isEnded()) {
            return;
        }

        syncRemaining();
        isEnded = true;

        timeConstraint.observableTimer.notifyTurnEnded(this);
    }

    protected void suppress() {
        if (isEnded()) {
            return;
        }

        syncRemaining();
        isSuppressed = true;

        timeConstraint.observableTimer.notifyTurnSuppressed(this);
    }

    protected void resume() {
        if (isEnded()) {
            return;
        }

        isSuppressed = false;

        if (isGoing()) {
            startTime = timerClock.instant();
        }

        timeConstraint.observableTimer.notifyTurnResumed(this);
    }

    private void syncRemaining() {
        remaining = remaining();
        timeConstraint.timeBank.saveRemaining(timerTurn, remaining);

        if (isEnoughToDepleteTimer()) {
            timeConstraint.deplete();
            isEnded = true;
        }
    }

    private boolean isEnoughToDepleteTimer() {
        if (!remaining.isZero()) {
            return false;
        }

        if (timeConstraint.depleteOnZeroRemaining()) {
            return true;
        } else {
            TimerTurn futureTurn = nextTurnOfSamePlayer();
            Duration futureDuration = timeConstraint.turnDurationCalculator.totalTurnDuration(futureTurn.gameRound, Duration.ZERO);

            return futureDuration.isZero();
        }
    }

    private TimerTurn nextTurnOfSamePlayer() {
        return timeConstraint.futureTurnAccess.nextTurnOfSamePlayer(timerTurn);
    }

    public Duration remaining() {
        Duration elapsed = isGoing()
                ? Duration.between(startTime, timerClock.instant())
                : Duration.ZERO;

        return remaining.minus(elapsed);
    }

    @Override
    public boolean isGoing() {
        return !isPaused && !isSuppressed && !isEnded;
    }

    @Override
    public boolean isEnded() {
        return isEnded;
    }

    @Override
    public TimerTurn timerTurn() {
        return timerTurn;
    }

    @Override
    public GameRound gameTurn() {
        return timerTurn.gameRound;
    }

    @Override
    public int roundNumber() {
        return timerTurn.gameRound.roundNumber;
    }

    @Override
    public int phase() {
        return timerTurn.gameRound.phase;
    }

    public Player player() {
        return timerTurn.player;
    }
}
