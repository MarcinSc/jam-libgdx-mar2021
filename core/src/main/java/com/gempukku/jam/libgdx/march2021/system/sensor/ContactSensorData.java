package com.gempukku.jam.libgdx.march2021.system.sensor;

public class ContactSensorData {
    private int contactCount = 0;

    public void incrementContactCount() {
        contactCount++;
    }

    public void decrementContactCount() {
        contactCount--;
    }

    public boolean hasContact() {
        return contactCount > 0;
    }
}
