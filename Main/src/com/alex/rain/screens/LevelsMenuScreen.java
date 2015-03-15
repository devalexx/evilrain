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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.io.File;
import java.io.FilenameFilter;

public class LevelsMenuScreen extends BasicUIScreen {
    public LevelsMenuScreen() {
        super();

        final Table table = new Table();
        final Table scrollPaneTable = new Table();
        scrollPaneTable.defaults().width(400).padTop(10);
        ScrollPane scrollPane = new ScrollPane(scrollPaneTable, skin);
        mainUI = table;
        table.setFillParent(true);
        scrollPane.setFadeScrollBars(false);
        stage.addActor(table);

        final TextButton buttonBackTop = new TextButton(I18nManager.getString("BACK"), skin);
        table.add(buttonBackTop).left().width(200);

        Label gameLabel = new Label(I18nManager.getString("LEVELS"), skin);
        table.add(gameLabel).left();

        table.row();

        table.add(scrollPane).colspan(2).expandX().fill();

        FileHandle[] levels = Gdx.files.internal("data/levels").list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("level");
            }
        });

        for(int i = 1; i < levels.length + 1; i++) {
            final TextButton button1 = new TextButton(I18nManager.getString("LEVEL") + " " + String.valueOf(i), skin);
            scrollPaneTable.add(button1);

            scrollPaneTable.row();

            final String level = "level" + String.valueOf(i);
            button1.addListener(new ChangeListener() {
                @Override
                public void changed (ChangeEvent event, Actor actor) {
                    RainGame.getInstance().setLevel(level);
                }
            });
        }

        final TextButton button0 = new TextButton(I18nManager.getString("TEST") + " " + I18nManager.getString("LEVEL"), skin);
        scrollPaneTable.add(button0).padBottom(10);

        button0.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                RainGame.getInstance().setLevel("test");
            }
        });

        buttonBackTop.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RainGame.getInstance().setScreen(new MainMenuScreen());
            }
        });
    }
}
