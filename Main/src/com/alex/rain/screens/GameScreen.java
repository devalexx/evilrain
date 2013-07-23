package com.alex.rain.screens;

import com.alex.rain.stages.GameWorld;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

/**
 * @author: Alexander Shubenkov
 * @since: 29.05.13
 */

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private GameWorld world;

    public GameScreen(GameWorld world) {
        this.world = world;
    }

    @Override
    public void render(float delta) {
        camera.viewportHeight = 480;
        camera.viewportWidth = 800;
        camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);
    }

    @Override
    public void resize(int i, int i2) {
    }

    @Override
    public void show() {
        camera = (OrthographicCamera) world.getCamera();
        camera.viewportHeight = 480;
        camera.viewportWidth = 800;

        camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
