package com.gempukku.jam.libgdx.march2021;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureArray;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.gempukku.libgdx.graph.plugin.sprites.SpritesPluginRuntimeInitializer;
import com.gempukku.libgdx.graph.plugin.ui.UIPluginRuntimeInitializer;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class JamMarch2021 extends ApplicationAdapter {
    private Scene currentScene;
    private ProfilerInfoProvider profilerInfoProvider;

    public JamMarch2021(ProfilerInfoProvider profilerInfoProvider) {
        this.profilerInfoProvider = profilerInfoProvider;
    }

    @Override
    public void create() {
        UIPluginRuntimeInitializer.register();
        SpritesPluginRuntimeInitializer.register();

        currentScene = new GameScene(profilerInfoProvider);
        currentScene.initializeScene();
    }

    @Override
    public void resize(int width, int height) {
        currentScene.resizeScene(width, height);
    }

    @Override
    public void render() {
        Scene nextScene = currentScene.getNextScene();
        if (nextScene != null) {
            currentScene.disposeScene();
            nextScene.initializeScene();

            currentScene = nextScene;
        }
        currentScene.renderScene();
    }

    @Override
    public void dispose() {
        currentScene.disposeScene();

        Gdx.app.debug("Unclosed", Cubemap.getManagedStatus());
        Gdx.app.debug("Unclosed", GLFrameBuffer.getManagedStatus());
        Gdx.app.debug("Unclosed", Mesh.getManagedStatus());
        Gdx.app.debug("Unclosed", Texture.getManagedStatus());
        Gdx.app.debug("Unclosed", TextureArray.getManagedStatus());
        Gdx.app.debug("Unclosed", ShaderProgram.getManagedStatus());
    }
}