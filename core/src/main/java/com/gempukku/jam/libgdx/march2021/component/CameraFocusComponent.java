package com.gempukku.jam.libgdx.march2021.component;

import com.badlogic.ashley.core.Component;
import com.gempukku.libgdx.lib.camera2d.focus.WeightedCameraFocus;

public class CameraFocusComponent implements Component {
    private float weight;
    private float advance;
    private transient WeightedCameraFocus focus;

    public float getWeight() {
        return weight;
    }

    public float getAdvance() {
        return advance;
    }

    public void setFocus(WeightedCameraFocus focus) {
        this.focus = focus;
    }

    public WeightedCameraFocus getFocus() {
        return focus;
    }
}
