package com.gempukku.jam.libgdx.march2021.system.machine.condition;

import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerCondition;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.FaceDirection;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.FacingComponent;

public class HasFacingCondition extends EngineTriggerCondition {
    private FaceDirection facing;

    @Override
    public boolean isTriggered() {
        FacingComponent facingComponent = entity.getComponent(FacingComponent.class);
        return facingComponent.getFaceDirection() == facing;
    }
}
