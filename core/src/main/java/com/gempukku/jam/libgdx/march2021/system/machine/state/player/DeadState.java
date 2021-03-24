package com.gempukku.jam.libgdx.march2021.system.machine.state.player;

import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerState;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.SpriteStateComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;

public class DeadState extends EngineTriggerState {
    private String state;

    @Override
    public void transitioningTo(String newState) {

    }

    @Override
    public void transitioningFrom(String oldState) {
        SpriteStateComponent spriteState = entity.getComponent(SpriteStateComponent.class);
        spriteState.setState(state);
        engine.getSystem(RenderingSystem.class).updateSprite(entity);
    }

    @Override
    public void update(float delta) {

    }
}
