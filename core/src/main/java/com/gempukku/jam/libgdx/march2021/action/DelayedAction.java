package com.gempukku.jam.libgdx.march2021.action;

import com.badlogic.ashley.core.Engine;
import com.gempukku.jam.libgdx.march2021.system.TimeSystem;

public class DelayedAction extends ScheduledAction {
    public DelayedAction(Engine engine, float time, Runnable runnable) {
        super(engine, engine.getSystem(TimeSystem.class).getTimeProvider().getTime() + time, runnable);
    }
}
