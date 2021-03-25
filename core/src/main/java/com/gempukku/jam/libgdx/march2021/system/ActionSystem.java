package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.gempukku.jam.libgdx.march2021.action.Action;

public class ActionSystem extends EntitySystem {
    private Array<Action> actionQueue = new Array<Action>(false, 100);
    private Action spaceTriggeredAction;

    public ActionSystem(int priority) {
        super(priority);
    }

    public void addAction(Action action) {
        actionQueue.add(action);
    }

    public void setSpaceTriggeredAction(Action action) {
        spaceTriggeredAction = action;
    }

    public void clearActions() {
        actionQueue.clear();
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (spaceTriggeredAction != null) {
                addAction(spaceTriggeredAction);
                spaceTriggeredAction = null;
            }
        }

        for (Action action : actionQueue) {
            if (action.act())
                actionQueue.removeValue(action, true);
        }
    }
}
