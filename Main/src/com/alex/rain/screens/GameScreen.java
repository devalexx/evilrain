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

import com.alex.rain.managers.SoundManager;
import com.alex.rain.stages.GameWorld;

public class GameScreen extends BasicUIScreen {
    private GameWorld world;

    public GameScreen(GameWorld world) {
        super(world);
        SoundManager.playMusic(SoundManager.GAME_MUSIC);
        this.world = world;
    }

    @Override
    public void render(float delta) {
    }

    @Override
    public void resize(int width, int height) {
        world.resize(width, height);
    }
}
