package com.kor2win.flextimer.turnPassingStrategies;

public class UnknownTurnPassingStrategyType extends RuntimeException {
    private final String type;

    public UnknownTurnPassingStrategyType(String type, Throwable cause) {
        super(cause);

        this.type = type;
    }

    public UnknownTurnPassingStrategyType(String type) {
        super();

        this.type = type;
    }

    public String getType() {
        return type;
    }
}
