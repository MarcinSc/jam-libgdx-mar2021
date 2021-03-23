package com.gempukku.jam.libgdx.march2021.system.activate;

import com.badlogic.ashley.core.Entity;

public interface ActivateListener {
    void activate(Entity activator, Entity activated);
}
