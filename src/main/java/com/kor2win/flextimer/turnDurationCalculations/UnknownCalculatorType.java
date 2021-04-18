package com.kor2win.flextimer.turnDurationCalculations;

public class UnknownCalculatorType extends RuntimeException {
    private final String type;

    public UnknownCalculatorType(String type, Throwable cause) {
        super(cause);

        this.type = type;
    }

    public UnknownCalculatorType(String type) {
        super();

        this.type = type;
    }

    public String getType() {
        return type;
    }
}
