package com.alex.rain;

import com.alex.rain.screens.GameScreen;
import com.alex.rain.screens.MainMenuScreen;
import com.alex.rain.stages.GameWorld;
import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * @author: Alexander Shubenkov
 * @since: 28.05.13
 */

public class RainGame extends Game {
    Screen screen;
    Stage stage;
    static RainGame instance;

    private RainGame() {

    }

    public static RainGame getInstance() {
        if(instance == null)
            instance = new RainGame();
        return instance;
    }

    @Override
    public void create() {
        GameWorld gameWorld = new GameWorld("level1");
        gameWorld.createWorld();
        stage = gameWorld;
        Gdx.input.setInputProcessor(stage);

        screen = new GameScreen(gameWorld);
        setScreen(screen);
        //setScreen(new MainMenuScreen());
    }

    @Override
    public void render() {
        super.render();

        if(stage != null) {
            stage.draw();
            stage.act(Gdx.graphics.getDeltaTime());
        }
    }

    public void setLevel(String name) {
        GameWorld gameWorld = new GameWorld(name);
        gameWorld.createWorld();
        stage = gameWorld;
        Gdx.input.setInputProcessor(stage);

        screen = new GameScreen(gameWorld);
        setScreen(screen);
    }
}
