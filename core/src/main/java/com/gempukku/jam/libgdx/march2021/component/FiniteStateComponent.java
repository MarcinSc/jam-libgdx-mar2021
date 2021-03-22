package com.gempukku.jam.libgdx.march2021.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.fst.FiniteStateMachine;
import com.gempukku.libgdx.lib.fst.TriggerCondition;
import com.gempukku.libgdx.lib.fst.TriggerState;

public class FiniteStateComponent implements Component {
    private String initialState;
    private ObjectMap<String, TriggerState> machineStates;
    private ObjectMap<String, ObjectMap<String, TriggerCondition>> transitions;

    // Internal state
    private transient FiniteStateMachine finiteStateMachine;

    public String getInitialState() {
        return initialState;
    }

    public ObjectMap<String, TriggerState> getMachineStates() {
        return machineStates;
    }

    public ObjectMap<String, ObjectMap<String, TriggerCondition>> getTransitions() {
        return transitions;
    }

    public FiniteStateMachine getFiniteStateMachine() {
        return finiteStateMachine;
    }

    public void setFiniteStateMachine(FiniteStateMachine finiteStateMachine) {
        this.finiteStateMachine = finiteStateMachine;
    }
}
