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
package com.alex.rain.ui;

import com.alex.rain.RainGame;
import com.alex.rain.managers.I18nManager;
import com.alex.rain.screens.LevelsMenuScreen;
import com.alex.rain.viewports.GameViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuWindow extends Window {
    private boolean won;
    private TextButton nextOrContinueButton;

    public MenuWindow(boolean wonGame, Skin skin, final int levelNumber) {
        super(wonGame ? I18nManager.getString("VICTORY") + "!" : I18nManager.getString("MENU"), skin);
        won = wonGame;
        setSize(GameViewport.WIDTH / 1.5f, GameViewport.HEIGHT / 1.5f);
        setPosition(GameViewport.WIDTH / 2f - getWidth() / 2f,
                GameViewport.HEIGHT / 2f - getHeight() / 2f);
        setModal(true);
        setMovable(false);
        setKeepWithinStage(false);
        //debug();

        row().width(400).padTop(10);

        nextOrContinueButton = new TextButton(I18nManager.getString(!wonGame ? "CONTINUE" : "NEXT"), skin);
        add(nextOrContinueButton);

        row().width(400).padTop(10);

        final TextButton restartButton = new TextButton(I18nManager.getString("RESTART"), skin);
        add(restartButton);

        row().width(400).padTop(10);

        final TextButton mainMenuButton = new TextButton(I18nManager.getString("LEVELS"), skin);
        add(mainMenuButton);

        nextOrContinueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(won)
                    RainGame.getInstance().setLevel("level" + (levelNumber + 1));
                else
                    setVisible(false);
            }
        });

        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RainGame.getInstance().setLevel("level" + levelNumber);
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RainGame.getInstance().setMenu(new LevelsMenuScreen());
            }
        });

        toFront();
    }

    public void update(boolean wonGame) {
        won = wonGame;

        setTitle(won ? I18nManager.getString("VICTORY") + "!" : I18nManager.getString("MENU"));
        nextOrContinueButton.setText(I18nManager.getString(!wonGame ? "CONTINUE" : "NEXT"));
    }
}
