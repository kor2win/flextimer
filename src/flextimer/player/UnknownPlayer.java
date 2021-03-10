package flextimer.player;

public class UnknownPlayer extends PlayerException {
    public final Player player;

    public UnknownPlayer(Player player) {
        super();

        this.player = player;
    }
}
