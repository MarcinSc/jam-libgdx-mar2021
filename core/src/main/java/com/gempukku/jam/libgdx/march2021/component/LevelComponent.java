package com.gempukku.jam.libgdx.march2021.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class LevelComponent implements Component {
    private Rectangle bounds;
    private Rectangle snapWindow;
    private Vector2 snapSpeed;
    private Rectangle lockWindow;

    public Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getSnapWindow() {
        return snapWindow;
    }

    public Vector2 getSnapSpeed() {
        return snapSpeed;
    }

    public Rectangle getLockWindow() {
        return lockWindow;
    }
}
