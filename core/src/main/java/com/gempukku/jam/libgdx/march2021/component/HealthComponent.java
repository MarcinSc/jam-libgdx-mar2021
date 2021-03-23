package com.gempukku.jam.libgdx.march2021.component;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {
    private int maxHealth;
    private int health;

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
