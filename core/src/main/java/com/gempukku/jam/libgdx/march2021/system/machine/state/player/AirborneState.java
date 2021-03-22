package com.gempukku.jam.libgdx.march2021.system.machine.state.player;

import com.badlogic.gdx.physics.box2d.Body;
import com.gempukku.jam.libgdx.march2021.component.AgilityComponent;
import com.gempukku.jam.libgdx.march2021.component.InputControlledComponent;
import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerState;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.Box2DBodyDataComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.FaceDirection;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.FacingComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.SpriteStateComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;

public class AirborneState extends EngineTriggerState {
    @Override
    public void transitioningTo(String newState) {
    }

    @Override
    public void transitioningFrom(String oldState) {
        SpriteStateComponent spriteState = entity.getComponent(SpriteStateComponent.class);
        spriteState.setState("Airborne");
        engine.getSystem(RenderingSystem.class).updateSprite(entity);
    }

    @Override
    public void update(float delta) {
        Box2DBodyDataComponent physicsComponent = entity.getComponent(Box2DBodyDataComponent.class);

        Body playerBody = physicsComponent.getBody();
        float verticalVelocity = playerBody.getLinearVelocity().y;

        InputControlledComponent input = entity.getComponent(InputControlledComponent.class);
        FacingComponent facing = entity.getComponent(FacingComponent.class);
        AgilityComponent agility = entity.getComponent(AgilityComponent.class);

        boolean left = input.getKeyState("Left");
        boolean right = input.getKeyState("Right");
        if (left && !right) {
            facing.setFaceDirection(FaceDirection.Left);
            playerBody.setLinearVelocity(-agility.getWalkSpeed(), verticalVelocity);
        } else if (right && !left) {
            facing.setFaceDirection(FaceDirection.Right);
            playerBody.setLinearVelocity(agility.getWalkSpeed(), verticalVelocity);
        } else {
            playerBody.setLinearVelocity(0, verticalVelocity);
        }
    }
}
