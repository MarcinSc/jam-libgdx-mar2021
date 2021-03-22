package com.gempukku.jam.libgdx.march2021;

public interface Scene {
    void initializeScene();

    void renderScene();

    void resizeScene(int width, int height);

    Scene getNextScene();

    void disposeScene();
}
