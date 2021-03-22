package com.gempukku.jam.libgdx.march2021.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.gempukku.jam.libgdx.march2021.component.CameraFocusComponent;
import com.gempukku.jam.libgdx.march2021.component.LevelComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.FacingComponent;
import com.gempukku.libgdx.entity.editor.plugin.ashley.graph.component.PositionComponent;
import com.gempukku.libgdx.lib.camera2d.FocusCameraController;
import com.gempukku.libgdx.lib.camera2d.constraint.LockedToWindowCameraConstraint;
import com.gempukku.libgdx.lib.camera2d.constraint.SceneCameraConstraint;
import com.gempukku.libgdx.lib.camera2d.constraint.SnapToWindowCameraConstraint;
import com.gempukku.libgdx.lib.camera2d.focus.WeightBasedFocus;
import com.gempukku.libgdx.lib.camera2d.focus.WeightedCameraFocus;

public class CameraSystem extends EntitySystem {
    private WeightBasedFocus weightBasedFocus;

    private FocusCameraController cameraController;
    private Camera camera;
    private SceneCameraConstraint sceneCameraConstraint;
    private SnapToWindowCameraConstraint snapWindowConstraint;
    private LockedToWindowCameraConstraint lockWindowConstraint;

    public CameraSystem(int priority, Camera camera) {
        super(priority);
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        weightBasedFocus = new WeightBasedFocus();
        sceneCameraConstraint = new SceneCameraConstraint(new Rectangle());
        snapWindowConstraint = new SnapToWindowCameraConstraint(new Rectangle(), new Vector2(0.1f, 0.1f));
        lockWindowConstraint = new LockedToWindowCameraConstraint(new Rectangle());

        cameraController = new FocusCameraController(camera,
                weightBasedFocus,
                snapWindowConstraint,
                // Make sure the focus point is going to appear in the specified window
                lockWindowConstraint,
                sceneCameraConstraint);

        Family cameraFocus = Family.all(CameraFocusComponent.class).get();
        engine.addEntityListener(cameraFocus, new CameraFocusListener());

        Family levels = Family.all(LevelComponent.class).get();
        engine.addEntityListener(levels, new LevelListener());
    }

    @Override
    public void update(float deltaTime) {
        cameraController.update(deltaTime);
    }

    private class CameraFocusListener implements EntityListener {
        @Override
        public void entityAdded(Entity entity) {
            CameraFocusComponent cameraFocus = entity.getComponent(CameraFocusComponent.class);

            WeightedCameraFocus weightedCameraFocus = new WeightedCameraFocus() {
                @Override
                public float getWeight() {
                    return cameraFocus.getWeight();
                }

                @Override
                public Vector2 getFocus(Vector2 focus) {
                    PositionComponent position = entity.getComponent(PositionComponent.class);
                    float positionX = position.getX();
                    float positionY = position.getY();
                    positionX += entity.getComponent(FacingComponent.class).getFaceDirection().getDirection() * cameraFocus.getAdvance();
                    return focus.set(positionX, positionY);
                }
            };

            weightBasedFocus.addFocus(weightedCameraFocus);
            cameraFocus.setFocus(weightedCameraFocus);
        }

        @Override
        public void entityRemoved(Entity entity) {
            CameraFocusComponent cameraFocus = entity.getComponent(CameraFocusComponent.class);
            WeightedCameraFocus focus = cameraFocus.getFocus();
            weightBasedFocus.removeFocus(focus);
        }
    }

    private class LevelListener implements EntityListener {
        @Override
        public void entityAdded(Entity entity) {
            LevelComponent level = entity.getComponent(LevelComponent.class);

            sceneCameraConstraint.setBounds(level.getBounds());
            snapWindowConstraint.setWindow(level.getSnapWindow());
            snapWindowConstraint.setSpeed(level.getSnapSpeed());
            lockWindowConstraint.setWindow(level.getLockWindow());
        }

        @Override
        public void entityRemoved(Entity entity) {

        }
    }
}
