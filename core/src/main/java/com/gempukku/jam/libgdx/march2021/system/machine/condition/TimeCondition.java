package com.gempukku.jam.libgdx.march2021.system.machine.condition;

import com.gempukku.jam.libgdx.march2021.system.TimeSystem;
import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerCondition;
import com.gempukku.libgdx.graph.time.TimeProvider;

public class TimeCondition extends EngineTriggerCondition {
    private float timerStart = 0;

    private float time;

    @Override
    public void reset() {
        timerStart = getTimeProvider().getTime();
    }

    private TimeProvider getTimeProvider() {
        return engine.getSystem(TimeSystem.class).getTimeProvider();
    }

    @Override
    public boolean isTriggered() {
        return getTimeProvider().getTime() >= timerStart + time;
    }
}
