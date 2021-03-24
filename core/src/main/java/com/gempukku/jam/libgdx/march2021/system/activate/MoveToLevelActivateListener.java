package com.gempukku.jam.libgdx.march2021.system.activate;

import com.badlogic.ashley.core.Entity;
import com.gempukku.jam.libgdx.march2021.component.MoveToLevelActionComponent;

import java.util.function.Consumer;

public class MoveToLevelActivateListener implements ActivateListener {
    private Consumer<String> levelConsumer;

    public MoveToLevelActivateListener(Consumer<String> levelConsumer) {
        this.levelConsumer = levelConsumer;
    }

    @Override
    public void activate(Entity activator, Entity activated) {
        MoveToLevelActionComponent moveToLevel = activated.getComponent(MoveToLevelActionComponent.class);
        if (moveToLevel != null && !moveToLevel.isExecuted()) {
            moveToLevel.setExecuted(true);
            levelConsumer.accept(moveToLevel.getLevel());
        }
    }
}
