package com.gempukku.jam.libgdx.march2021.system.sensor;

import com.badlogic.gdx.physics.box2d.Fixture;

public interface SensorContactListener<T> {
    T createNewSensorValue();

    void contactBegun(T sensorData, Fixture other);

    void contactEnded(T sensorData, Fixture other);
}
