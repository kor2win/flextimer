# Flextimer
Timer engine for board games. Behaviour aspects can be developed independently and used in any combination:
- turn sequencing - in what order players play their moves. Extends `flextimer.turnFlow.TurnPassingStrategy`
- time banking - the way player's time stored. Implements `flextimer.timeConstraint.TimeBank`
- time control - the way player's time changed. Implements `flextimer.timeConstraint.TurnDurationCalculator`

### Getting started
Some simple usage example:

```java
import flextimer.timeConstraint.*;
import flextimer.turnFlow.*;
import flextimer.ui.*;
import timeBanking.*;
import turnDurationCalculations.*;
import turnPassingStrategies.*;

import java.time.*;

abstract class MyTimer {
    public void main(String[] args) throws Exception {
        Timer timer = buildTimer();

        while (true) {
            ConstrainedSimultaneousTurns turns = timer.getConstrainedSimultaneousTurns();

            for (int i = 0; i < turns.size(); i++) {
                ConstrainedTimerTurn turn = turns.get(i);
                runTurnHandling(turn);
            }

            timer.passSimultaneousTurns();
        }
    }

    private void runTurnHandling(ConstrainedTimerTurn turn) {
        while (true) {
            SomeSignal input = readInputFromPlayer();

            if (input.isPassTurn()) {
                turn.end();

                break;
            } else if (input.isPause()) {
                turn.pause();
            } else if (input.isResume()) {
                turn.start();
            }
        }
    }

    abstract protected SomeSignal readInputFromPlayer();

    private Timer buildTimer() throws IndexOutOfBounds {
        PlayersOrder playersOrder = buildPlayersOrder();

        Config config = buildConfig(playersOrder);

        TurnPassingStrategy turnPassingStrategy = new StraightTurnPassingStrategy();

        TurnFlow turnFlow = new TurnFlow(turnPassingStrategy, config);


        TurnDurationCalculator turnDurationCalculator = new SimpleTurnDurationCalculator();
        PlayersTimeBank timeBank = buildTimeBank(playersOrder);

        TimeConstraint timeConstraint = new TimeConstraint(turnDurationCalculator, timeBank, turnFlow, config);

        return new Timer(turnFlow, timeConstraint, config);
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
        return new PlayersOrder(new Player[] {
                new Player("Anton"),
                new Player("Max")
        });
    }

    private PlayersTimeBank buildTimeBank(PlayersOrder playersOrder) throws IndexOutOfBounds {
        PlayersTimeBank timeBank = new PlayersTimeBank();
        Duration total = Duration.ofMinutes(10);

        for (int i = 0; i < playersOrder.size(); i++) {
            timeBank.savePlayerRemaining(playersOrder.get(i), total);
        }

        return timeBank;
    }
}

interface SomeSignal {
    boolean isPassTurn();

    boolean isPause();

    boolean isResume();
}
```