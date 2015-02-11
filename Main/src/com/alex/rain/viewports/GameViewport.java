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
package com.alex.rain.viewports;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameViewport extends Viewport {
    public static final int WIDTH = 800, HEIGHT = 480;
    private float offsetX, offsetY;

    public GameViewport() {
        setCamera(new OrthographicCamera());
        setWorldSize(WIDTH, HEIGHT);
    }

    @Override
    public void update (int screenWidth, int screenHeight, boolean centerCamera) {
        Vector2 viewFit = Scaling.fit.apply(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                WIDTH, HEIGHT).cpy();
        Vector2 viewFill = Scaling.fill.apply(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                WIDTH, HEIGHT);

        setScreenBounds(0, 0, screenWidth, screenHeight);

        boolean isPortrait = viewFit.x - WIDTH < 0.1;
        boolean isLandscape = viewFit.y - HEIGHT < 0.1;
        offsetX = isLandscape ? viewFill.x / 2 - WIDTH / 2 : 0;
        offsetY = isPortrait ? viewFill.y / 2 - HEIGHT / 2 : 0;
        setWorldSize(isLandscape ? viewFill.x : WIDTH,
                isPortrait ? viewFill.y : HEIGHT);
        apply(centerCamera);
    }

    public void apply (boolean centerCamera) {
        Gdx.gl.glViewport(getScreenX(), getScreenY(), getScreenWidth(), getScreenHeight());
        getCamera().viewportWidth = getWorldWidth();
        getCamera().viewportHeight = getWorldHeight();
        if (centerCamera)
            getCamera().position.set(- offsetX + getWorldWidth() / 2, - offsetY + getWorldHeight() / 2, 0);
        getCamera().update();
    }
}
