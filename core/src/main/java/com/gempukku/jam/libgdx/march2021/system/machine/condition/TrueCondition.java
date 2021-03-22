package com.gempukku.jam.libgdx.march2021.system.machine.condition;

import com.gempukku.libgdx.lib.fst.TriggerCondition;

public class TrueCondition implements TriggerCondition {
    @Override
    public void reset() {

    }

    @Override
    public boolean isTriggered() {
        return true;
    }
}
