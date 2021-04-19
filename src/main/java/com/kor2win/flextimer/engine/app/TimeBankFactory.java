package com.kor2win.flextimer.engine.app;

import com.kor2win.flextimer.engine.timeConstraint.*;

import java.util.*;

public interface TimeBankFactory {
    TimeBank make(String type, Map<String, Object> arguments);

    Set<String> getTypes();
}
