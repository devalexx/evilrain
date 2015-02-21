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
package com.alex.rain.managers;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author: Alexander Shubenkov
 * @since: 30.01.14
 */

public class I18nManager {
    private static ResourceBundle rootResourceBundle;
    private static boolean isInit;
    public static String availableLanguages[] = {"en", "ru"};
    public static Locale locale;

    public static void load(Locale l) {
        try {
            rootResourceBundle = new PropertyResourceBundle(new InputStreamReader(
                    Gdx.files.internal("data/i18n/messages_" + l.getLanguage() + ".properties").read(), "UTF-8"));
            locale = l;
            isInit = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String key) {
        if(!isInit)
            load(Locale.ENGLISH);

        try {
            return rootResourceBundle.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
            return '!' + key + '!';
        }
    }

    public static String getString(String key, Object... params) {
        try {
            return MessageFormat.format(rootResourceBundle.getString(key), params);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static boolean isAvailable(Locale locale) {
        for(String lang : availableLanguages)
            if(lang.equals(locale.getLanguage()))
                return true;

        return false;
    }

    public static int getCurrentLanguageId() {
        for(int i = 0; i < availableLanguages.length; i++) {
            if(availableLanguages[i].equals(locale.getLanguage()))
                return i;
        }

        return -1;
    }

    public static String getCurrentLanguage() {
        return locale.getLanguage();
    }
}
