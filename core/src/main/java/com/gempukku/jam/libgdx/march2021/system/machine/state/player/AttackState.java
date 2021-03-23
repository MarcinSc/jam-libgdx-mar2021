package com.gempukku.jam.libgdx.march2021.system.machine.state.player;

import com.gempukku.jam.libgdx.march2021.component.AttackerComponent;
import com.gempukku.jam.libgdx.march2021.system.CombatSystem;
import com.gempukku.jam.libgdx.march2021.system.TimeSystem;
import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerState;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.SpriteStateComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.RenderingSystem;

public class AttackState extends EngineTriggerState {
    private float attackAfter;
    private String attackSensor;

    private float attackStart;
    private boolean attackMade;

    @Override
    public void transitioningTo(String newState) {

    }

    @Override
    public void transitioningFrom(String oldState) {
        float time = engine.getSystem(TimeSystem.class).getTimeProvider().getTime();
        AttackerComponent attacker = entity.getComponent(AttackerComponent.class);
        attacker.setLastAttacked(time);
        SpriteStateComponent spriteState = entity.getComponent(SpriteStateComponent.class);
        spriteState.setState("Attack");
        engine.getSystem(RenderingSystem.class).updateSprite(entity);

        attackStart = time;
        attackMade = false;
    }

    @Override
    public void update(float delta) {
        if (!attackMade) {
            float time = engine.getSystem(TimeSystem.class).getTimeProvider().getTime();
            if (attackStart + attackAfter <= time) {
                engine.getSystem(CombatSystem.class).executeAttack(entity, attackSensor);
                attackMade = true;
            }
        }
    }
}
