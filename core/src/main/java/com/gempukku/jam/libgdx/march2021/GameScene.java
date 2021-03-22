package com.gempukku.jam.libgdx.march2021;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.jam.libgdx.march2021.system.CameraSystem;
import com.gempukku.jam.libgdx.march2021.system.DirectTextureLoader;
import com.gempukku.jam.libgdx.march2021.system.FiniteStateSystem;
import com.gempukku.jam.libgdx.march2021.system.InputControlSystem;
import com.gempukku.jam.libgdx.march2021.system.SpawnSystem;
import com.gempukku.jam.libgdx.march2021.system.TimeSystem;
import com.gempukku.jam.libgdx.march2021.system.sensor.ContactSensorContactListener;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.SpriteComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.Box2DSystem;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.EntityPositionUpdateListener;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.TextureLoader;
import com.gempukku.libgdx.graph.loader.GraphLoader;
import com.gempukku.libgdx.graph.pipeline.PipelineLoaderCallback;
import com.gempukku.libgdx.graph.pipeline.PipelineRenderer;
import com.gempukku.libgdx.graph.pipeline.RenderOutputs;
import com.gempukku.libgdx.graph.time.TimeProvider;
import com.gempukku.libgdx.lib.template.JsonTemplateLoader;
import com.gempukku.libgdx.lib.template.ashley.AshleyEngineJson;
import com.gempukku.libgdx.lib.template.ashley.AshleyTemplateEntityLoader;
import com.gempukku.libgdx.lib.template.ashley.EntityDef;

import java.io.IOException;
import java.io.InputStream;

public class GameScene implements Scene {
    private Array<Disposable> resources = new Array<>();
    private PipelineRenderer pipelineRenderer;
    private DirectTextureLoader directTextureLoader;

    private Engine engine;
    private Camera camera;

    private ProfilerInfoProvider profilerInfoProvider;
    private PerformanceProfiler performanceProfiler;

    public GameScene(ProfilerInfoProvider profilerInfoProvider) {
        this.profilerInfoProvider = profilerInfoProvider;
    }

    @Override
    public void initializeScene() {
        //Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);

        performanceProfiler = new PerformanceProfiler(profilerInfoProvider);
        resources.add(performanceProfiler);

        directTextureLoader = new DirectTextureLoader();
        resources.add(directTextureLoader);

        camera = createCamera();

        engine = setupEntitySystem(directTextureLoader, camera);

        createEntities();
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

        engine.update(Gdx.graphics.getDeltaTime());

//        System.out.println(camera.viewportWidth);
//        System.out.println("Camera position: " + camera.position);
//        for (Entity player : engine.getEntitiesFor(Family.all(PlayerComponent.class).get())) {
//            PositionComponent position = player.getComponent(PositionComponent.class);
//            System.out.println("Character position: " + position.getX() + ", " + position.getY());
//        }

        performanceProfiler.beforeDraw();
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

    private void createEntities() {
        Json json = new AshleyEngineJson(engine);
        ClasspathFileHandleResolver fileHandleResolver = new ClasspathFileHandleResolver();
        createEntity(AshleyTemplateEntityLoader.loadTemplate("handWrittenEntities/player/player.json", json, fileHandleResolver));

        JsonValue level = JsonTemplateLoader.loadTemplateFromFile("entities/level-1.json", fileHandleResolver);
        for (JsonValue entity : level.get("entities")) {
            createEntity(AshleyTemplateEntityLoader.convertToAshley(entity, json));
        }
    }

    private void createEntity(EntityDef template) {
        Entity entity = engine.createEntity();
        for (Component component : template.getComponents()) {
            entity.add(component);
        }
        engine.addEntity(entity);
    }

    private static Camera createCamera() {
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, 320, 240);
        //camera.zoom = 0.5f;
        camera.position.set(0, 0, 0);
        camera.update();

        return camera;
    }

    private Engine setupEntitySystem(TextureLoader textureLoader, Camera camera) {
        Engine engine = new Engine();

        TimeSystem timeSystem = new TimeSystem(0);
        engine.addSystem(timeSystem);

        SpawnSystem spawnSystem = new SpawnSystem(1);
        engine.addSystem(spawnSystem);

        pipelineRenderer = loadPipeline(timeSystem.getTimeProvider(), camera);
        resources.add(pipelineRenderer);

        InputControlSystem inputControlSystem = new InputControlSystem(2);
        engine.addSystem(inputControlSystem);

        FiniteStateSystem finiteStateSystem = new FiniteStateSystem(5);
        engine.addSystem(finiteStateSystem);

        Box2DSystem box2DSystem = new Box2DSystem(10, new Vector2(0, -15f), false, 100);
        box2DSystem.addSensorContactListener("groundContact", new ContactSensorContactListener(box2DSystem.getBitForCategory("Ground")));
        engine.addSystem(box2DSystem);

        RenderingSystem renderingSystem = new RenderingSystem(20, timeSystem.getTimeProvider(), pipelineRenderer, textureLoader);
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

        CameraSystem cameraSystem = new CameraSystem(30, camera);
        engine.addSystem(cameraSystem);

        return engine;
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
