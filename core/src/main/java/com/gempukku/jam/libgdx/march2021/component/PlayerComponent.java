package com.gempukku.jam.libgdx.march2021.component;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    private boolean dead;

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
