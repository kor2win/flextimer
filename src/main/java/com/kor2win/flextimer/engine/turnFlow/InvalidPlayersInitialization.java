package com.kor2win.flextimer.engine.turnFlow;

public class InvalidPlayersInitialization extends PlayersException {
    public InvalidPlayersInitialization() {
        super();
    }

    public InvalidPlayersInitialization(String message) {
        super(message);
    }

    public InvalidPlayersInitialization(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPlayersInitialization(Throwable cause) {
        super(cause);
    }
}
