package com.gempukku.jam.libgdx.march2021.system.sensor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.gempukku.jam.libgdx.march2021.component.PlayerComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.Box2DBodyDataComponent;

public class FallContactSensorData extends ContactSensorData {
    public FallContactSensorData(Entity entity) {
        super(entity);
    }

    public void investigateFallOn(Fixture other) {
        if (getEntity().getComponent(PlayerComponent.class) != null) {
            Box2DBodyDataComponent body = getEntity().getComponent(Box2DBodyDataComponent.class);
            System.out.println(body.getBody().getLinearVelocity().len());
        }
    }
}
