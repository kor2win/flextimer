package com.kor2win.flextimer.timer.turnFlow;


public class UnknownPlayer extends TurnFlowException {
    public final Player player;

    public UnknownPlayer(Player player) {
        super();

        this.player = player;
    }
}
