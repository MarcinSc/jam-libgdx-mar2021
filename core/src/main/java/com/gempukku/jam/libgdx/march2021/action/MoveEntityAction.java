package com.gempukku.jam.libgdx.march2021.action;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.PositionComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;
import com.gempukku.libgdx.graph.time.TimeProvider;

public class MoveEntityAction implements Action {
    private Engine engine;
    private TimeProvider timeProvider;
    private Entity entity;
    private Vector2 from;
    private Vector2 to;
    private float time;

    private float start;

    public MoveEntityAction(Engine engine, TimeProvider timeProvider, Entity entity, Vector2 from, Vector2 to, float time) {
        this.engine = engine;
        this.timeProvider = timeProvider;
        this.entity = entity;
        this.from = from;
        this.to = to;
        this.time = time;

        start = timeProvider.getTime();
    }

    @Override
    public boolean act() {
        float currentTime = timeProvider.getTime();
        if (currentTime >= start + this.time) {
            moveToProgress(1f);
            return true;
        }
        moveToProgress((currentTime - start) / time);
        return false;
    }

    private void moveToProgress(float progress) {
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        positionComponent.setX(from.x + (to.x - from.x) * progress);
        positionComponent.setY(from.y + (to.y - from.y) * progress);
        engine.getSystem(RenderingSystem.class).updateSpriteData(entity);
    }
}
