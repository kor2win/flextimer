package com.kor2win.flextimer.engine.app;

import com.kor2win.flextimer.engine.timeConstraint.*;
import com.kor2win.flextimer.engine.turnFlow.*;
import com.kor2win.flextimer.engine.timer.*;
import com.kor2win.flextimer.engine.timer.Timer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimerBuilderTest {
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
                .createTimeBank("b")
                .createTurnDurationCalculator("c")
                .createTurnPassingStrategy("s")
                .build();

        assertNotNull(t);
    }

    @Test
    public void shortConfigCourse() {
        setUpFactories();

        Config config = mock(Config.class);

        Timer t = timerBuilder
                .setConfig(config)
                .createTimeBank("b")
                .createTurnDurationCalculator("c")
                .createTurnPassingStrategy("s")
                .build();

        assertNotNull(t);
    }

    @Test
    public void incompleteTimer() {
        assertThrows(BuilderNotComplete.class, timerBuilder::build);
    }
}