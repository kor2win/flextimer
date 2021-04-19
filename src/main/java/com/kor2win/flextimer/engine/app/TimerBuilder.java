package com.kor2win.flextimer.engine.app;

import com.kor2win.flextimer.engine.timeConstraint.*;
import com.kor2win.flextimer.engine.turnFlow.*;
import com.kor2win.flextimer.engine.timer.*;
import com.kor2win.flextimer.engine.timer.Timer;

import java.util.*;

public class TimerBuilder {
    private final TimeBankFactory bankFactory;
    private final TurnDurationCalculatorFactory calculatorFactory;
    private final TurnPassingStrategyFactory turnPassingStrategyFactory;

    private TimerConfig timerConfig;
    private TimeConstraintConfig timeConstraintConfig;
    private TurnFlowConfig turnFlowConfig;
    private TimeBank timeBank;
    private TurnDurationCalculator turnDurationCalculator;
    private TurnPassingStrategy turnPassingStrategy;

    public TimerBuilder(
            TimeBankFactory bankFactory,
            TurnDurationCalculatorFactory calculatorFactory,
            TurnPassingStrategyFactory turnPassingStrategyFactory
    ) {
        this.bankFactory = bankFactory;
        this.calculatorFactory = calculatorFactory;
        this.turnPassingStrategyFactory = turnPassingStrategyFactory;
    }

    public TimerBuilder setConfig(Config config) {
        this.timerConfig = config;
        this.timeConstraintConfig = config;
        this.turnFlowConfig = config;

        return this;
    }

    public TimerBuilder setTimerConfig(TimerConfig config) {
        this.timerConfig = config;

        return this;
    }

    public TimerBuilder setTimeConstraintConfig(TimeConstraintConfig config) {
        this.timeConstraintConfig = config;

        return this;
    }

    public TimerBuilder setTurnFlowConfig(TurnFlowConfig config) {
        this.turnFlowConfig = config;

        return this;
    }

    public TimerBuilder createTimeBank(String type, Map<String, Object> arguments) {
        this.timeBank = bankFactory.make(type, arguments);

        return this;
    }

    public TimerBuilder createTurnDurationCalculator(String type, Map<String, Object> arguments) {
        this.turnDurationCalculator = calculatorFactory.make(type, arguments);

        return this;
    }

    public TimerBuilder createTurnPassingStrategy(String type, Map<String, Object> arguments) {
        this.turnPassingStrategy = turnPassingStrategyFactory.make(type, arguments);

        return this;
    }

    public Timer build() {
        validateIsBuildComplete();

        TurnFlow turnFlow = new TurnFlow(turnPassingStrategy, turnFlowConfig);
        TimeConstraint timeConstraint = new TimeConstraint(turnDurationCalculator, timeBank, turnFlow, timeConstraintConfig);

        return new Timer(turnFlow, timeConstraint, timerConfig);
    }

    private void validateIsBuildComplete() {
        if (
                timerConfig == null
                || timeConstraintConfig == null
                || turnFlowConfig == null
                || turnPassingStrategy == null
                || turnDurationCalculator == null
                || timeBank == null
        ) {
            throw new BuilderNotComplete();
        }
    }
}
