package com.gempukku.jam.libgdx.march2021.system.machine.condition;

import com.gempukku.jam.libgdx.march2021.component.InputControlledComponent;
import com.gempukku.jam.libgdx.march2021.system.TimeSystem;
import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerCondition;

public class WhenKeyPressedWithinCondition extends EngineTriggerCondition {
    private float time;
    private String[] keyMapping;

    @Override
    public boolean isTriggered() {
        float time = engine.getSystem(TimeSystem.class).getTimeProvider().getTime();
        InputControlledComponent inputControlled = entity.getComponent(InputControlledComponent.class);
        for (InputControlledComponent.InputEntry input : inputControlled.getInputs()) {
            if (time <= input.getTime() + this.time) {
                for (String key : keyMapping) {
                    if (input.getType().equals(key))
                        return true;
                }
            }
        }

        return false;
    }
}
