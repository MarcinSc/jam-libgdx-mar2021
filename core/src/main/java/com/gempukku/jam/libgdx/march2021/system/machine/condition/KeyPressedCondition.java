package com.gempukku.jam.libgdx.march2021.system.machine.condition;

import com.gempukku.jam.libgdx.march2021.component.InputControlledComponent;
import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerCondition;

public class KeyPressedCondition extends EngineTriggerCondition {
    private String keyState;

    @Override
    public boolean isTriggered() {
        InputControlledComponent inputControlled = entity.getComponent(InputControlledComponent.class);
        return inputControlled.getKeyState(keyState);
    }
}
