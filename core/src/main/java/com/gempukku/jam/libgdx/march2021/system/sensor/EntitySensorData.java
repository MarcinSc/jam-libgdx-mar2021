package com.gempukku.jam.libgdx.march2021.system.sensor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectSet;

public class EntitySensorData {
    private ObjectSet<Entity> entities = new ObjectSet<>();

    public void addContactEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeContactEntity(Entity entity) {
        entities.remove(entity);
    }

    public Iterable<Entity> getEntities() {
        return entities;
    }
}
