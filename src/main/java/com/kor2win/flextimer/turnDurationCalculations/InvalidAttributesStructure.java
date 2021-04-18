package com.kor2win.flextimer.turnDurationCalculations;

import java.util.*;

public class InvalidAttributesStructure extends RuntimeException {
    private Map<String, Object> attributes;

    public InvalidAttributesStructure(Map<String, Object> attributes, Throwable cause) {
        super(cause);

        this.attributes = attributes;
    }

    public InvalidAttributesStructure(Map<String, Object> attributes) {
        super();

        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
