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
package com.alex.rain.screens;

import com.alex.rain.RainGame;
import com.alex.rain.managers.ResourceManager;
import com.alex.rain.managers.TextureManager;
import com.alex.rain.viewports.GameViewport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class BasicUIScreen implements Screen {
    protected Stage stage;
    protected Texture backgroundTexture = TextureManager.getTexture("background.png");
    protected Skin skin = ResourceManager.getSkin();
    protected Actor mainUI;
    protected boolean debugRendererEnabled;

    public BasicUIScreen() {
        this(true);
    }

    public BasicUIScreen(Stage stage) {
        this.stage = stage;
    }

    public BasicUIScreen(boolean createStage) {
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        if(createStage) {
            stage = new Stage(new GameViewport());
            Gdx.input.setInputProcessor(stage);
        }
    }

    @Override
    public void render(float delta) {
        stage.getBatch().setColor(1, 1, 1, 1);
        stage.getBatch().begin();
            stage.getBatch().draw(backgroundTexture,
                    -(int) stage.getViewport().getWorldWidth(), -(int) stage.getViewport().getWorldHeight(), 0, 0,
                    (int) stage.getViewport().getWorldWidth() * 2, (int) stage.getViewport().getWorldHeight() * 2);
        stage.getBatch().end();
        stage.act(delta);
        stage.draw();

        if(debugRendererEnabled) {
            RainGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            if(mainUI != null)
                mainUI.drawDebug(RainGame.shapeRenderer);
            RainGame.shapeRenderer.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        if(stage != null) {
            stage.getViewport().update(width, height, true);
            stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
            RainGame.polyBatch.setProjectionMatrix(stage.getBatch().getProjectionMatrix());
            RainGame.shapeRenderer.setProjectionMatrix(stage.getBatch().getProjectionMatrix());

            if(stage.getViewport() instanceof GameViewport) {
                GameViewport gameViewport = (GameViewport)stage.getViewport();
                if(mainUI != null)
                    mainUI.setPosition(-gameViewport.offsetX, -gameViewport.offsetY);
            }
        }
    }

    @Override
    public void show() {
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
        stage.dispose();
    }
}
