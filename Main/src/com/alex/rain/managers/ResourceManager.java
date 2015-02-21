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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;

public class ResourceManager {
    static ResourceManager instance = new ResourceManager();

    static Skin skin;

    private ResourceManager() {
        skin = new Skin(Gdx.files.internal("data/skins/uiskin.json"));
        TextureManager.addExistingAtlas("uiskin.png", skin.getAtlas());
        for(BitmapFont f : ResourceManager.getAllFonts().values())
            f.setScale(1f);
    }

    public static ResourceManager getInstance() {
        return instance;
    }

    public static Skin getSkin() {
        return skin;
    }

    public static BitmapFont getFont() {
        return skin.get("default-font", BitmapFont.class);
    }

    public static ObjectMap<String, BitmapFont> getAllFonts() {
        return skin.getAll(BitmapFont.class);
    }
}
