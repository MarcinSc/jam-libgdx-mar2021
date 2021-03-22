package com.gempukku.jam.libgdx.march2021.system.machine.condition;

import com.badlogic.gdx.utils.Array;
import com.gempukku.jam.libgdx.march2021.system.machine.ContainerTriggerCondition;
import com.gempukku.libgdx.lib.fst.TriggerCondition;

public class OrCondition implements ContainerTriggerCondition {
    private Array<TriggerCondition> conditions;

    @Override
    public void reset() {
        for (TriggerCondition condition : conditions) {
            condition.reset();
        }
    }

    @Override
    public boolean isTriggered() {
        for (TriggerCondition condition : conditions) {
            if (condition.isTriggered())
                return true;
        }
        return false;
    }

    @Override
    public Iterable<TriggerCondition> getChildren() {
        return conditions;
    }
}
