package com.gempukku.jam.libgdx.march2021.system.level;

import com.badlogic.ashley.core.Engine;
import com.gempukku.jam.libgdx.march2021.action.Action;
import com.gempukku.jam.libgdx.march2021.system.ActionSystem;
import com.gempukku.jam.libgdx.march2021.system.TimeSystem;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;
import com.gempukku.libgdx.graph.pipeline.PipelineRenderer;
import com.gempukku.libgdx.graph.plugin.screen.GraphScreenShaders;

public class MadTeaPartyLevelLogic implements LevelLogic {
    private Engine engine;

    @Override
    public void loadLogic(Engine engine) {
        this.engine = engine;
        PipelineRenderer pipelineRenderer = engine.getSystem(RenderingSystem.class).getPipelineRenderer();
        pipelineRenderer.setPipelineProperty("Boss HP", true);

        GraphScreenShaders graphScreenShaders = pipelineRenderer.getPluginData(GraphScreenShaders.class);
        graphScreenShaders.setProperty("Boss HP", "Health", 1f);
        graphScreenShaders.setProperty("Boss HP", "Old Health", 1f);

        ActionSystem actionSystem = engine.getSystem(ActionSystem.class);
        actionSystem.addAction(
                new SpawnFalling(5, 3, 10));
        actionSystem.addAction(
                new SpawnFalling(10, 3, 10));
    }

    @Override
    public void unloadLogic(Engine engine) {
        PipelineRenderer pipelineRenderer = engine.getSystem(RenderingSystem.class).getPipelineRenderer();
        pipelineRenderer.setPipelineProperty("Boss HP", false);
    }

    private class SpawnFalling implements Action {
        private float spawnTime;
        private float damageTime;
        private float destroyTime;
        private boolean spawned;
        private boolean damaging;

        public SpawnFalling(float delay, float damageDelay, float duration) {
            float start = engine.getSystem(TimeSystem.class).getTimeProvider().getTime();
            spawnTime = start + delay;
            damageTime = start + delay + damageDelay;
            destroyTime = start + delay + duration;
        }

        @Override
        public boolean act() {
            float currentTime = engine.getSystem(TimeSystem.class).getTimeProvider().getTime();
            if (!spawned && currentTime >= spawnTime) {
                System.out.println("Spawning visual");
                spawned = true;
            }
            if (!damaging && currentTime >= damageTime) {
                System.out.println("Spawning damage");
                damaging = true;
            }
            if (currentTime >= destroyTime) {
                System.out.println("Destroying");
                return true;
            } else {
                return false;
            }
        }
    }
}
