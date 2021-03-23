package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.gempukku.jam.libgdx.march2021.component.AttackerComponent;
import com.gempukku.jam.libgdx.march2021.component.HealthComponent;
import com.gempukku.jam.libgdx.march2021.component.UpdateSpriteOnDamageComponent;
import com.gempukku.jam.libgdx.march2021.system.sensor.EntitySensorData;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.Box2DBodyDataComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.FaceDirection;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.FacingComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;

public class CombatSystem extends EntitySystem {
    private Engine engine;

    public CombatSystem(int priority) {
        super(priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
    }

    public void executeAttack(Entity attacker, String attackSensor) {
        int damage = attacker.getComponent(AttackerComponent.class).getDamage();

        FaceDirection faceDirection = attacker.getComponent(FacingComponent.class).getFaceDirection();
        String sidedAttackSensor = attackSensor + faceDirection.name();

        EntitySensorData attackedCharacters = (EntitySensorData) attacker.getComponent(Box2DBodyDataComponent.class).getSensorDataByName(sidedAttackSensor).getData();
        for (Entity entity : attackedCharacters.getEntities()) {
            HealthComponent healthComponent = entity.getComponent(HealthComponent.class);
            int health = healthComponent.getHealth();
            if (health <= damage) {
                entityDies(entity);
            } else {
                healthComponent.setHealth(health - damage);
                entityDamaged(entity);
            }
        }
    }

    private void entityDamaged(Entity entity) {
        if (entity.getComponent(UpdateSpriteOnDamageComponent.class) != null) {
            engine.getSystem(RenderingSystem.class).updateSpriteProperties(entity);
        }
    }

    private void entityDies(Entity entity) {
        engine.removeEntity(entity);
    }
}
