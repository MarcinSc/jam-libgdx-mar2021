package com.gempukku.jam.libgdx.march2021.system.level;

import com.badlogic.ashley.core.Engine;

public interface LevelLogic {
    void loadLogic(Engine engine);

    void unloadLogic(Engine engine);
}
