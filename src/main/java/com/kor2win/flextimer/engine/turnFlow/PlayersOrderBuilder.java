package com.kor2win.flextimer.engine.turnFlow;

import java.util.*;

public class PlayersOrderBuilder {
    private final List<Player> players = new ArrayList<>();

    public PlayersOrderBuilder add(Player p) {
        players.add(p);

        return this;
    }

    public PlayersOrder build() {
        return new PlayersOrder(players);
    }
}
