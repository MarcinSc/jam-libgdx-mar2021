package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.gempukku.jam.libgdx.march2021.component.PlayerComponent;
import com.gempukku.jam.libgdx.march2021.component.PlayerSpawnComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.PositionComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.Box2DSystem;

public class SpawnSystem extends EntitySystem {
    private ImmutableArray<Entity> playerEntities;

    public SpawnSystem(int priority) {
        super(priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        playerEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class).get());
        engine.addEntityListener(
                Family.all(PlayerSpawnComponent.class, PositionComponent.class).get(),
                new EntityListener() {
                    @Override
                    public void entityAdded(Entity entity) {
                        PositionComponent spawnPosition = entity.getComponent(PositionComponent.class);
                        for (Entity playerEntity : playerEntities) {
                            PositionComponent position = playerEntity.getComponent(PositionComponent.class);
                            position.setX(spawnPosition.getX());
                            position.setY(spawnPosition.getY());

                            Box2DSystem physicsSystem = engine.getSystem(Box2DSystem.class);
                            physicsSystem.updateBody(playerEntity);
                        }
                    }

                    @Override
                    public void entityRemoved(Entity entity) {

                    }
                });
    }
}
