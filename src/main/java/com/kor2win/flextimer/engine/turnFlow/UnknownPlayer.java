package com.kor2win.flextimer.engine.turnFlow;


public class UnknownPlayer extends PlayersException {
    public final Player player;

    public UnknownPlayer(Player player, Throwable cause) {
        super(cause);

        this.player = player;
    }

    public UnknownPlayer(Player player) {
        super();

        this.player = player;
    }
}
