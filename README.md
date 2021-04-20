# Flextimer
Timer engine for board games. Behaviour aspects can be developed independently and used in any combination:
- turn sequencing - in what order players play their moves. Extends `com.kor2win.flextimer.engine.turnFlow.TurnPassingStrategy`
- time banking - the way player's time stored. Implements `com.kor2win.flextimer.engine.timeConstraint.TimeBank`
- time control - the way player's time changed. Implements `com.kor2win.flextimer.engine.timeConstraint.TurnDurationCalculator`

### Getting started
Some simple usage example:

```java

import com.kor2win.flextimer.engine.app.*;
import com.kor2win.flextimer.engine.timeConstraint.*;
import com.kor2win.flextimer.engine.timer.Timer;
import com.kor2win.flextimer.engine.turnFlow.*;
import com.kor2win.flextimer.timeBanking.TimeBankFactory;
import com.kor2win.flextimer.turnDurationCalculations.TurnDurationCalculatorFactory;
import com.kor2win.flextimer.turnPassingStrategies.TurnPassingStrategyFactory;

import java.util.*;

abstract class MyTimer {
    public void main(String[] args) throws Exception {
        Timer timer = buildTimer();

        while (true) {
            ConstrainedSimultaneousTurns turns = timer.getCurrentSimultaneousTurns();

            for (int i = 0; i < turns.size(); i++) {
                ConstrainedTimerTurn turn = turns.get(i);
                runTurnHandling(turn);
            }

            timer.passSimultaneousTurns();
        }
    }

    private void runTurnHandling(ConstrainedTimerTurn turn) {
        while (true) {
            SomeSignal signal = readInputSignal();

            if (signal == SomeSignal.PASS_TURN) {
                turn.end();

                break;
            } else if (signal == SomeSignal.PAUSE) {
                turn.pause();
            } else if (signal == SomeSignal.RESUME) {
                turn.start();
            }
        }
    }

    abstract protected SomeSignal readInputSignal();

    private Timer buildTimer() {
        TimerBuilder timerBuilder = new TimerBuilder(
                new TimeBankFactory(),
                new TurnDurationCalculatorFactory(),
                new TurnPassingStrategyFactory()
        );

        PlayersOrder playersOrder = buildPlayersOrder();
        Config config = buildConfig(playersOrder);

        return timerBuilder
                .setConfig(config)
                .createTimeBank(TimeBankFactory.TYPE_PLAYERS)
                .createTurnDurationCalculator(TurnDurationCalculatorFactory.TYPE_SIMPLE)
                .createTurnPassingStrategy(TurnPassingStrategyFactory.TYPE_STRAIGHT)
                .build();
    }

    private Config buildConfig(PlayersOrder playersOrder) {
        Config config = new Config();

        config.setDepleteOnZeroRemaining(true);
        config.setPauseOnTurnPass(true);
        config.setPlayersOrder(playersOrder);
        config.setPhasesCount(3);

        return config;
    }

    private PlayersOrder buildPlayersOrder() {
        return new PlayersOrder(Arrays.asList(
                new Player("Anton"),
                new Player("Max")
        ));
    }
}

enum SomeSignal {
    PASS_TURN,
    PAUSE,
    RESUME
}
```