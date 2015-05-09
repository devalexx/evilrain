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
import com.badlogic.gdx.Preferences;

import java.util.Locale;


public class SettingsManager {
    static String LOCALE_SETTING = "locale";
    static String HIGH_GRAPHICS_SETTING = "hi_graph";
    static String SMOOTH_TEXTURES_SETTING = "smooth_texture_type";
    static String SOUND_SETTING = "sound";
    static String MAX_COMPLETED_LEVEL = "max_completed_lvl";

    public enum SmoothTextureType {
        NONE_TEXTURES,
        UI_TEXTURES,
        ALL_TEXTURES;

        @Override
        public String toString() {
            return I18nManager.getString(super.toString());
        }
    }

    public static String availableLanguages[] = {"en", "ru"};

    static Preferences prefs = Gdx.app.getPreferences("EvilRain");

    public static void setLanguage(Locale locale) {
        prefs.putString(LOCALE_SETTING, locale.getLanguage());

        if(!I18nManager.locale.equals(locale))
            I18nManager.load(locale);
    }

    public static Locale getLanguage() {
        Locale locale = I18nManager.isAvailable(Locale.getDefault()) ? Locale.getDefault() : Locale.ENGLISH;
        return new Locale(prefs.getString(LOCALE_SETTING, locale.getLanguage()));
    }

    public static void save() {
        prefs.flush();
    }

    public static void setHighGraphics(boolean state) {
        prefs.putBoolean(HIGH_GRAPHICS_SETTING, state);
    }

    public static boolean isHighGraphics() {
        return prefs.getBoolean(HIGH_GRAPHICS_SETTING, true);
    }

    public static void setSmoothTextureType(SmoothTextureType state) {
        prefs.putString(SMOOTH_TEXTURES_SETTING, state.name());

        TextureManager.setLinearFilter(state);
    }

    public static SmoothTextureType getSmoothTextureType() {
        return SmoothTextureType.valueOf(prefs.getString(SMOOTH_TEXTURES_SETTING, SmoothTextureType.UI_TEXTURES.name()));
    }

    public static void setSound(boolean state) {
        prefs.putBoolean(SOUND_SETTING, state);

        SoundManager.setSound(state);
    }

    public static boolean hasSound() {
        return prefs.getBoolean(SOUND_SETTING, true);
    }

    public static void setMaxCompletedLevel(int level) {
        prefs.putInteger(MAX_COMPLETED_LEVEL, level);
        save();
    }

    public static int getMaxCompletedLevel() {
        return prefs.getInteger(MAX_COMPLETED_LEVEL, 0);
    }
}
