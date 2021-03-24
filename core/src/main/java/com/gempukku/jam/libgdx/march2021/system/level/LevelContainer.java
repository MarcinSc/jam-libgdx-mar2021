package com.gempukku.jam.libgdx.march2021.system.level;

import com.badlogic.gdx.utils.IntMap;

public class LevelContainer {
    private IntMap<LevelLogic> levelLogicMap = new IntMap<>();

    public void addLevelLogic(int number, LevelLogic levelLogic) {
        levelLogicMap.put(number, levelLogic);
    }

    public LevelLogic getLevelLogic(int levelNumber) {
        return levelLogicMap.get(levelNumber);
    }
}
