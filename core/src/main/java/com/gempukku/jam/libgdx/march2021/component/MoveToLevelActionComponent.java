package com.gempukku.jam.libgdx.march2021.component;

import com.badlogic.ashley.core.Component;

public class MoveToLevelActionComponent implements Component {
    private String level;
    private boolean executed;

    public String getLevel() {
        return level;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }
}
