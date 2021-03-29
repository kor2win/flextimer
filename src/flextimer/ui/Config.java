package flextimer.ui;

import flextimer.timeConstraint.*;
import flextimer.turnFlow.*;

public class Config implements TimerConfig, TimeConstraintConfig, TurnFlowConfig {
    private boolean pauseOnTurnPass = false;
    private boolean depleteOnZeroRemaining = false;
    private PlayersOrder playersOrder = new PlayersOrder(new Player[]{});
    private int phasesCount = 1;

    @Override
    public boolean pauseOnTurnPass() {
        return pauseOnTurnPass;
    }

    @Override
    public boolean depleteOnZeroRemaining() {
        return depleteOnZeroRemaining;
    }

    @Override
    public PlayersOrder playersOrder() {
        return playersOrder;
    }

    @Override
    public int phasesCount() {
        return phasesCount;
    }

    public void setPauseOnTurnPass(boolean pauseOnTurnPass) {
        this.pauseOnTurnPass = pauseOnTurnPass;
    }

    public void setDepleteOnZeroRemaining(boolean depleteOnZeroRemaining) {
        this.depleteOnZeroRemaining = depleteOnZeroRemaining;
    }

    public void setPlayersOrder(PlayersOrder playersOrder) {
        this.playersOrder = playersOrder;
    }

    public void setPhasesCount(int phasesCount) {
        this.phasesCount = phasesCount;
    }
}
