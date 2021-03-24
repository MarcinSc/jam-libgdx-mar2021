package com.gempukku.jam.libgdx.march2021;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gempukku.jam.libgdx.march2021.action.Action;
import com.gempukku.jam.libgdx.march2021.action.DelayedAction;
import com.gempukku.jam.libgdx.march2021.action.FadeInAction;
import com.gempukku.jam.libgdx.march2021.action.FadeOutAction;
import com.gempukku.jam.libgdx.march2021.component.LevelComponent;
import com.gempukku.jam.libgdx.march2021.system.ActivateSystem;
import com.gempukku.jam.libgdx.march2021.system.CameraSystem;
import com.gempukku.jam.libgdx.march2021.system.CombatSystem;
import com.gempukku.jam.libgdx.march2021.system.DirectTextureLoader;
import com.gempukku.jam.libgdx.march2021.system.FiniteStateSystem;
import com.gempukku.jam.libgdx.march2021.system.InputControlSystem;
import com.gempukku.jam.libgdx.march2021.system.LevelSetupSystem;
import com.gempukku.jam.libgdx.march2021.system.SpawnSystem;
import com.gempukku.jam.libgdx.march2021.system.TimeSystem;
import com.gempukku.jam.libgdx.march2021.system.activate.MoveToLevelActivateListener;
import com.gempukku.jam.libgdx.march2021.system.sensor.ContactSensorContactListener;
import com.gempukku.jam.libgdx.march2021.system.sensor.EntitySensorContactListener;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.SpriteComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.Box2DSystem;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.EntityPositionUpdateListener;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;
import com.gempukku.libgdx.graph.loader.GraphLoader;
import com.gempukku.libgdx.graph.pipeline.PipelineLoaderCallback;
import com.gempukku.libgdx.graph.pipeline.PipelineRenderer;
import com.gempukku.libgdx.graph.pipeline.RenderOutputs;
import com.gempukku.libgdx.graph.plugin.screen.GraphScreenShaders;
import com.gempukku.libgdx.graph.plugin.ui.UIPluginPublicData;
import com.gempukku.libgdx.graph.time.TimeProvider;
import com.gempukku.libgdx.lib.template.JsonTemplateLoader;
import com.gempukku.libgdx.lib.template.ashley.AshleyEngineJson;
import com.gempukku.libgdx.lib.template.ashley.AshleyTemplateEntityLoader;
import com.gempukku.libgdx.lib.template.ashley.EntityDef;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class GameScene implements Scene {
    private Array<Action> actionQueue = new Array<Action>(false, 100);
    private Action spaceTriggeredAction;

    private Array<Disposable> resources = new Array<>();
    private PipelineRenderer pipelineRenderer;
    private DirectTextureLoader textureLoader;

    private Skin skin;
    private Stage stage;
    private Engine engine;
    private Camera camera;

    private ProfilerInfoProvider profilerInfoProvider;
    private PerformanceProfiler performanceProfiler;
    private Table centerTable;
    private Table bottomTable;
    private Label levelNumber;
    private Label levelTitle;
    private Label levelQuote;

    public GameScene(ProfilerInfoProvider profilerInfoProvider) {
        this.profilerInfoProvider = profilerInfoProvider;
    }

    @Override
    public void initializeScene() {
        //Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);

        performanceProfiler = new PerformanceProfiler(profilerInfoProvider);
        resources.add(performanceProfiler);

        textureLoader = new DirectTextureLoader();
        resources.add(textureLoader);

        skin = new Skin(Gdx.files.classpath("skin/default/uiskin.json"));
        resources.add(skin);

        stage = createStage();
        resources.add(stage);

        camera = createCamera();

        engine = setupEntitySystem();

        loadLevel("entities/level-1.json");
    }

    private Stage createStage() {
        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel(0.75f);
        Stage stage = new Stage(viewport);

        centerTable = new Table(skin);
        centerTable.setFillParent(true);

        levelNumber = new Label(null, skin);
        levelTitle = new Label(null, skin);
        levelQuote = new Label(null, skin);
        levelQuote.setWrap(true);
        levelQuote.setAlignment(Align.center);

        centerTable.add(levelNumber).center().row();
        centerTable.add(levelTitle).center().row();
        centerTable.add(levelQuote).growX().center().row();

        bottomTable = new Table(skin);
        bottomTable.bottom();
        bottomTable.setFillParent(true);

        bottomTable.add(new Label("Press SPACE to continue", skin)).center().bottom().row();

        return stage;
    }

    private void loadLevel(String level) {
        createEntities(level);
        engine.getSystem(InputControlSystem.class).setEnabled(false);
        for (Entity levelEntity : engine.getEntitiesFor(Family.all(LevelComponent.class).get())) {
            LevelComponent levelComponent = levelEntity.getComponent(LevelComponent.class);
            showLevelScreen(levelComponent.getLevelNumber(), levelComponent.getTitle(), levelComponent.getQuote());
        }
        GraphScreenShaders screenShaders = pipelineRenderer.getPluginData(GraphScreenShaders.class);
        screenShaders.setProperty("Blackout", "Alpha", 1f);

        spaceTriggeredAction = new Action() {
            @Override
            public boolean act() {
                hideLevelScreen();
                float fadeInLength = 1f;
                TimeProvider timeProvider = engine.getSystem(TimeSystem.class).getTimeProvider();
                actionQueue.add(new DelayedAction(timeProvider, fadeInLength,
                        new Runnable() {
                            @Override
                            public void run() {
                                engine.getSystem(InputControlSystem.class).setEnabled(true);
                            }
                        }));
                actionQueue.add(new FadeInAction(timeProvider, screenShaders, "Blackout", "Alpha", fadeInLength));
                return true;
            }
        };
    }

    private void showLevelScreen(int number, String title, String quote) {
        levelNumber.setText("Level " + number);
        levelTitle.setText(title);
        levelQuote.setText(quote);

        stage.addActor(centerTable);
        stage.addActor(bottomTable);
    }

    private void hideLevelScreen() {
        centerTable.remove();
        bottomTable.remove();
    }

    private void unloadLevelAndGoTo(String level) {
        GraphScreenShaders screenShaders = pipelineRenderer.getPluginData(GraphScreenShaders.class);
        float fadeOutLength = 1f;
        TimeProvider timeProvider = engine.getSystem(TimeSystem.class).getTimeProvider();
        engine.getSystem(InputControlSystem.class).setEnabled(false);
        actionQueue.add(new FadeOutAction(timeProvider, screenShaders, "Blackout", "Alpha", fadeOutLength));
        actionQueue.add(new DelayedAction(timeProvider, fadeOutLength,
                new Runnable() {
                    @Override
                    public void run() {
                        engine.removeAllEntities();
                        loadLevel(level);
                    }
                }));
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (spaceTriggeredAction != null) {
                actionQueue.add(spaceTriggeredAction);
                spaceTriggeredAction = null;
            }
        }

        for (Action action : actionQueue) {
            if (action.act())
                actionQueue.removeValue(action, true);
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

    private void createEntities(String level) {
        Json json = new AshleyEngineJson(engine);
        ClasspathFileHandleResolver fileHandleResolver = new ClasspathFileHandleResolver();
        if (fileHandleResolver.resolve(level).exists()) {
            createEntity(AshleyTemplateEntityLoader.loadTemplate("handWrittenEntities/player/player.json", json, fileHandleResolver));

            JsonValue levelJson = JsonTemplateLoader.loadTemplateFromFile(level, fileHandleResolver);
            for (JsonValue entity : levelJson.get("entities")) {
                createEntity(AshleyTemplateEntityLoader.convertToAshley(entity, json));
            }
        } else {
            Gdx.app.exit();
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

    private Engine setupEntitySystem() {
        Engine engine = new Engine();

        TimeSystem timeSystem = new TimeSystem(0);
        engine.addSystem(timeSystem);

        SpawnSystem spawnSystem = new SpawnSystem(1);
        engine.addSystem(spawnSystem);

        pipelineRenderer = loadPipeline(timeSystem.getTimeProvider(), camera, stage);
        resources.add(pipelineRenderer);

        InputControlSystem inputControlSystem = new InputControlSystem(2);
        engine.addSystem(inputControlSystem);

        LevelSetupSystem levelSetupSystem = new LevelSetupSystem(3, pipelineRenderer);
        engine.addSystem(levelSetupSystem);

        CombatSystem combatSystem = new CombatSystem(4);
        engine.addSystem(combatSystem);

        ActivateSystem activateSystem = new ActivateSystem(5);
        activateSystem.addActivateListener(
                new MoveToLevelActivateListener(
                        new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                unloadLevelAndGoTo(s);
                            }
                        }));
        engine.addSystem(activateSystem);

        FiniteStateSystem finiteStateSystem = new FiniteStateSystem(6);
        engine.addSystem(finiteStateSystem);

        Box2DSystem box2DSystem = new Box2DSystem(10, new Vector2(0, -15f), false, 100);
        box2DSystem.addSensorContactListener("groundContact", new ContactSensorContactListener(box2DSystem.getBitForCategory("Ground")));
        box2DSystem.addSensorContactListener("attackableList", new EntitySensorContactListener(box2DSystem.getBitForCategory("Attackable")));
        box2DSystem.addSensorContactListener("activator", new EntitySensorContactListener(box2DSystem.getBitForCategory("Activable")));
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

    private static PipelineRenderer loadPipeline(TimeProvider timeProvider, Camera camera, Stage stage) {
        FileHandle pipelineFile = Gdx.files.classpath("pipeline.json");
        try {
            InputStream is = pipelineFile.read();
            try {
                PipelineRenderer pipelineRenderer = GraphLoader.loadGraph(is, new PipelineLoaderCallback(timeProvider));
                pipelineRenderer.setPipelineProperty("Camera", camera);
                pipelineRenderer.getPluginData(UIPluginPublicData.class).setStage("", stage);
                return pipelineRenderer;
            } finally {
                is.close();
            }
        } catch (IOException exp) {
            throw new GdxRuntimeException("Unable to load pipeline", exp);
        }
    }
}
