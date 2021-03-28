package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.gempukku.jam.libgdx.march2021.action.Action;
import com.gempukku.jam.libgdx.march2021.action.DelayedAction;
import com.gempukku.jam.libgdx.march2021.action.FadeInAction;
import com.gempukku.jam.libgdx.march2021.action.FadeOutAction;
import com.gempukku.jam.libgdx.march2021.component.ImportTemplateComponent;
import com.gempukku.jam.libgdx.march2021.component.LevelComponent;
import com.gempukku.jam.libgdx.march2021.system.level.IntoRabbitHoleLevelLogic;
import com.gempukku.jam.libgdx.march2021.system.level.LevelContainer;
import com.gempukku.jam.libgdx.march2021.system.level.MadTeaPartyLevelLogic;
import com.gempukku.jam.libgdx.march2021.system.level.TheGreatFallLevelLogic;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.Box2DSystem;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;
import com.gempukku.libgdx.graph.pipeline.PipelineRenderer;
import com.gempukku.libgdx.graph.plugin.screen.GraphScreenShaders;
import com.gempukku.libgdx.graph.time.TimeProvider;
import com.gempukku.libgdx.lib.template.JsonTemplateLoader;
import com.gempukku.libgdx.lib.template.ashley.AshleyEngineJson;
import com.gempukku.libgdx.lib.template.ashley.AshleyTemplateEntityLoader;
import com.gempukku.libgdx.lib.template.ashley.EntityDef;

public class LevelSystem extends EntitySystem {
    private LevelContainer levelContainer;
    private String currentLevel;

    public LevelSystem(int priority) {
        super(priority);

        levelContainer = new LevelContainer();
        levelContainer.addLevelLogic(1, new IntoRabbitHoleLevelLogic());
        levelContainer.addLevelLogic(2, new TheGreatFallLevelLogic());
        levelContainer.addLevelLogic(3, new MadTeaPartyLevelLogic());
    }

    public void loadLevel(String level, boolean showLevelScreen) {
        currentLevel = level;
        Engine engine = getEngine();

        if (createEntities(level)) {
            pauseGame();

            PipelineRenderer pipelineRenderer = engine.getSystem(RenderingSystem.class).getPipelineRenderer();

            GraphScreenShaders screenShaders = pipelineRenderer.getPluginData(GraphScreenShaders.class);
            screenShaders.setProperty("Blackout", "Alpha", 1f);

            if (showLevelScreen) {
                UISystem uiSystem = engine.getSystem(UISystem.class);
                uiSystem.showLevelScreen();

                ActionSystem actionSystem = engine.getSystem(ActionSystem.class);
                actionSystem.setSpaceTriggeredAction(
                        new Action() {
                            @Override
                            public boolean act() {
                                uiSystem.hideLevelScreen();
                                float fadeInLength = 1f;
                                TimeProvider timeProvider = engine.getSystem(TimeSystem.class).getTimeProvider();
                                actionSystem.addAction(new DelayedAction(engine, fadeInLength,
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                unpauseGame();
                                            }
                                        }));
                                actionSystem.addAction(new FadeInAction(timeProvider, screenShaders, "Blackout", "Alpha", fadeInLength));
                                return true;
                            }
                        });
            } else {
                float fadeInLength = 1f;
                TimeProvider timeProvider = engine.getSystem(TimeSystem.class).getTimeProvider();
                ActionSystem actionSystem = engine.getSystem(ActionSystem.class);
                actionSystem.addAction(new DelayedAction(engine, fadeInLength,
                        new Runnable() {
                            @Override
                            public void run() {
                                unpauseGame();
                            }
                        }));
                actionSystem.addAction(new FadeInAction(timeProvider, screenShaders, "Blackout", "Alpha", fadeInLength));
            }
        }
    }

    public void unloadLevelAndGoTo(String level) {
        unloadLevel(
                new Runnable() {
                    @Override
                    public void run() {
                        loadLevel(level, true);
                    }
                }
        );
    }

    public void unloadLevel(Runnable followup) {
        Engine engine = getEngine();

        for (Entity levelEntity : engine.getEntitiesFor(Family.all(LevelComponent.class).get())) {
            LevelComponent levelComponent = levelEntity.getComponent(LevelComponent.class);
            int levelNumber = levelComponent.getLevelNumber();
            levelContainer.getLevelLogic(levelNumber).unloadLogic(engine);
        }

        PipelineRenderer pipelineRenderer = engine.getSystem(RenderingSystem.class).getPipelineRenderer();
        GraphScreenShaders screenShaders = pipelineRenderer.getPluginData(GraphScreenShaders.class);
        float fadeOutLength = 1f;
        TimeProvider timeProvider = engine.getSystem(TimeSystem.class).getTimeProvider();
        pauseGame();
        ActionSystem actionSystem = engine.getSystem(ActionSystem.class);
        actionSystem.addAction(new FadeOutAction(timeProvider, screenShaders, "Blackout", "Alpha", fadeOutLength));
        actionSystem.addAction(new DelayedAction(engine, fadeOutLength,
                new Runnable() {
                    @Override
                    public void run() {
                        engine.removeAllEntities();
                        actionSystem.clearActions();
                        followup.run();
                    }
                }));
    }

    public void reloadCurrentLevel() {
        unloadLevel(
                new Runnable() {
                    @Override
                    public void run() {
                        loadLevel(currentLevel, false);
                    }
                });
    }


    private boolean createEntities(String level) {
        Engine engine = getEngine();

        Json json = new AshleyEngineJson(engine);
        JsonReader jsonReader = new JsonReader();
        ClasspathFileHandleResolver fileHandleResolver = new ClasspathFileHandleResolver();
        if (fileHandleResolver.resolve(level).exists()) {
            createEntity(AshleyTemplateEntityLoader.loadTemplate("handWrittenEntities/player/player.json", json, fileHandleResolver));

            JsonValue levelJson = JsonTemplateLoader.loadTemplateFromFile(level, fileHandleResolver);
            for (JsonValue entity : levelJson.get("entities")) {
                JsonValue importTemplate = entity.get(ImportTemplateComponent.class.getName());
                if (importTemplate != null) {
                    JsonValue importedPart = jsonReader.parse(fileHandleResolver.resolve(importTemplate.getString("path")));
                    for (JsonValue importedComponent : importedPart) {
                        entity.addChild(importedComponent.name(), clone(importedComponent));
                    }
                }
                createEntity(AshleyTemplateEntityLoader.convertToAshley(entity, json));
            }
            return true;
        } else {
            Gdx.app.exit();
            return false;
        }
    }

    private static JsonValue clone(JsonValue value) {
        if (value.isString())
            return new JsonValue(value.asString());
        JsonReader reader = new JsonReader();
        return reader.parse(value.toJson(JsonWriter.OutputType.json));
    }

    private void createEntity(EntityDef template) {
        Engine engine = getEngine();

        Entity entity = engine.createEntity();
        for (Component component : template.getComponents()) {
            entity.add(component);
        }
        engine.addEntity(entity);
    }

    private void pauseGame() {
        Engine engine = getEngine();
        engine.getSystem(InputControlSystem.class).setEnabled(false);
        engine.getSystem(Box2DSystem.class).setEnabled(false);
        //engine.getSystem(FiniteStateSystem.class).setEnabled(false);
    }

    private void unpauseGame() {
        Engine engine = getEngine();
        engine.getSystem(InputControlSystem.class).setEnabled(true);
        engine.getSystem(Box2DSystem.class).setEnabled(true);
        //engine.getSystem(FiniteStateSystem.class).setEnabled(true);

        for (Entity levelEntity : engine.getEntitiesFor(Family.all(LevelComponent.class).get())) {
            LevelComponent levelComponent = levelEntity.getComponent(LevelComponent.class);
            int levelNumber = levelComponent.getLevelNumber();
            levelContainer.getLevelLogic(levelNumber).loadLogic(engine);
        }
    }
}
