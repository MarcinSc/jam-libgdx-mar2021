package com.gempukku.jam.libgdx.march2021.system.machine.condition;

import com.badlogic.gdx.utils.Array;
import com.gempukku.jam.libgdx.march2021.system.machine.ContainerTriggerCondition;
import com.gempukku.libgdx.lib.fst.TriggerCondition;

public class NotCondition implements ContainerTriggerCondition {
    private TriggerCondition condition;

    @Override
    public void reset() {
        condition.reset();
    }

    @Override
    public boolean isTriggered() {
        return !condition.isTriggered();
    }

    @Override
    public Iterable<TriggerCondition> getChildren() {
        return new Array<>(new TriggerCondition[]{condition});
    }
}
