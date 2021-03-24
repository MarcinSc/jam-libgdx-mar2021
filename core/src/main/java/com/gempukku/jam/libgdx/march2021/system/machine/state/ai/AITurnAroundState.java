package com.gempukku.jam.libgdx.march2021.system.machine.state.ai;


import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerState;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.FacingComponent;

public class AITurnAroundState extends EngineTriggerState {
    @Override
    public void transitioningTo(String newState) {

    }

    @Override
    public void transitioningFrom(String oldState) {
        FacingComponent facing = entity.getComponent(FacingComponent.class);
        facing.setFaceDirection(facing.getFaceDirection().inverse());
    }

    @Override
    public void update(float delta) {

    }
}
