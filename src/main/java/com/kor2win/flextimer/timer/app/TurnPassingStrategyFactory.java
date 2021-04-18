package com.kor2win.flextimer.timer.app;

import com.kor2win.flextimer.timer.turnFlow.*;

import java.util.*;

public interface TurnPassingStrategyFactory {
    TurnPassingStrategy make(String type, Map<String, Object> arguments);

    Set<String> getTypes();
}
