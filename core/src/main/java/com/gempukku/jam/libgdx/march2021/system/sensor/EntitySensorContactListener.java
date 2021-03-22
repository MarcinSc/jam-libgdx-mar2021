package com.gempukku.jam.libgdx.march2021.system.sensor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;

public class EntitySensorContactListener implements SensorContactListener<EntitySensorData> {
    private short collisionBits;

    public EntitySensorContactListener(short collisionBits) {
        this.collisionBits = collisionBits;
    }

    @Override
    public EntitySensorData createNewSensorValue() {
        return new EntitySensorData();
    }

    @Override
    public void contactBegun(EntitySensorData sensorData, Fixture other) {
        if ((other.getFilterData().categoryBits & collisionBits) > 0) {
            sensorData.addContactEntity((Entity) other.getUserData());
        }
    }

    @Override
    public void contactEnded(EntitySensorData sensorData, Fixture other) {
        if ((other.getFilterData().categoryBits & collisionBits) > 0) {
            sensorData.removeContactEntity((Entity) other.getUserData());
        }
    }
}
