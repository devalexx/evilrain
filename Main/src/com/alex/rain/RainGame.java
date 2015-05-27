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

import com.alex.rain.managers.SettingsManager;
import com.alex.rain.managers.TextureManager;
import com.alex.rain.screens.GameScreen;
import com.alex.rain.screens.SplashScreen;
import com.alex.rain.stages.EditableGameWorld;
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
    Stage stage;
    static RainGame instance = new RainGame();
    static float time;
    public static PolygonSpriteBatch polyBatch;
    public static ShapeRenderer shapeRenderer;
    public final static String VERSION = "0.1.1";

    private RainGame() {
    }

    public static RainGame getInstance() {
        return instance;
    }

    @Override
    public void create() {
        TextureManager.reload();
        polyBatch = new PolygonSpriteBatch();
        shapeRenderer = new ShapeRenderer();

        TextureManager.getAtlas("pack.atlas");

        //setLevel("level1", false);
        setScreen(new SplashScreen());
        Gdx.gl.glClearColor(0, 0, 0, 0);
        TextureManager.setLinearFilter(SettingsManager.getSmoothTextureType());
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
        setLevel(name, false);
    }

    public void setLevel(String name, boolean editable) {
        if(stage != null)
            stage.dispose();
        GameWorld gameWorld;
        if(!editable)
            gameWorld = new GameWorld(name);
        else
            gameWorld = new EditableGameWorld(name);
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
