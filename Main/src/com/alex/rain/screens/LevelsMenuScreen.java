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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class LevelsMenuScreen extends BasicUIScreen {
    public LevelsMenuScreen() {
        super();

        final Table table = new Table();
        ScrollPane scrollPane = new ScrollPane(table);
        mainUI = scrollPane;
        scrollPane.debugAll();
        scrollPane.setFillParent(true);
        stage.addActor(scrollPane);

        table.row().width(400).padTop(10);

        final TextButton button0 = new TextButton("Test", skin);
        table.add(button0);

        table.row().width(400).padTop(10);

        for(int i = 1; i < 8; i++) {
            final TextButton button1 = new TextButton("Level " + String.valueOf(i), skin);
            table.add(button1);

            table.row().width(400).padTop(10);

            final String level = "level" + String.valueOf(i);
            button1.addListener(new ChangeListener() {
                @Override
                public void changed (ChangeEvent event, Actor actor) {
                    RainGame.getInstance().setLevel(level);
                }
            });
        }

        final TextButton buttonB = new TextButton("Back", skin);
        table.add(buttonB);

        button0.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                RainGame.getInstance().setLevel("test");
            }
        });

        buttonB.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                RainGame.getInstance().setScreen(new MainMenuScreen());
            }
        });
    }
}
