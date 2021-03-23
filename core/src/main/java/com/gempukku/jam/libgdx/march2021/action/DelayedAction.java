package com.gempukku.jam.libgdx.march2021.action;

import com.gempukku.libgdx.graph.time.TimeProvider;

public class DelayedAction extends ScheduledAction {
    public DelayedAction(TimeProvider timeProvider, float time, Runnable runnable) {
        super(timeProvider, timeProvider.getTime() + time, runnable);
    }
}
