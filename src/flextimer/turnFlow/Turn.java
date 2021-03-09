package flextimer.turnFlow;

import flextimer.Player;

public interface Turn {
    int number();
    int phase();
    Player player();
}
