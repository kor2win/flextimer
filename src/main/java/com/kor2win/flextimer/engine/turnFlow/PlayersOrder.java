package com.kor2win.flextimer.engine.turnFlow;

import java.util.*;

public class PlayersOrder {
    private final LinkedList<Player> players;

    public PlayersOrder(Player[] players) {
        this.players = new LinkedList<>(Arrays.asList(players));
    }

    public Player first() {
        return players.getFirst();
    }

    public Player last() {
        return players.getLast();
    }

    public Player after(Player player) {
        if (player.equals(last())) {
            return first();
        }

        Player prev = null;
        for (Player p : players) {
            if (prev != null && prev.equals(player)) {
                return p;
            }

            prev = p;
        }

        throw new UnknownPlayer(player);
    }

    public Player before(Player player) {
        if (player.equals(first())) {
            return last();
        }

        Player prev = null;
        for (Player p : players) {
            if (prev != null && p.equals(player)) {
                return prev;
            }

            prev = p;
        }

        throw new UnknownPlayer(player);
    }

    public int size() {
        return players.size();
    }

    public Player get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBounds();
        }

        return players.get(index);
    }
}
