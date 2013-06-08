package com.alex.rain;

import com.alex.rain.screens.GameScreen;
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

    @Override
    public void create() {
        GameWorld gameWorld = new GameWorld();
        gameWorld.createWorld();
        stage = gameWorld;
        Gdx.input.setInputProcessor(stage);

        screen = new GameScreen(gameWorld);
        setScreen(screen);
    }

    @Override
    public void render() {
        super.render();
        stage.draw();
        stage.act(Gdx.graphics.getDeltaTime());
        //screen.render(1);
    }
}
