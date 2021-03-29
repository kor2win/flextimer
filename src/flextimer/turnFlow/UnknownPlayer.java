package flextimer.turnFlow;


public class UnknownPlayer extends TurnFlowException {
    public final Player player;

    public UnknownPlayer(Player player) {
        super();

        this.player = player;
    }
}
