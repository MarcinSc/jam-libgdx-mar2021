package com.gempukku.jam.libgdx.march2021.component;

import com.badlogic.ashley.core.Component;

public class AttackerComponent implements Component {
    private float lastAttacked;
    private int damage = 1;

    public float getLastAttacked() {
        return lastAttacked;
    }

    public void setLastAttacked(float lastAttacked) {
        this.lastAttacked = lastAttacked;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
