package com.gempukku.jam.libgdx.march2021.system.machine.state.player;

import com.badlogic.gdx.physics.box2d.Body;
import com.gempukku.jam.libgdx.march2021.component.AgilityComponent;
import com.gempukku.jam.libgdx.march2021.component.InputControlledComponent;
import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerState;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.Box2DBodyDataComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.FaceDirection;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.FacingComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.SpriteStateComponent;

public class WalkIdleState extends EngineTriggerState {
    private boolean idle;

    @Override
    public void transitioningTo(String newState) {

    }

    @Override
    public void transitioningFrom(String oldState) {
        setIdle();
    }

    @Override
    public void update(float delta) {
        InputControlledComponent input = entity.getComponent(InputControlledComponent.class);
        FacingComponent facing = entity.getComponent(FacingComponent.class);
        Box2DBodyDataComponent physicsComponent = entity.getComponent(Box2DBodyDataComponent.class);
        AgilityComponent agility = entity.getComponent(AgilityComponent.class);

        Body playerBody = physicsComponent.getBody();
        float verticalVelocity = playerBody.getLinearVelocity().y;

        boolean left = input.getKeyState("Left");
        boolean right = input.getKeyState("Right");
        if (left && !right) {
            facing.setFaceDirection(FaceDirection.Left);
            playerBody.setLinearVelocity(-agility.getWalkSpeed(), verticalVelocity);
            if (idle)
                setWalk();
        } else if (right && !left) {
            facing.setFaceDirection(FaceDirection.Right);
            playerBody.setLinearVelocity(agility.getWalkSpeed(), verticalVelocity);
            if (idle)
                setWalk();
        } else {
            if (!idle)
                setIdle();
            playerBody.setLinearVelocity(0, verticalVelocity);
        }
    }

    private void setIdle() {
        SpriteStateComponent spriteState = entity.getComponent(SpriteStateComponent.class);
        spriteState.setState("Idle");
        idle = true;
    }

    private void setWalk() {
        SpriteStateComponent spriteState = entity.getComponent(SpriteStateComponent.class);
        spriteState.setState("Walk");
        idle = false;
    }
}
