package com.gempukku.jam.libgdx.march2021.system.machine;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.gempukku.libgdx.lib.fst.TriggerCondition;

public abstract class EngineTriggerCondition implements TriggerCondition {
    protected transient Entity entity;
    protected transient Engine engine;

    public void init(Entity entity, Engine engine) {
        this.entity = entity;
        this.engine = engine;
    }

    @Override
    public void reset() {

    }
}
