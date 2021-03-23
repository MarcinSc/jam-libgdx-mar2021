package com.gempukku.jam.libgdx.march2021.action;

import com.gempukku.libgdx.graph.time.TimeProvider;

public class ScheduledAction implements Action {
    private TimeProvider timeProvider;
    private float time;
    private Runnable runnable;

    public ScheduledAction(TimeProvider timeProvider, float time, Runnable runnable) {
        this.timeProvider = timeProvider;
        this.time = time;
        this.runnable = runnable;
    }

    @Override
    public boolean act() {
        if (timeProvider.getTime() >= time) {
            runnable.run();
            return true;
        }
        return false;
    }
}
