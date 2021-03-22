package com.gempukku.jam.libgdx.march2021.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Queue;

public class InputControlledComponent implements Component {
    // Loaded from JSON
    private ObjectMap<String, String> triggerKeyMapping;
    private ObjectMap<String, String> stateKeys;

    // Internal state
    private transient Queue<InputEntry> inputs = new Queue<>();
    private transient ObjectMap<String, Integer> stateKeyIndex;
    private transient boolean[] keyStates;

    public ObjectMap<String, String> getTriggerKeyMapping() {
        return triggerKeyMapping;
    }

    public ObjectMap<String, String> getStateKeys() {
        return stateKeys;
    }

    public Queue<InputEntry> getInputs() {
        return inputs;
    }

    public void setKeyState(String state, boolean value) {
        initStateKeyIndex();
        keyStates[stateKeyIndex.get(state)] = value;
    }

    public boolean getKeyState(String state) {
        initStateKeyIndex();
        return keyStates[stateKeyIndex.get(state)];
    }

    private void initStateKeyIndex() {
        if (stateKeyIndex == null) {
            stateKeyIndex = new ObjectMap<>();
            int index = 0;
            for (ObjectMap.Entry<String, String> stateKey : new ObjectMap.Entries<>(stateKeys)) {
                stateKeyIndex.put(stateKey.key, index);
                index++;
            }
            this.keyStates = new boolean[stateKeyIndex.size];
        }
    }

    public static class InputEntry {
        private float time;
        private String type;

        public InputEntry(float time, String type) {
            this.time = time;
            this.type = type;
        }

        public float getTime() {
            return time;
        }

        public String getType() {
            return type;
        }
    }
}
