package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.jam.libgdx.march2021.component.FiniteStateComponent;
import com.gempukku.jam.libgdx.march2021.system.machine.ContainerTriggerCondition;
import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerCondition;
import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerState;
import com.gempukku.libgdx.lib.fst.FiniteStateMachine;
import com.gempukku.libgdx.lib.fst.MachineState;
import com.gempukku.libgdx.lib.fst.TriggerCondition;
import com.gempukku.libgdx.lib.fst.TriggerMachineState;
import com.gempukku.libgdx.lib.fst.TriggerState;

public class FiniteStateSystem extends EntitySystem {
    private ImmutableArray<Entity> finiteStateEntities;

    private boolean enabled = true;

    public FiniteStateSystem(int priority) {
        super(priority);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void addedToEngine(Engine engine) {
        Family family = Family.all(FiniteStateComponent.class).get();
        engine.addEntityListener(family,
                new EntityListener() {
                    @Override
                    public void entityAdded(Entity entity) {
                        FiniteStateComponent finiteState = entity.getComponent(FiniteStateComponent.class);
                        String initialState = finiteState.getInitialState();

                        FiniteStateMachine machine = new FiniteStateMachine(initialState);

                        for (String state : finiteState.getMachineStates().keys()) {
                            machine.addState(state, createMachineState(engine, state, finiteState, entity));
                        }

                        finiteState.setFiniteStateMachine(machine);
                    }

                    @Override
                    public void entityRemoved(Entity entity) {

                    }
                });
        finiteStateEntities = engine.getEntitiesFor(family);
    }

    private MachineState createMachineState(Engine engine, String state, FiniteStateComponent finiteState, Entity entity) {
        TriggerState triggerState = finiteState.getMachineStates().get(state);
        initializeState(engine, entity, triggerState);

        TriggerMachineState machineState = new TriggerMachineState(triggerState);
        ObjectMap<String, TriggerCondition> transitions = finiteState.getTransitions().get(state);
        for (ObjectMap.Entry<String, TriggerCondition> transition : transitions) {
            String targetState = transition.key;
            initializeTransition(engine, entity, transition.value);
            machineState.addTransition(targetState, transition.value);
        }
        return machineState;
    }

    private void initializeTransition(Engine engine, Entity entity, TriggerCondition triggerCondition) {
        if (triggerCondition instanceof EngineTriggerCondition)
            ((EngineTriggerCondition) triggerCondition).init(entity, engine);
        if (triggerCondition instanceof ContainerTriggerCondition) {
            for (TriggerCondition child : ((ContainerTriggerCondition) triggerCondition).getChildren()) {
                initializeTransition(engine, entity, child);
            }
        }
    }

    private void initializeState(Engine engine, Entity entity, TriggerState machineState) {
        if (machineState instanceof EngineTriggerState)
            ((EngineTriggerState) machineState).init(entity, engine);
    }

    @Override
    public void update(float deltaTime) {
        if (enabled) {
            for (Entity finiteStateEntity : finiteStateEntities) {
                FiniteStateComponent finiteStateComponent = finiteStateEntity.getComponent(FiniteStateComponent.class);
                finiteStateComponent.getFiniteStateMachine().update(deltaTime);
            }
        }
    }
}
