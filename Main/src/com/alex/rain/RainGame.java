package com.alex.rain;

import com.alex.rain.screens.GameScreen;
import com.alex.rain.screens.MainMenuScreen;
import com.alex.rain.stages.GameWorld;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * @author: Alexander Shubenkov
 * @since: 28.05.13
 */

public class RainGame extends Game {
    Screen screen;
    Stage stage;
    static RainGame instance;
    static float time;
    static boolean lightVersion = true;

    private RainGame() {
    }

    public static RainGame getInstance() {
        if(instance == null)
            instance = new RainGame();
        return instance;
    }

    @Override
    public void create() {
        lightVersion = Gdx.app.getType() != Application.ApplicationType.Desktop;

        GameWorld gameWorld = new GameWorld("level1");
        gameWorld.createWorld();
        stage = gameWorld;
        Gdx.input.setInputProcessor(stage);

        screen = new GameScreen(gameWorld);
        setScreen(screen);
        //setScreen(new MainMenuScreen());
        Gdx.graphics.getGL20().glClearColor(0, 0, 0, 0);
    }

    @Override
    public void render() {
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if(stage != null) {
            stage.draw();
            stage.act(Gdx.graphics.getDeltaTime());
        }

        super.render();
        time += Gdx.graphics.getDeltaTime();
    }

    public void setLevel(String name) {
        if(stage != null)
            stage.dispose();
        GameWorld gameWorld = new GameWorld(name);
        gameWorld.createWorld();
        stage = gameWorld;
        Gdx.input.setInputProcessor(stage);

        screen = new GameScreen(gameWorld);
        setScreen(screen);
    }

    public void setMenu(Screen screen) {
        setScreen(screen);
        stage.dispose();
        stage = null;
    }

    public static float getTime() {
        return time;
    }

    public static boolean isLightVersion() {
        return lightVersion;
    }
}
