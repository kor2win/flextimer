package com.kor2win.flextimer.timer.app;

import com.kor2win.flextimer.timer.timeConstraint.*;

import java.util.*;

public interface TimeBankFactory {
    TimeBank make(String type, Map<String, Object> arguments);

    Set<String> getTypes();
}
