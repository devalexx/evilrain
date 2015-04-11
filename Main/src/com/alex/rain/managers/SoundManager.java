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
package com.alex.rain.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class SoundManager {
    private static final HashMap<String, Sound> soundMap = new HashMap<>();
    private static final HashMap<String, Music> musicMap = new HashMap<>();

    public static final String MENU_MUSIC = "DarkMystery.mp3";
    public static final String GAME_MUSIC = "TheDeadlyYear.mp3";

    public static final String WATER_SOUND = "water.mp3";

    private static boolean sound;
    static SoundManager instance = new SoundManager();

    private static boolean waterIsPlaying;

    private SoundManager() {
        sound = SettingsManager.hasSound();
        loadSounds();
    }

    public static SoundManager getInstance() {
        return instance;
    }

    public static void playMusic(String name) {
        if(!sound)
            return;

        Music music;

        if(musicMap.get(name) == null) {
            music = Gdx.audio.newMusic(Gdx.files.internal("data/music/" + name));
            musicMap.put(name, music);
            music.setLooping(true);
        } else
            music = musicMap.get(name);

        if(!music.isPlaying()) {
            stopMusic();
            music.play();
        }
    }

    public static void stopMusic() {
        for(Music music : musicMap.values())
            music.stop();
    }

    private static void loadSounds() {
        String sounds[] = {WATER_SOUND};
        for(String name : sounds) {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal("data/sound/" + name));
            soundMap.put(name, sound);
        }
    }

    public static void stopSound(String name) {
        if(!sound)
            return;

        Sound sound = soundMap.get(name);

        sound.stop();
    }

    public static void playSound(String name) {
        if(!sound)
            return;

        Sound sound = soundMap.get(name);

        if(name.equals(WATER_SOUND))
            sound.loop();
        else
            sound.play();
    }

    public static void setSound(boolean sound) {
        if(SoundManager.sound == sound)
            return;

        if(!sound)
            stopMusic();
        else
            playMusic(MENU_MUSIC);

        SoundManager.sound = sound;
    }

    public static void stopWaterSound() {
        if(!waterIsPlaying)
            return;

        stopSound(WATER_SOUND);

        waterIsPlaying = false;
    }

    public static void playWaterSound() {
        if(waterIsPlaying)
            return;

        playSound(WATER_SOUND);

        waterIsPlaying = true;
    }
}
