package com.gempukku.jam.libgdx.march2021.system.level;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gempukku.jam.libgdx.march2021.action.Action;
import com.gempukku.jam.libgdx.march2021.action.DelayedAction;
import com.gempukku.jam.libgdx.march2021.system.ActionSystem;
import com.gempukku.jam.libgdx.march2021.system.IdSystem;
import com.gempukku.jam.libgdx.march2021.system.LevelSystem;
import com.gempukku.jam.libgdx.march2021.system.TimeSystem;
import com.gempukku.jam.libgdx.march2021.system.UISystem;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.PositionComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;
import com.gempukku.libgdx.graph.pipeline.PipelineRenderer;
import com.gempukku.libgdx.graph.plugin.particles.GraphParticleEffect;
import com.gempukku.libgdx.graph.plugin.particles.GraphParticleEffects;
import com.gempukku.libgdx.graph.plugin.particles.generator.AbstractParticleGenerator;
import com.gempukku.libgdx.graph.plugin.screen.GraphScreenShaders;
import com.gempukku.libgdx.lib.template.ashley.AshleyEngineJson;
import com.gempukku.libgdx.lib.template.ashley.AshleyTemplateEntityLoader;
import com.gempukku.libgdx.lib.template.ashley.EntityDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                new SpawnFalling(2, 3, 5, 8));
        appendPartyActions(actionSystem, 12f, 8f, 2, 1f, 0.75f);
        actionSystem.addAction(
                new SpawnFalling(22, 3, 5, 8));
        actionSystem.addAction(
                new SpawnFalling(25, 3, 5, 8));
        appendPartyActions(actionSystem, 35f, 8f, 2, 0.75f, 0.5f);
        actionSystem.addAction(
                new SpawnFalling(45, 3, 5, 8));
        actionSystem.addAction(
                new SpawnFalling(48, 3, 5, 8));
        appendPartyActions(actionSystem, 58f, 8f, 3, 0.5f, 0.25f);
        actionSystem.addAction(
                new SpawnFalling(68, 3, 5, 8));
        actionSystem.addAction(
                new SpawnFalling(71, 3, 5, 8));
        appendPartyActions(actionSystem, 80f, 8f, 3, 0.25f, 0f);
        actionSystem.addAction(
                new DelayedAction(engine, 90f, new Runnable() {
                    @Override
                    public void run() {
                        engine.getSystem(LevelSystem.class).unloadLevelAndGoTo("entities/level-4.json");
                    }
                }));
    }

    private void appendPartyActions(ActionSystem actionSystem, float spawnDelay, float spawnDuration, int guestCount, float healthStart, float healthFinish) {
        actionSystem.addAction(
                new SpawnGuests(spawnDelay, 4, spawnDuration, guestCount));
        actionSystem.addAction(
                new LowerPartyHealth(spawnDelay + spawnDuration, 1, healthStart, healthFinish));
        actionSystem.addAction(
                new DelayedAction(engine, spawnDelay,
                        new Runnable() {
                            @Override
                            public void run() {
                                engine.getSystem(UISystem.class).showHintScreen("Party is about to start, find empty seat!");
                            }
                        }));
        actionSystem.addAction(
                new DelayedAction(engine, spawnDelay + spawnDuration,
                        new Runnable() {
                            @Override
                            public void run() {
                                engine.getSystem(UISystem.class).hideHintScreen();
                            }
                        }));
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

        engine.getSystem(UISystem.class).hideHintScreen();
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

        private Entity damagingEntity;
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
                AshleyEngineJson json = new AshleyEngineJson(engine);
                ClasspathFileHandleResolver fileHandleResolver = new ClasspathFileHandleResolver();
                EntityDef entityDef = AshleyTemplateEntityLoader.loadTemplate("templates/FallingDamage.json", json, fileHandleResolver);
                damagingEntity = engine.createEntity();
                for (Component component : entityDef.getComponents()) {
                    damagingEntity.add(component);
                }
                PositionComponent position = damagingEntity.getComponent(PositionComponent.class);
                position.setX(location);
                position.setY(0);

                engine.addEntity(damagingEntity);
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
                engine.removeEntity(damagingEntity);
                return true;
            } else {
                return false;
            }
        }
    }

    private class SpawnGuests implements Action {
        private float spawnTime;
        private float damageTime;
        private float destroyTime;

        private boolean spawned;
        private boolean damaging;

        private Entity[] guestEntities;
        private Entity[] damageEntities;
        private Entity floorDamageEntity;

        public SpawnGuests(float delay, float damageDelay, float duration, int count) {
            float start = engine.getSystem(TimeSystem.class).getTimeProvider().getTime();
            spawnTime = start + delay;
            damageTime = start + delay + damageDelay;
            destroyTime = start + delay + duration;

            List<Integer> positions = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                positions.add(i + 1);
            }
            Collections.shuffle(positions);

            IdSystem idSystem = engine.getSystem(IdSystem.class);

            AshleyEngineJson json = new AshleyEngineJson(engine);
            ClasspathFileHandleResolver fileHandleResolver = new ClasspathFileHandleResolver();

            EntityDef entityDef = AshleyTemplateEntityLoader.loadTemplate("templates/FloorDamage.json", json, fileHandleResolver);
            floorDamageEntity = engine.createEntity();
            for (Component component : entityDef.getComponents()) {
                floorDamageEntity.add(component);
            }

            guestEntities = new Entity[count];
            damageEntities = new Entity[count];
            for (int i = 0; i < count; i++) {
                entityDef = AshleyTemplateEntityLoader.loadTemplate("templates/party/PartyGoer" + (i + 1) + ".json", json, fileHandleResolver);
                Entity guestEntity = engine.createEntity();
                for (Component component : entityDef.getComponents()) {
                    guestEntity.add(component);
                }

                PositionComponent spawnPosition = idSystem.getEntityById("spawn-" + positions.get(i)).getComponent(PositionComponent.class);
                PositionComponent guestPosition = guestEntity.getComponent(PositionComponent.class);
                guestPosition.setX(spawnPosition.getX());
                guestPosition.setY(spawnPosition.getY());

                guestEntities[i] = guestEntity;

                entityDef = AshleyTemplateEntityLoader.loadTemplate("templates/SeatDamage.json", json, fileHandleResolver);
                Entity damageEntity = engine.createEntity();
                for (Component component : entityDef.getComponents()) {
                    damageEntity.add(component);
                }

                PositionComponent damagePosition = damageEntity.getComponent(PositionComponent.class);
                damagePosition.setX(spawnPosition.getX());
                damagePosition.setY(spawnPosition.getY());

                damageEntities[i] = damageEntity;
            }
        }

        @Override
        public boolean act() {
            float currentTime = engine.getSystem(TimeSystem.class).getTimeProvider().getTime();
            if (!spawned && currentTime >= spawnTime) {
                for (Entity entity : guestEntities) {
                    engine.addEntity(entity);
                }
                spawned = true;
            }
            if (!damaging && currentTime >= damageTime) {
                for (Entity entity : damageEntities) {
                    engine.addEntity(entity);
                }
                engine.addEntity(floorDamageEntity);
                damaging = true;
            }
            if (currentTime >= destroyTime) {
                for (Entity entity : guestEntities) {
                    engine.removeEntity(entity);
                }
                for (Entity entity : damageEntities) {
                    engine.removeEntity(entity);
                }
                engine.removeEntity(floorDamageEntity);
                return true;
            }
            return false;
        }
    }

    private class LowerPartyHealth implements Action {
        private float lowerStart;
        private float lowerDuration;

        private float healthStart;
        private float healthFinish;

        public LowerPartyHealth(float delay, float lowerDuration, float healthStart, float healthFinish) {
            float start = engine.getSystem(TimeSystem.class).getTimeProvider().getTime();
            lowerStart = start + delay;

            this.lowerDuration = lowerDuration;
            this.healthStart = healthStart;
            this.healthFinish = healthFinish;
        }

        @Override
        public boolean act() {
            float currentTime = engine.getSystem(TimeSystem.class).getTimeProvider().getTime();
            if (currentTime >= lowerStart + lowerDuration) {
                PipelineRenderer pipelineRenderer = engine.getSystem(RenderingSystem.class).getPipelineRenderer();
                GraphScreenShaders graphScreenShaders = pipelineRenderer.getPluginData(GraphScreenShaders.class);
                graphScreenShaders.setProperty("Boss HP", "Health", healthFinish);
                graphScreenShaders.setProperty("Boss HP", "Old Health", healthFinish);
                return true;
            } else if (currentTime >= lowerStart) {
                float progress = 1 - (currentTime - lowerStart) / lowerDuration;
                PipelineRenderer pipelineRenderer = engine.getSystem(RenderingSystem.class).getPipelineRenderer();
                GraphScreenShaders graphScreenShaders = pipelineRenderer.getPluginData(GraphScreenShaders.class);
                graphScreenShaders.setProperty("Boss HP", "Health", healthFinish);
                graphScreenShaders.setProperty("Boss HP", "Old Health", healthFinish + progress * (healthStart - healthFinish));
                return false;
            }
            return false;
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
