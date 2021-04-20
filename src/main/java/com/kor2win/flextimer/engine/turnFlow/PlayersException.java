package com.kor2win.flextimer.engine.turnFlow;

public abstract class PlayersException extends RuntimeException {
    public PlayersException() {
        super();
    }

    public PlayersException(String message) {
        super(message);
    }

    public PlayersException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayersException(Throwable cause) {
        super(cause);
    }
}
