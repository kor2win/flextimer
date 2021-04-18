package com.kor2win.flextimer.timeBanking;

import com.kor2win.flextimer.timer.timeConstraint.*;

import java.util.*;

public class TimeBankFactory implements com.kor2win.flextimer.timer.app.TimeBankFactory {
    public static final String TYPE_PLAYERS = "players";

    private static final Set<String> TYPES = new HashSet<>(Collections.singletonList(TYPE_PLAYERS));

    @Override
    public TimeBank make(String type, Map<String, Object> arguments) {
        if (TYPE_PLAYERS.equals(type)) {
            return new PlayersTimeBank();
        }

        throw new UnknownBankType();
    }

    @Override
    public Set<String> getTypes() {
        return TYPES;
    }
}
