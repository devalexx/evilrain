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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.Locale;

public class OptionsScreen extends BasicUIScreen {
    public OptionsScreen() {
        super();

        final Table table = new Table();
        final Table innerTable = new Table();
        mainUI = table;
        table.setFillParent(true);
        stage.addActor(table);

        final TextButton buttonBackTop = new TextButton(I18nManager.getString("BACK"), skin);
        table.add(buttonBackTop).left().width(200);
        buttonBackTop.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RainGame.getInstance().setScreen(new MainMenuScreen());
            }
        });

        Label gameLabel = new Label(I18nManager.getString("OPTIONS"), skin);
        table.add(gameLabel).left();

        table.row();

        table.add(innerTable).expand();

        Label languageLabel = new Label(I18nManager.getString("LANGUAGE"), skin);
        innerTable.add(languageLabel).left();

        final SelectBox<String> languageSelect = new SelectBox<>(skin);
        languageSelect.setItems(getLanguages());
        languageSelect.setSelectedIndex(I18nManager.getCurrentLanguageId());
        innerTable.add(languageSelect).left();

        innerTable.row();

        final CheckBox graphicsCheckBox = new CheckBox(I18nManager.getString("HIGH_GRAPHICS"), skin);
        graphicsCheckBox.setChecked(SettingsManager.getHighGraphics());
        innerTable.add(graphicsCheckBox).left();

        innerTable.row();

        final TextButton saveButton = new TextButton(I18nManager.getString("SAVE"), skin);
        innerTable.add(saveButton).left().width(200);

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SettingsManager.setLanguage(new Locale(I18nManager.availableLanguages[languageSelect.getSelectedIndex()]));
                //SettingsManager.setSound(soundCheckBox.isChecked());
                SettingsManager.setHighGraphics(graphicsCheckBox.isChecked());
                RainGame.getInstance().setScreen(new MainMenuScreen());
            }
        });
    }

    public String[] getLanguages() {
        String[] languages = new String[I18nManager.availableLanguages.length];
        for(int i = 0; i < I18nManager.availableLanguages.length; i++) {
            languages[i] = I18nManager.getString("LANGUAGE_" + I18nManager.availableLanguages[i].toUpperCase());
        }

        return languages;
    }
}
