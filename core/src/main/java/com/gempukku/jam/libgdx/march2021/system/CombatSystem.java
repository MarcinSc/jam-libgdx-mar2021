package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;

public class CombatSystem extends EntitySystem {
    public CombatSystem(int priority) {
        super(priority);
    }

    public void executeAttack(Entity attacker, String attackSensor) {
        System.out.println("Execute attack");
    }
}
