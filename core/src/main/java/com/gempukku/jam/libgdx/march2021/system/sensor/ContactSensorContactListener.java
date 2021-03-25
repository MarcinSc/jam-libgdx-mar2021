package com.gempukku.jam.libgdx.march2021.system.sensor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.system.SensorContactListener;

public class ContactSensorContactListener implements SensorContactListener<ContactSensorData> {
    private short collisionBits;

    public ContactSensorContactListener(short collisionBits) {
        this.collisionBits = collisionBits;
    }

    @Override
    public ContactSensorData createNewSensorValue(Entity entity) {
        return new ContactSensorData(entity);
    }

    @Override
    public void contactBegun(ContactSensorData sensorData, Fixture other) {
        if ((other.getFilterData().categoryBits & collisionBits) > 0) {
            sensorData.incrementContactCount();
        }
    }

    @Override
    public void contactEnded(ContactSensorData sensorData, Fixture other) {
        if ((other.getFilterData().categoryBits & collisionBits) > 0) {
            sensorData.decrementContactCount();
        }
    }

    @Override
    public void preSolve(ContactSensorData sensorData, Fixture other) {

    }
}
