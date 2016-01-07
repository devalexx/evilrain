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
import com.alex.rain.viewports.GameViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class HintWindow extends Window {
    private Label hintLabel;
    private int fullHintCounter = 5;

    public HintWindow(Skin skin, final int levelNumber) {
        super(I18nManager.getString("LEVEL") + " " + levelNumber, skin);
        setSize(GameViewport.WIDTH / 1.5f, GameViewport.HEIGHT / 1.5f);
        setPosition(GameViewport.WIDTH / 2f - getWidth() / 2f,
                GameViewport.HEIGHT / 2f - getHeight() / 2f);
        setModal(true);
        setMovable(false);
        setKeepWithinStage(false);
        setVisible(false);

        hintLabel = new Label(I18nManager.getString(levelNumber != 0 ? "LEVEL" + levelNumber + "_HINT" : "TEST_HINT"), skin);
        hintLabel.setAlignment(Align.center);
        hintLabel.setWrap(true);
        add(hintLabel).colspan(2).padTop(10).width(450).expand().fill().center();

        row();

        final TextButton closeButton = new TextButton(I18nManager.getString("CLOSE"), skin);
        add(closeButton).width(200);

        final TextButton fullHintButton = new TextButton(I18nManager.getString("FULL_HINT"), skin);
        if(levelNumber != 0)
            add(fullHintButton).width(200);

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
            }
        });

        fullHintButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(--fullHintCounter > 0) {
                    fullHintButton.setText(String.valueOf(fullHintCounter));
                    return;
                }

                hintLabel.setText(I18nManager.getString("LEVEL" + levelNumber + "_FULL_HINT"));
            }
        });
    }

    public void setText(String str) {
        hintLabel.setText(str);
    }
}
