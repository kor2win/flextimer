package com.kor2win.flextimer.timer.app;

import com.kor2win.flextimer.timer.timeConstraint.*;
import com.kor2win.flextimer.timer.turnFlow.*;
import com.kor2win.flextimer.timer.ui.*;
import com.kor2win.flextimer.timer.ui.Timer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimerBuilderTest {
    private static final Map<String, Object> ARGS_EMPTY = new HashMap<>();

    private TimerBuilder timerBuilder;

    @Mock private TimeBankFactory timeBankFactory;
    @Mock private TurnDurationCalculatorFactory turnDurationCalculatorFactory;
    @Mock private TurnPassingStrategyFactory turnPassingStrategyFactory;

    @BeforeEach
    public void setUp() {
        timerBuilder = new TimerBuilder(timeBankFactory, turnDurationCalculatorFactory, turnPassingStrategyFactory);
    }

    private void setUpFactories() {
        TimeBank timeBank = mock(TimeBank.class);
        TurnDurationCalculator turnDurationCalculator = mock(TurnDurationCalculator.class);
        TurnPassingStrategy turnPassingStrategy = mock(TurnPassingStrategy.class);

        when(timeBankFactory.make(anyString(), any())).thenReturn(timeBank);
        when(turnDurationCalculatorFactory.make(anyString(), any())).thenReturn(turnDurationCalculator);
        when(turnPassingStrategyFactory.make(anyString(), any())).thenReturn(turnPassingStrategy);
    }

    @Test
    public void normalCourse() {
        setUpFactories();

        TimerConfig timerConfig = mock(TimerConfig.class);
        TimeConstraintConfig timeConstraintConfig = mock(TimeConstraintConfig.class);
        TurnFlowConfig turnFlowConfig = mock(TurnFlowConfig.class);

        Timer t = timerBuilder
                .setTimerConfig(timerConfig)
                .setTimeConstraintConfig(timeConstraintConfig)
                .setTurnFlowConfig(turnFlowConfig)
                .createTimeBank("b", ARGS_EMPTY)
                .createTurnDurationCalculator("c", ARGS_EMPTY)
                .createTurnPassingStrategyCalculator("s", ARGS_EMPTY)
                .build();

        assertNotNull(t);
    }

    @Test
    public void shortConfigCourse() {
        setUpFactories();

        Config config = mock(Config.class);

        Timer t = timerBuilder
                .setConfig(config)
                .createTimeBank("b", ARGS_EMPTY)
                .createTurnDurationCalculator("c", ARGS_EMPTY)
                .createTurnPassingStrategyCalculator("s", ARGS_EMPTY)
                .build();

        assertNotNull(t);
    }

    @Test
    public void incompleteTimer() {
        assertThrows(BuilderNotComplete.class, timerBuilder::build);
    }
}