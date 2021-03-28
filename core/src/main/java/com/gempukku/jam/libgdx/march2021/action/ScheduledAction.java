package com.gempukku.jam.libgdx.march2021.action;

import com.badlogic.ashley.core.Engine;
import com.gempukku.jam.libgdx.march2021.system.TimeSystem;

public class ScheduledAction implements Action {
    private Engine engine;
    private float time;
    private Runnable runnable;

    public ScheduledAction(Engine engine, float time, Runnable runnable) {
        this.engine = engine;
        this.time = time;
        this.runnable = runnable;
    }

    @Override
    public boolean act() {
        if (engine.getSystem(TimeSystem.class).getTimeProvider().getTime() >= time) {
            runnable.run();
            return true;
        }
        return false;
    }
}
