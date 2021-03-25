package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.gempukku.jam.libgdx.march2021.component.LevelComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;
import com.gempukku.libgdx.graph.pipeline.PipelineRenderer;

public class LevelSetupSystem extends EntitySystem {
    public LevelSetupSystem(int priority) {
        super(priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(
                Family.all(LevelComponent.class).get(),
                new EntityListener() {
                    @Override
                    public void entityAdded(Entity entity) {
                        LevelComponent level = entity.getComponent(LevelComponent.class);
                        Color backgroundColor = Color.valueOf(level.getColor());
                        PipelineRenderer pipelineRenderer = engine.getSystem(RenderingSystem.class).getPipelineRenderer();
                        pipelineRenderer.setPipelineProperty("Background", backgroundColor);
                    }

                    @Override
                    public void entityRemoved(Entity entity) {

                    }
                }
        );
    }
}
