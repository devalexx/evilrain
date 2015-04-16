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
import com.alex.rain.managers.I18nManager;
import com.alex.rain.managers.SettingsManager;
import com.alex.rain.managers.SoundManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen extends BasicUIScreen {
    int c = 0;

    public MainMenuScreen() {
        super();
        SoundManager.playMusic(SoundManager.MENU_MUSIC);
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        final Table table = new Table();
        mainUI = table;
        table.setFillParent(true);
        stage.addActor(table);

        Label gameLabel = new Label(I18nManager.getString("GAME_NAME"), skin);
        table.add(gameLabel).expand();

        table.row().width(400).padTop(10);

        final TextButton button = new TextButton(I18nManager.getString("START_GAME"), skin);
        table.add(button);
        button.setPosition(0, -100);

        table.row().width(400).padTop(10);

        final TextButton button2 = new TextButton(I18nManager.getString("OPTIONS"), skin);
        table.add(button2);
        button2.setPosition(0, 0);

        table.row().width(400).padTop(10);

        final TextButton aboutButton = new TextButton(I18nManager.getString("ABOUT"), skin);
        table.add(aboutButton);

        table.row().width(400).padTop(10);

        final TextButton button3 = new TextButton(I18nManager.getString("QUIT"), skin);
        table.add(button3);
        button3.setPosition(0, 100);

        table.row().padTop(10);

        Label versionLabel = new Label("v." + RainGame.VERSION, skin);
        table.add(versionLabel).expand().right().bottom();

        button.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                RainGame.getInstance().setScreen(new LevelsMenuScreen());
            }
        });

        button2.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                RainGame.getInstance().setScreen(new OptionsScreen());
            }
        });

        button3.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        aboutButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                RainGame.getInstance().setScreen(new AboutScreen());
            }
        });

        gameLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(c++ > 10)
                    SettingsManager.setMaxCompletedLevel(1000);
            }
        });
    }


}
