package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.EntitySystem;
import com.gempukku.libgdx.graph.time.DefaultTimeKeeper;
import com.gempukku.libgdx.graph.time.TimeProvider;

public class TimeSystem extends EntitySystem {
    private DefaultTimeKeeper timeKeeper = new DefaultTimeKeeper();

    public TimeSystem(int priority) {
        super(priority);
    }

    @Override
    public void update(float deltaTime) {
        timeKeeper.updateTime(deltaTime);
    }

    public TimeProvider getTimeProvider() {
        return timeKeeper;
    }
}
