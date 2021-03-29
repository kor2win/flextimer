package flextimer.ui;

import flextimer.timeConstraint.*;

public class Config implements TimerConfig, TimeConstraintConfig {
    private boolean pauseOnTurnPass = false;
    private boolean depleteOnZeroRemaining = false;

    @Override
    public boolean pauseOnTurnPass() {
        return pauseOnTurnPass;
    }

    @Override
    public boolean depleteOnZeroRemaining() {
        return depleteOnZeroRemaining;
    }

    public void setPauseOnTurnPass(boolean pauseOnTurnPass) {
        this.pauseOnTurnPass = pauseOnTurnPass;
    }

    public void setDepleteOnZeroRemaining(boolean depleteOnZeroRemaining) {
        this.depleteOnZeroRemaining = depleteOnZeroRemaining;
    }
}
