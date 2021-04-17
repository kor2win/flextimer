package com.kor2win.flextimer.timer.turnFlow;

public class Player {
    public final String name;
    private final int hashCode;

    public Player(String name) {
        this.name = name;
        hashCode = calculateHashCode();
    }

    public boolean equals(Player p) {
        return name.equals(p.name);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player
                ? this.equals((Player) obj)
                : super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private int calculateHashCode() {
        return name.hashCode();
    }
}
