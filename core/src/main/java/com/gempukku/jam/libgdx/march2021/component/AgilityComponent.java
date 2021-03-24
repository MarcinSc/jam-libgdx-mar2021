package com.gempukku.jam.libgdx.march2021.component;

import com.badlogic.ashley.core.Component;

public class AgilityComponent implements Component {
    private float walkSpeed;
    private float jumpSpeed;

    public float getWalkSpeed() {
        return walkSpeed;
    }

    public float getJumpSpeed() {
        return jumpSpeed;
    }
}
