package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Queue;
import com.gempukku.jam.libgdx.march2021.component.InputControlledComponent;

public class InputControlSystem extends EntitySystem {
    private ImmutableArray<Entity> inputControlledEntities;
    private Engine engine;

    public InputControlSystem(int priority) {
        super(priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        Family family = Family.all(InputControlledComponent.class).get();
        inputControlledEntities = engine.getEntitiesFor(family);
    }

    @Override
    public void update(float deltaTime) {
        float time = engine.getSystem(TimeSystem.class).getTimeProvider().getTime();
        for (Entity playerControllerEntity : inputControlledEntities) {
            InputControlledComponent playerController = playerControllerEntity.getComponent(InputControlledComponent.class);
            Queue<InputControlledComponent.InputEntry> inputs = playerController.getInputs();
            for (ObjectMap.Entry<String, String> keyMapping : playerController.getTriggerKeyMapping()) {
                String action = keyMapping.key;
                String[] keys = keyMapping.value.split("\\|");
                if (isAnyJustPressed(keys)) {
                    inputs.addLast(new InputControlledComponent.InputEntry(time, action));
                    if (inputs.size > 10)
                        inputs.removeFirst();
                }
            }
            for (ObjectMap.Entry<String, String> stateKey : playerController.getStateKeys()) {
                String state = stateKey.key;
                String[] keys = stateKey.value.split("\\|");
                playerController.setKeyState(state, isAnyPressed(keys));
            }
        }
    }

    private boolean isAnyPressed(String[] keys) {
        for (String key : keys) {
            if (Gdx.input.isKeyPressed(Input.Keys.valueOf(key)))
                return true;
        }
        return false;
    }

    private boolean isAnyJustPressed(String[] keys) {
        for (String key : keys) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.valueOf(key)))
                return true;
        }
        return false;
    }
}
