package flextimer.exception;

import flextimer.Player;

public class UnknownPlayer extends TimeBankException {
    public final Player player;

    public UnknownPlayer(Player player) {
        super();

        this.player = player;
    }
}
