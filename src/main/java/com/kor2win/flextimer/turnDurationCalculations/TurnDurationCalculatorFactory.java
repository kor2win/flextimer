package com.kor2win.flextimer.turnDurationCalculations;

import com.kor2win.flextimer.engine.timeConstraint.*;

import java.time.*;
import java.util.*;

public class TurnDurationCalculatorFactory implements com.kor2win.flextimer.engine.app.TurnDurationCalculatorFactory {
    public static final String TYPE_SIMPLE = "simple";
    public static final String TYPE_CHESS = "chess";
    private static final Set<String> TYPES = new HashSet<>(Arrays.asList(TYPE_SIMPLE, TYPE_CHESS));

    @Override
    public TurnDurationCalculator make(String type, Map<String, Object> arguments) {
        if (TYPE_SIMPLE.equals(type)) {
            return buildSimple();
        } else if (TYPE_CHESS.equals(type)) {
            return tryBuildChess(arguments);
        }

        throw new UnknownCalculatorType(type);
    }

    private SimpleTurnDurationCalculator buildSimple() {
        return new SimpleTurnDurationCalculator();
    }

    private ChessTurnDurationCalculator tryBuildChess(Map<String, Object> attributes) {
        try {
            return buildChess(attributes);
        } catch (NegativeIncrement negativeIncrement) {
            throw new InvalidAttributesStructure(attributes, negativeIncrement);
        }
    }

    @SuppressWarnings("unchecked")
    private ChessTurnDurationCalculator buildChess(Map<String, Object> attributes) throws NegativeIncrement {
        ChessTurnDurationIncrements i = new ChessTurnDurationIncrements();

        if (attributes.containsKey("round_increments")) {
            Map<Integer, Duration> roundIncrements = (Map<Integer, Duration>) attributes.get("round_increments");

            for (Map.Entry<Integer, Duration> entry : roundIncrements.entrySet()) {
                i.setRoundIncrement(entry.getKey(), entry.getValue());
            }
        }

        if (attributes.containsKey("phase_increments")) {
            Map<Integer, Duration> phaseIncrements = (Map<Integer, Duration>) attributes.get("phase_increments");

            for (Map.Entry<Integer, Duration> entry : phaseIncrements.entrySet()) {
                i.setPhaseIncrement(entry.getKey(), entry.getValue());
            }
        }

        return new ChessTurnDurationCalculator(i);
    }

    @Override
    public Set<String> getTypes() {
        return TYPES;
    }
}
