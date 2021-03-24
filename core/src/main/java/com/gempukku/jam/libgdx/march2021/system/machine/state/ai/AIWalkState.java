package com.gempukku.jam.libgdx.march2021.system.machine.state.ai;

import com.badlogic.gdx.physics.box2d.Body;
import com.gempukku.jam.libgdx.march2021.component.AgilityComponent;
import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerState;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.Box2DBodyDataComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.FacingComponent;

public class AIWalkState extends EngineTriggerState {
    @Override
    public void transitioningTo(String newState) {

    }

    @Override
    public void transitioningFrom(String oldState) {

    }

    @Override
    public void update(float delta) {
        FacingComponent facing = entity.getComponent(FacingComponent.class);
        Box2DBodyDataComponent physicsComponent = entity.getComponent(Box2DBodyDataComponent.class);
        AgilityComponent agility = entity.getComponent(AgilityComponent.class);

        Body playerBody = physicsComponent.getBody();
        float verticalVelocity = playerBody.getLinearVelocity().y;

        float direction = facing.getFaceDirection().getDirection();
        playerBody.setLinearVelocity(direction * agility.getWalkSpeed(), verticalVelocity);
    }
}
