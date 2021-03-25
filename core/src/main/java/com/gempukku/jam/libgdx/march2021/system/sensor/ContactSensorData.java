package com.gempukku.jam.libgdx.march2021.system.sensor;

import com.badlogic.ashley.core.Entity;

public class ContactSensorData {
    private Entity entity;
    private int contactCount = 0;

    public ContactSensorData(Entity entity) {
        this.entity = entity;
    }

    public void incrementContactCount() {
        contactCount++;
    }

    public void decrementContactCount() {
        contactCount--;
    }

    public boolean hasContact() {
        return contactCount > 0;
    }

    public Entity getEntity() {
        return entity;
    }
}
