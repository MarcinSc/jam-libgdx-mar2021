package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gempukku.jam.libgdx.march2021.component.LevelComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;
import com.gempukku.libgdx.graph.pipeline.PipelineRenderer;
import com.gempukku.libgdx.graph.plugin.ui.UIPluginPublicData;

public class UISystem extends EntitySystem implements Disposable {
    private Skin skin;
    private Stage stage;
    private Table centerTable;
    private Table bottomTable;
    private Label levelNumber;
    private Label levelTitle;
    private Label levelQuote;

    public UISystem(int priority) {
        super(priority);

        skin = new Skin(Gdx.files.classpath("skin/default/uiskin.json"));

        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel(0.75f);
        stage = new Stage(viewport);

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
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(LevelComponent.class).get(),
                new EntityListener() {
                    @Override
                    public void entityAdded(Entity entity) {
                        LevelComponent levelComponent = entity.getComponent(LevelComponent.class);
                        levelNumber.setText("Level " + levelComponent.getLevelNumber());
                        levelTitle.setText(levelComponent.getTitle());
                        levelQuote.setText(levelComponent.getQuote());
                    }

                    @Override
                    public void entityRemoved(Entity entity) {

                    }
                });
    }

    public void showLevelScreen() {
        stage.addActor(centerTable);
        stage.addActor(bottomTable);

        Engine engine = getEngine();

        PipelineRenderer pipelineRenderer = engine.getSystem(RenderingSystem.class).getPipelineRenderer();
        pipelineRenderer.getPluginData(UIPluginPublicData.class).setStage("", stage);
    }

    public void hideLevelScreen() {
        centerTable.remove();
        bottomTable.remove();
    }

    @Override
    public void dispose() {
        skin.dispose();
        stage.dispose();
    }
}
