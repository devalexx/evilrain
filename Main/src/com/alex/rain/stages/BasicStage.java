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
package com.alex.rain.stages;

import com.alex.rain.RainGame;
import com.alex.rain.screens.MainMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BasicStage extends Stage {
    public BasicStage(Viewport viewport) {
        super(viewport);
    }

    @Override
    public boolean keyDown(int keyCode) {
        if(keyCode == Input.Keys.ESCAPE || keyCode == Input.Keys.Q || keyCode == Input.Keys.BACK) {
            if(RainGame.getInstance().getScreen() instanceof MainMenuScreen)
                Gdx.app.exit();
            else
                RainGame.getInstance().setScreen(new MainMenuScreen());
        }

        return super.keyDown(keyCode);
    }
}
