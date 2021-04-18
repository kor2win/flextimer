package com.kor2win.flextimer.turnPassingStrategies;

import com.kor2win.flextimer.timer.turnFlow.*;

import java.util.*;

public class TurnPassingStrategyFactory implements com.kor2win.flextimer.timer.app.TurnPassingStrategyFactory {
    public static final String TYPE_STRAIGHT = "straight";
    public static final String TYPE_ROTATING = "rotating";
    private static final Set<String> TYPES = new HashSet<>(Arrays.asList(TYPE_STRAIGHT, TYPE_ROTATING));

    @Override
    public TurnPassingStrategy make(String type, Map<String, Object> arguments) {
        if (TYPE_STRAIGHT.equals(type)) {
            return buildStraight(arguments);
        } else if (TYPE_ROTATING.equals(type)) {
            return buildRotating(arguments);
        }

        throw new UnknownTurnPassingStrategyType(type);
    }

    private TurnPassingStrategy buildStraight(Map<String, Object> attributes) {
        if (attributes.containsKey("simultaneous_until")) {
            return new StraightTurnPassingStrategy((GameRound) attributes.get("simultaneous_until"));
        } else {
            return new StraightTurnPassingStrategy();
        }
    }

    private TurnPassingStrategy buildRotating(Map<String, Object> attributes) {
        if (attributes.containsKey("simultaneous_until")) {
            return new RotatingTurnPassingStrategy((GameRound) attributes.get("simultaneous_until"));
        } else {
            return new RotatingTurnPassingStrategy();
        }
    }

    @Override
    public Set<String> getTypes() {
        return TYPES;
    }
}
