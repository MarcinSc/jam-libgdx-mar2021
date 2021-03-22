package com.gempukku.jam.libgdx.march2021;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.gempukku.libgdx.graph.util.SimpleNumberFormatter;

public class PerformanceProfiler implements Disposable {
    private boolean profile = false;
    private GLProfiler profiler;
    private Skin profileSkin;
    private Stage profileStage;
    private Label profileLabel;

    private long start;

    private Box2DDebugRenderer debugRenderer;
    private Matrix4 tmpMatrix = new Matrix4();

    private ProfilerInfoProvider profilerInfoProvider;

    public PerformanceProfiler(ProfilerInfoProvider profilerInfoProvider) {
        this.profilerInfoProvider = profilerInfoProvider;
    }

    public void toggle() {
        if (profile)
            disable();
        else
            enable();
    }

    public void enable() {
        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();

        profileSkin = new Skin(Gdx.files.classpath("skin/default/uiskin.json"));
        profileStage = new Stage();
        profileLabel = new Label("", profileSkin);

        Table tbl = new Table(profileSkin);

        tbl.setFillParent(true);
        tbl.align(Align.topRight);

        tbl.add(profileLabel).pad(10f);
        tbl.row();

        profileStage.addActor(tbl);

        debugRenderer = new Box2DDebugRenderer();

        profile = true;
    }

    public void disable() {
        profileSkin.dispose();
        profileStage.dispose();

        profiler.disable();

        debugRenderer.dispose();

        profile = false;
    }

    public void beforeDraw() {
        if (profile) {
            profiler.reset();
            start = profilerInfoProvider.getNanoTime();
        }
    }

    public void afterDraw(Camera camera, World world, float scale) {
        if (profile) {
            float ms = (profilerInfoProvider.getNanoTime() - start) / 1000000f;

            StringBuilder sb = new StringBuilder();
            sb.append("Time: " + SimpleNumberFormatter.format(ms) + "ms\n");
            sb.append("FPS: " + Gdx.graphics.getFramesPerSecond() + "\n");
            sb.append("GL Calls: " + profiler.getCalls() + "\n");
            sb.append("Draw calls: " + profiler.getDrawCalls() + "\n");
            sb.append("Shader switches: " + profiler.getShaderSwitches() + "\n");
            sb.append("Texture bindings: " + profiler.getTextureBindings() + "\n");
            sb.append("Vertex calls: " + profiler.getVertexCount().total + "\n");
            long memory = profilerInfoProvider.getUsedMemory();
            sb.append("Used memory: " + memory + "MB");

            tmpMatrix.set(camera.combined).scale(scale, scale, scale);
            debugRenderer.render(world, tmpMatrix);

            profileLabel.setText(sb.toString());

            profileStage.draw();
        }
    }

    @Override
    public void dispose() {
        if (profile) {
            disable();
        }
    }
}
