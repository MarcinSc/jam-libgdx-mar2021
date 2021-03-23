package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.gempukku.jam.libgdx.march2021.system.activate.ActivateListener;
import com.gempukku.jam.libgdx.march2021.system.sensor.EntitySensorData;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.Box2DBodyDataComponent;

public class ActivateSystem extends EntitySystem {
    private Array<ActivateListener> listeners = new Array<>();
    private ImmutableArray<Entity> physicsEntities;

    public ActivateSystem(int priority) {
        super(priority);
    }

    public void addActivateListener(ActivateListener listener) {
        listeners.add(listener);
    }

    public void removeActivateListener(ActivateListener listener) {
        listeners.removeValue(listener, true);
    }

    @Override
    public void addedToEngine(Engine engine) {
        physicsEntities = engine.getEntitiesFor(Family.all(Box2DBodyDataComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity physicsEntity : physicsEntities) {
            Box2DBodyDataComponent bodyData = physicsEntity.getComponent(Box2DBodyDataComponent.class);
            Box2DBodyDataComponent.SensorData activator = bodyData.getSensorDataByName("activator");
            if (activator != null) {
                EntitySensorData entitySensorData = (EntitySensorData) activator.getData();
                for (Entity entity : entitySensorData.getEntities()) {
                    for (ActivateListener listener : listeners) {
                        listener.activate(physicsEntity, entity);
                    }
                }
            }
        }
    }
}
