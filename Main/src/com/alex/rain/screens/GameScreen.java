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

import com.alex.rain.stages.GameWorld;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private GameWorld world;

    public GameScreen(GameWorld world) {
        this.world = world;
    }

    @Override
    public void render(float delta) {
        /*camera.viewportHeight = 480;
        camera.viewportWidth = 800;
        camera.position.set(0, 0, 0f);*/
    }

    @Override
    public void resize(int width, int height) {
        /*camera.viewportWidth = 800;
        camera.viewportHeight = 480;
        camera.position.set(0, -10, 0f);*/
        //world.getSpriteBatch().setProjectionMatrix(camera.projection);
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
