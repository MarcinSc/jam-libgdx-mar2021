package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.gempukku.jam.libgdx.march2021.component.IdComponent;

public class IdSystem extends EntitySystem {
    private ImmutableArray<Entity> idEntities;

    public IdSystem(int priority) {
        super(priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        idEntities = engine.getEntitiesFor(Family.all(IdComponent.class).get());
    }

    public Entity getEntityById(String id) {
        for (Entity idEntity : idEntities) {
            if (idEntity.getComponent(IdComponent.class).getId().equals(id))
                return idEntity;
        }
        return null;
    }
}
