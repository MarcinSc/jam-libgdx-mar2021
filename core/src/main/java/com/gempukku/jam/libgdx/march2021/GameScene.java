package com.gempukku.jam.libgdx.march2021;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gempukku.jam.libgdx.march2021.system.ActionSystem;
import com.gempukku.jam.libgdx.march2021.system.ActivateSystem;
import com.gempukku.jam.libgdx.march2021.system.CameraSystem;
import com.gempukku.jam.libgdx.march2021.system.CombatSystem;
import com.gempukku.jam.libgdx.march2021.system.DirectTextureLoader;
import com.gempukku.jam.libgdx.march2021.system.FiniteStateSystem;
import com.gempukku.jam.libgdx.march2021.system.IdSystem;
import com.gempukku.jam.libgdx.march2021.system.InputControlSystem;
import com.gempukku.jam.libgdx.march2021.system.LevelSetupSystem;
import com.gempukku.jam.libgdx.march2021.system.LevelSystem;
import com.gempukku.jam.libgdx.march2021.system.PlayerFallListener;
import com.gempukku.jam.libgdx.march2021.system.SpawnSystem;
import com.gempukku.jam.libgdx.march2021.system.TimeSystem;
import com.gempukku.jam.libgdx.march2021.system.UISystem;
import com.gempukku.jam.libgdx.march2021.system.activate.AnimateMoveAndDestroyActivateListener;
import com.gempukku.jam.libgdx.march2021.system.activate.MoveToLevelActivateListener;
import com.gempukku.jam.libgdx.march2021.system.sensor.ContactSensorContactListener;
import com.gempukku.jam.libgdx.march2021.system.sensor.EntitySensorContactListener;
import com.gempukku.jam.libgdx.march2021.system.sensor.FallContactSensorContactListener;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.SpriteComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.Box2DSystem;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.EntityPositionUpdateListener;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;
import com.gempukku.libgdx.graph.loader.GraphLoader;
import com.gempukku.libgdx.graph.pipeline.PipelineLoaderCallback;
import com.gempukku.libgdx.graph.pipeline.PipelineRenderer;
import com.gempukku.libgdx.graph.pipeline.RenderOutputs;
import com.gempukku.libgdx.graph.time.TimeProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class GameScene implements Scene {
    private Array<Disposable> resources = new Array<>();
    private DirectTextureLoader textureLoader;

    private Engine engine;
    private Camera camera;

    private PerformanceProfiler performanceProfiler;

    public GameScene(ProfilerInfoProvider profilerInfoProvider) {
        performanceProfiler = new PerformanceProfiler(profilerInfoProvider);
        resources.add(performanceProfiler);
    }

    @Override
    public void initializeScene() {
        //Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);

        textureLoader = new DirectTextureLoader();
        resources.add(textureLoader);

        camera = createCamera();

        engine = setupEntitySystem();

        LevelSystem levelSystem = engine.getSystem(LevelSystem.class);
        levelSystem.loadLevel("entities/level-1.json", true);
        //levelSystem.loadLevel("entities/level-2.json", true);
    }

    @Override
    public Scene getNextScene() {
        return null;
    }

    @Override
    public void resizeScene(int width, int height) {
//        camera.viewportWidth = width;
//        camera.viewportHeight = height;
//        camera.update();
    }

    @Override
    public void renderScene() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            performanceProfiler.toggle();
        }

        float delta = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
        engine.update(delta);

        performanceProfiler.beforeDraw();
        PipelineRenderer pipelineRenderer = engine.getSystem(RenderingSystem.class).getPipelineRenderer();
        pipelineRenderer.render(RenderOutputs.drawToScreen);
        Box2DSystem box2DSystem = engine.getSystem(Box2DSystem.class);
        performanceProfiler.afterDraw(camera, box2DSystem.getWorld(), box2DSystem.getPixelsToMeters());
    }

    @Override
    public void disposeScene() {
        for (EntitySystem system : engine.getSystems()) {
            if (system instanceof Disposable)
                ((Disposable) system).dispose();
        }

        for (Disposable resource : resources) {
            resource.dispose();
        }
        resources.clear();
    }

    private static Camera createCamera() {
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, 320, 240);
        //camera.zoom = 0.5f;
        camera.position.set(0, 0, 0);
        camera.update();

        return camera;
    }

    private Engine setupEntitySystem() {
        Engine engine = new Engine();

        int priority = 0;

        TimeSystem timeSystem = new TimeSystem(priority++);
        engine.addSystem(timeSystem);

        ActionSystem actionSystem = new ActionSystem(priority++);
        engine.addSystem(actionSystem);

        LevelSystem levelSystem = new LevelSystem(priority++);
        engine.addSystem(levelSystem);

        SpawnSystem spawnSystem = new SpawnSystem(priority++);
        engine.addSystem(spawnSystem);

        PipelineRenderer pipelineRenderer = loadPipeline(timeSystem.getTimeProvider(), camera);
        resources.add(pipelineRenderer);

        InputControlSystem inputControlSystem = new InputControlSystem(priority++);
        engine.addSystem(inputControlSystem);

        LevelSetupSystem levelSetupSystem = new LevelSetupSystem(priority++);
        engine.addSystem(levelSetupSystem);

        CombatSystem combatSystem = new CombatSystem(priority++,
                new Runnable() {
                    @Override
                    public void run() {
                        playerDied();
                    }
                });
        engine.addSystem(combatSystem);

        ActivateSystem activateSystem = new ActivateSystem(priority++);
        activateSystem.addActivateListener(
                new MoveToLevelActivateListener(
                        new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                engine.getSystem(LevelSystem.class).unloadLevelAndGoTo(s);
                            }
                        }));
        activateSystem.addActivateListener(
                new AnimateMoveAndDestroyActivateListener(engine));
        engine.addSystem(activateSystem);

        UISystem uiSystem = new UISystem(priority++);
        engine.addSystem(uiSystem);

        IdSystem idSystem = new IdSystem(priority++);
        engine.addSystem(idSystem);

        FiniteStateSystem finiteStateSystem = new FiniteStateSystem(priority++);
        engine.addSystem(finiteStateSystem);

        Box2DSystem box2DSystem = new Box2DSystem(priority++, new Vector2(0, -15f), false, 100);
        box2DSystem.addSensorContactListener("groundContact", new FallContactSensorContactListener(box2DSystem.getBitForCategory("Ground")));
        box2DSystem.addSensorContactListener("attackableList", new EntitySensorContactListener(box2DSystem.getBitForCategory("Attackable")));
        box2DSystem.addSensorContactListener("activator", new EntitySensorContactListener(box2DSystem.getBitForCategory("Activable")));
        box2DSystem.addSensorContactListener("vulnerable", new ContactSensorContactListener(box2DSystem.getBitForCategory("Harmful")));
        box2DSystem.addCollisionListener(new PlayerFallListener(
                new Runnable() {
                    @Override
                    public void run() {
                        playerDied();
                    }
                }));
        engine.addSystem(box2DSystem);

        CameraSystem cameraSystem = new CameraSystem(priority++, camera);
        engine.addSystem(cameraSystem);

        RenderingSystem renderingSystem = new RenderingSystem(priority++, timeSystem.getTimeProvider(), pipelineRenderer, textureLoader);
        engine.addSystem(renderingSystem);

        box2DSystem.addEntityPositionUpdateListener(
                new EntityPositionUpdateListener() {
                    @Override
                    public void positionUpdated(Entity entity) {
                        if (entity.getComponent(SpriteComponent.class) != null) {
                            renderingSystem.updateSpriteData(entity);
                        }
                    }
                });

        return engine;
    }

    private void playerDied() {
        LevelSystem levelSystem = engine.getSystem(LevelSystem.class);
        levelSystem.reloadCurrentLevel();
    }

    private static PipelineRenderer loadPipeline(TimeProvider timeProvider, Camera camera) {
        FileHandle pipelineFile = Gdx.files.classpath("pipeline.json");
        try {
            InputStream is = pipelineFile.read();
            try {
                PipelineRenderer pipelineRenderer = GraphLoader.loadGraph(is, new PipelineLoaderCallback(timeProvider));
                pipelineRenderer.setPipelineProperty("Camera", camera);
                return pipelineRenderer;
            } finally {
                is.close();
            }
        } catch (IOException exp) {
            throw new GdxRuntimeException("Unable to load pipeline", exp);
        }
    }
}
