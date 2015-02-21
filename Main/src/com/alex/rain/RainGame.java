/*******************************************************************************
 * Copyright 2013 See AUTHORS file.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE V3
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.alex.rain;

import com.alex.rain.managers.TextureManager;
import com.alex.rain.screens.GameScreen;
import com.alex.rain.stages.GameWorld;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class RainGame extends Game {
    Screen screen;
    Stage stage;
    static RainGame instance = new RainGame();
    static float time;
    static boolean lightVersion = true;
    public static PolygonSpriteBatch polyBatch;
    public static ShapeRenderer shapeRenderer;

    private RainGame() {
    }

    public static RainGame getInstance() {
        return instance;
    }

    @Override
    public void create() {
        polyBatch = new PolygonSpriteBatch();
        shapeRenderer = new ShapeRenderer();

        lightVersion = Gdx.app.getType() != Application.ApplicationType.Desktop;
        TextureManager.getAtlas("pack.atlas");

        GameWorld gameWorld = new GameWorld("level2");
        gameWorld.createWorld();
        stage = gameWorld;
        Gdx.input.setInputProcessor(stage);

        screen = new GameScreen(gameWorld);
        setScreen(screen);
        //setScreen(new MainMenuScreen());
        Gdx.gl.glClearColor(0, 0, 0, 0);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
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
        Gdx.input.setCatchBackKey(true);

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

    @Override
    public void resume() {
        super.resume();
        TextureManager.reload();
    }

    @Override
    public void resize(int width, int height) {
        getScreen().resize(width, height);
    }
}
