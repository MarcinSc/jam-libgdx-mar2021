package com.gempukku.jam.libgdx.march2021.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class AnimateMoveAndDestroyComponent implements Component {
    private String id;
    private String spriteState;
    private Vector2 distance;
    private float time;
    private boolean executed;

    public String getId() {
        return id;
    }

    public String getSpriteState() {
        return spriteState;
    }

    public Vector2 getDistance() {
        return distance;
    }

    public float getTime() {
        return time;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }
}
