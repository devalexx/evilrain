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

import com.alex.rain.managers.I18nManager;
import com.alex.rain.managers.TextureManager;
import com.alex.rain.stages.GameWorld;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class GameUI extends Table {
    public ImageButton actionButton, arrowUpButton, arrowDownButton, arrowLeftButton ,arrowRightButton;
    
    public GameUI(final GameWorld world, Skin skin) {
        setFillParent(true);
        //debug();

        Button hintButton = new TextButton(I18nManager.getString("HINT"), skin);
        hintButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                world.showHintWindow();
            }
        });
        add(hintButton).left();

        Button menuButton = new TextButton(I18nManager.getString("MAIN_MENU"), skin);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                world.showMenuWindow();
            }
        });
        add(menuButton).right().top();

        /*if(cloud == null && emitter == null)
            return;*/

        row();

        add().expand();

        row();

        Table controlButtonsTable = new Table();
        //controlButtonsTable.debug();
        add(controlButtonsTable).left();
        Sprite arrowLeftSprite = TextureManager.getSpriteFromAtlas("uiskin.png", "arrow");
        Sprite arrowDownSprite = TextureManager.getSpriteFromAtlas("uiskin.png", "arrow");
        Sprite arrowRightSprite = TextureManager.getSpriteFromAtlas("uiskin.png", "arrow");
        actionButton = new ImageButton(new SpriteDrawable(TextureManager.getSpriteFromAtlas("uiskin.png", "button")));
        arrowUpButton = new ImageButton(new SpriteDrawable(TextureManager.getSpriteFromAtlas("uiskin.png", "arrow")));
        arrowDownButton = new ImageButton(new SpriteDrawable(arrowDownSprite));
        arrowLeftButton = new ImageButton(new SpriteDrawable(arrowLeftSprite));
        arrowRightButton = new ImageButton(new SpriteDrawable(arrowRightSprite));
        actionButton.setVisible(false);
        arrowUpButton.setVisible(false);
        arrowDownButton.setVisible(false);
        arrowLeftButton.setVisible(false);
        arrowRightButton.setVisible(false);
        arrowLeftButton.getImage().setOrigin(arrowLeftSprite.getWidth() / 2, arrowLeftSprite.getHeight() / 2);
        arrowDownButton.getImage().setOrigin(arrowLeftSprite.getWidth() / 2, arrowLeftSprite.getHeight() / 2);
        arrowRightButton.getImage().setOrigin(arrowLeftSprite.getWidth() / 2, arrowLeftSprite.getHeight() / 2);
        arrowLeftButton.getImage().setRotation(90);
        arrowDownButton.getImage().setRotation(180);
        arrowRightButton.getImage().setRotation(-90);
        controlButtonsTable.setFillParent(true);
        controlButtonsTable.bottom();
        controlButtonsTable.defaults().width(100).height(100);
        controlButtonsTable.add(arrowUpButton).colspan(3);
        controlButtonsTable.row();
        controlButtonsTable.add(arrowLeftButton);
        controlButtonsTable.add(arrowDownButton);
        controlButtonsTable.add(arrowRightButton);
        controlButtonsTable.add(actionButton).expandX().right();
        arrowLeftButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                world.handleAction(Input.Keys.LEFT, false);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                world.handleAction(Input.Keys.LEFT, true);
                return true;
            }
        });
        arrowRightButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                world.handleAction(Input.Keys.RIGHT, false);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                world.handleAction(Input.Keys.RIGHT, true);
                return true;
            }
        });
        arrowUpButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                world.handleAction(Input.Keys.UP, false);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                world.handleAction(Input.Keys.UP, true);
                return true;
            }
        });
        arrowDownButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                world.handleAction(Input.Keys.DOWN, false);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                world.handleAction(Input.Keys.DOWN, true);
                return true;
            }
        });
        actionButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                world.handleAction(Input.Keys.SPACE, false);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                world.handleAction(Input.Keys.SPACE, true);
                return true;
            }
        });
    }
}
