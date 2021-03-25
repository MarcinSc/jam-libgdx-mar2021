package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.Entity;
import com.gempukku.jam.libgdx.march2021.component.PlayerComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.Box2DBodyDataComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.CollisionListener;

public class PlayerFallListener implements CollisionListener {
    @Override
    public void collision(Entity entity1, Entity entity2) {
        PlayerComponent playerComponent = entity1.getComponent(PlayerComponent.class);
        if (playerComponent != null) {
            float verticalSpeed = entity1.getComponent(Box2DBodyDataComponent.class).getBody().getLinearVelocity().y;
            if (verticalSpeed < -8) {
                playerComponent.setDead(true);
            }
        }
    }
}
