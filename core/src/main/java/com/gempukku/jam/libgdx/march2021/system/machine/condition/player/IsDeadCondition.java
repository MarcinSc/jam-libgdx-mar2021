package com.gempukku.jam.libgdx.march2021.system.machine.condition.player;

import com.gempukku.jam.libgdx.march2021.component.PlayerComponent;
import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerCondition;

public class IsDeadCondition extends EngineTriggerCondition {
    @Override
    public boolean isTriggered() {
        return entity.getComponent(PlayerComponent.class).isDead();
    }
}
