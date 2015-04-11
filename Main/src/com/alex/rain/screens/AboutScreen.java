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
import com.alex.rain.managers.SoundManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class AboutScreen extends BasicUIScreen {
    public AboutScreen() {
        super();
        SoundManager.playMusic(SoundManager.MENU_MUSIC);

        final Table table = new Table();
        final Table scrollPaneTable = new Table();
        ScrollPane scrollPane = new ScrollPane(scrollPaneTable, skin);
        mainUI = table;
        table.setFillParent(true);
        scrollPane.setFadeScrollBars(false);
        stage.addActor(table);

        final TextButton buttonBackTop = new TextButton(I18nManager.getString("BACK"), skin);
        table.add(buttonBackTop).left().width(200);

        Label gameLabel = new Label(I18nManager.getString("ABOUT"), skin);
        table.add(gameLabel).left();

        table.row();

        table.add(scrollPane).colspan(2).expand().fill();

        Label aboutLabel = new Label(I18nManager.getString("ABOUT_TEXT"), skin);
        aboutLabel.setWrap(true);
        aboutLabel.setAlignment(Align.center);
        scrollPaneTable.add(aboutLabel).expand();

        buttonBackTop.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RainGame.getInstance().setScreen(new MainMenuScreen());
            }
        });
    }
}
