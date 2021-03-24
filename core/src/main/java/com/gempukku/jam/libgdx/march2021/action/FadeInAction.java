package com.gempukku.jam.libgdx.march2021.action;

import com.gempukku.libgdx.graph.plugin.screen.GraphScreenShaders;
import com.gempukku.libgdx.graph.time.TimeProvider;

public class FadeInAction implements Action {
    private TimeProvider timeProvider;
    private GraphScreenShaders graphScreenShaders;
    private String tag;
    private String property;
    private float time;

    private float start;

    public FadeInAction(TimeProvider timeProvider, GraphScreenShaders graphScreenShaders,
                        String tag, String property,
                        float time) {
        this.timeProvider = timeProvider;
        this.graphScreenShaders = graphScreenShaders;
        this.tag = tag;
        this.property = property;
        this.time = time;

        this.start = timeProvider.getTime();
    }

    @Override
    public boolean act() {
        float currentTime = timeProvider.getTime();
        if (currentTime >= start + this.time) {
            graphScreenShaders.setProperty(tag, property, 0);
            return true;
        }
        graphScreenShaders.setProperty(tag, property, (float) Math.pow(1 - (currentTime - start) / time, 2));
        return false;
    }
}
