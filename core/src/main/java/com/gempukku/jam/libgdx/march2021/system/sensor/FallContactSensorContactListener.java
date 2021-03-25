package com.gempukku.jam.libgdx.march2021.system.sensor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;

public class FallContactSensorContactListener extends ContactSensorContactListener {
    public FallContactSensorContactListener(short collisionBits) {
        super(collisionBits);
    }

    @Override
    public FallContactSensorData createNewSensorValue(Entity entity) {
        return new FallContactSensorData(entity);
    }

    @Override
    public void preSolve(ContactSensorData sensorData, Fixture other) {
        ((FallContactSensorData) sensorData).investigateFallOn(other);
    }
}
