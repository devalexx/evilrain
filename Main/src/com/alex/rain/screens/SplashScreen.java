/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
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
import com.alex.rain.managers.TextureManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;

public class SplashScreen extends BasicUIScreen {
    private Timer autoStartTimer = new Timer();
    private Image splashScreenImage;
    private boolean touched;

    public SplashScreen() {
        super();
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        autoStartTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                next();
            }
        }, 5);

        Table table = new Table();
        mainUI = table;
        table.setFillParent(true);
        Texture splashScreenTexture = TextureManager.getTexture("splashscreen.png");
        splashScreenImage = new Image(splashScreenTexture);
        table.add(splashScreenImage);
        stage.addActor(table);
    }

    private void next() {
        if(touched)
            return;

        touched = true;

        autoStartTimer.clear();

        splashScreenImage.addAction(Actions.fadeOut(1));
        autoStartTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                RainGame.getInstance().setScreen(new MainMenuScreen());
                dispose();
            }
        }, 1);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if(Gdx.input.isTouched())
            next();
    }
}
