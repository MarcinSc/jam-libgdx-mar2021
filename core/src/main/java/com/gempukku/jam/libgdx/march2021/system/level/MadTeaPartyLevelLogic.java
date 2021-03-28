package com.gempukku.jam.libgdx.march2021.system.level;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gempukku.jam.libgdx.march2021.action.Action;
import com.gempukku.jam.libgdx.march2021.system.ActionSystem;
import com.gempukku.jam.libgdx.march2021.system.TimeSystem;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;
import com.gempukku.libgdx.graph.pipeline.PipelineRenderer;
import com.gempukku.libgdx.graph.plugin.particles.GraphParticleEffect;
import com.gempukku.libgdx.graph.plugin.particles.GraphParticleEffects;
import com.gempukku.libgdx.graph.plugin.particles.generator.AbstractParticleGenerator;
import com.gempukku.libgdx.graph.plugin.screen.GraphScreenShaders;

public class MadTeaPartyLevelLogic implements LevelLogic {
    private Engine engine;
    private Array<GraphParticleEffect> effects = new Array<>();

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
                new SpawnFalling(5, 3, 10, 15));
        actionSystem.addAction(
                new SpawnFalling(10, 3, 10, 15));
    }

    @Override
    public void unloadLogic(Engine engine) {
        PipelineRenderer pipelineRenderer = engine.getSystem(RenderingSystem.class).getPipelineRenderer();
        pipelineRenderer.setPipelineProperty("Boss HP", false);

        GraphParticleEffects graphParticleEffects = pipelineRenderer.getPluginData(GraphParticleEffects.class);
        for (GraphParticleEffect effect : effects) {
            graphParticleEffects.destroyEffect(effect);
        }
        effects.clear();
    }

    private class SpawnFalling implements Action {
        private float spawnTime;
        private float damageTime;
        private float stopTime;
        private float destroyTime;
        private float location;
        private boolean spawned;
        private boolean damaging;
        private boolean stopped;

        private GraphParticleEffect effect;

        public SpawnFalling(float delay, float damageDelay, float stopDelay, float duration) {
            float start = engine.getSystem(TimeSystem.class).getTimeProvider().getTime();
            spawnTime = start + delay;
            damageTime = start + delay + damageDelay;
            stopTime = start + delay + stopDelay;
            destroyTime = start + delay + duration;
            location = MathUtils.random(-140, 140);
        }

        @Override
        public boolean act() {
            float currentTime = engine.getSystem(TimeSystem.class).getTimeProvider().getTime();
            if (!spawned && currentTime >= spawnTime) {
                PipelineRenderer pipelineRenderer = engine.getSystem(RenderingSystem.class).getPipelineRenderer();
                GraphParticleEffects graphParticleEffects = pipelineRenderer.getPluginData(GraphParticleEffects.class);
                LineSquareParticleGenerator particleGenerator = new LineSquareParticleGenerator(3, new Vector3(location, 120, -11), new Vector3(20, 0, 0));
                effect = graphParticleEffects.createEffect("Falling", particleGenerator);
                graphParticleEffects.startEffect(effect);
                effects.add(effect);
                spawned = true;
            }
            if (!damaging && currentTime >= damageTime) {
                damaging = true;
            }
            if (!stopped && currentTime >= stopTime) {
                PipelineRenderer pipelineRenderer = engine.getSystem(RenderingSystem.class).getPipelineRenderer();
                GraphParticleEffects graphParticleEffects = pipelineRenderer.getPluginData(GraphParticleEffects.class);
                graphParticleEffects.stopEffect(effect);
                stopped = true;
            }
            if (currentTime >= destroyTime) {
                PipelineRenderer pipelineRenderer = engine.getSystem(RenderingSystem.class).getPipelineRenderer();
                GraphParticleEffects graphParticleEffects = pipelineRenderer.getPluginData(GraphParticleEffects.class);
                graphParticleEffects.destroyEffect(effect);
                effects.removeValue(effect, true);
                return true;
            } else {
                return false;
            }
        }
    }

    private static class LineSquareParticleGenerator extends AbstractParticleGenerator {
        private Vector3 center;
        private Vector3 range;

        public LineSquareParticleGenerator(float lifeLength, Vector3 center, Vector3 range) {
            super(lifeLength);
            this.center = center;
            this.range = range;
        }

        @Override
        protected void generateLocation(Vector3 location) {
            float value = MathUtils.random();
            value *= value;
            if (MathUtils.randomBoolean())
                value = -value;
            location.set(
                    center.x + value * range.x,
                    center.y + value * range.y,
                    center.z + value * range.z);
        }
    }
}
