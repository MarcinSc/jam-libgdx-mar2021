package com.gempukku.jam.libgdx.march2021.system.activate;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.gempukku.jam.libgdx.march2021.action.DelayedAction;
import com.gempukku.jam.libgdx.march2021.action.MoveEntityAction;
import com.gempukku.jam.libgdx.march2021.component.AnimateMoveAndDestroyComponent;
import com.gempukku.jam.libgdx.march2021.system.ActionSystem;
import com.gempukku.jam.libgdx.march2021.system.IdSystem;
import com.gempukku.jam.libgdx.march2021.system.TimeSystem;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.PositionComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.SpriteStateComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;
import com.gempukku.libgdx.graph.time.TimeProvider;

public class AnimateMoveAndDestroyActivateListener implements ActivateListener {
    private Engine engine;

    public AnimateMoveAndDestroyActivateListener(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void activate(Entity activator, Entity activated) {
        AnimateMoveAndDestroyComponent animateMoveAndDestroyComponent = activated.getComponent(AnimateMoveAndDestroyComponent.class);
        if (animateMoveAndDestroyComponent != null && !animateMoveAndDestroyComponent.isExecuted()) {
            Entity entity = engine.getSystem(IdSystem.class).getEntityById(animateMoveAndDestroyComponent.getId());
            TimeProvider timeProvider = engine.getSystem(TimeSystem.class).getTimeProvider();
            PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
            float startX = positionComponent.getX();
            float startY = positionComponent.getY();

            float endX = startX + animateMoveAndDestroyComponent.getDistance().x;
            float endY = startY + animateMoveAndDestroyComponent.getDistance().y;

            float time = animateMoveAndDestroyComponent.getTime();
            entity.getComponent(SpriteStateComponent.class).setState(animateMoveAndDestroyComponent.getSpriteState());
            engine.getSystem(RenderingSystem.class).updateSprite(entity);
            ActionSystem actionSystem = engine.getSystem(ActionSystem.class);
            actionSystem.addAction(
                    new MoveEntityAction(engine, timeProvider, entity,
                            new Vector2(startX, startY), new Vector2(endX, endY),
                            time));
            actionSystem.addAction(
                    new DelayedAction(timeProvider, time,
                            new Runnable() {
                                @Override
                                public void run() {
                                    engine.removeEntity(entity);
                                }
                            }));

            animateMoveAndDestroyComponent.setExecuted(true);
        }
    }
}
