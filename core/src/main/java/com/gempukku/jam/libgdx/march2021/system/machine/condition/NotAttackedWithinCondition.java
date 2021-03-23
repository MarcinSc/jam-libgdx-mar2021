package com.gempukku.jam.libgdx.march2021.system.machine.condition;

import com.gempukku.jam.libgdx.march2021.component.AttackerComponent;
import com.gempukku.jam.libgdx.march2021.system.TimeSystem;
import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerCondition;

public class NotAttackedWithinCondition extends EngineTriggerCondition {
    private float time;

    @Override
    public boolean isTriggered() {
        AttackerComponent attacker = entity.getComponent(AttackerComponent.class);
        float lastAttacked = attacker.getLastAttacked();
        float currentTime = engine.getSystem(TimeSystem.class).getTimeProvider().getTime();
        return lastAttacked <= currentTime - time;
    }
}
