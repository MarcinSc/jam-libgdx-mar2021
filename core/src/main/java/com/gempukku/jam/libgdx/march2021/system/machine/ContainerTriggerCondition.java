package com.gempukku.jam.libgdx.march2021.system.machine;

import com.gempukku.libgdx.lib.fst.TriggerCondition;

public interface ContainerTriggerCondition extends TriggerCondition {
    Iterable<TriggerCondition> getChildren();
}
