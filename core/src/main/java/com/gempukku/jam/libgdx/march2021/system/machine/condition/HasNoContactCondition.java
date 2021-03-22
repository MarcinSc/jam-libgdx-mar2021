package com.gempukku.jam.libgdx.march2021.system.machine.condition;


import com.gempukku.jam.libgdx.march2021.system.machine.EngineTriggerCondition;
import com.gempukku.jam.libgdx.march2021.system.sensor.ContactSensorData;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.Box2DBodyDataComponent;

public class HasNoContactCondition extends EngineTriggerCondition {
    private String sensor;

    @Override
    public boolean isTriggered() {
        Box2DBodyDataComponent physics = entity.getComponent(Box2DBodyDataComponent.class);
        ContactSensorData sensor = (ContactSensorData) physics.getSensorDataByName(this.sensor).getData();
        return sensor == null || !sensor.hasContact();
    }
}
