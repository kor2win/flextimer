package com.kor2win.flextimer.engine.app;

import com.kor2win.flextimer.engine.turnFlow.*;

import java.util.*;

public interface TurnPassingStrategyFactory {
    TurnPassingStrategy make(String type, Map<String, Object> arguments);

    Set<String> getTypes();
}
