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
package com.alex.rain;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.*;

public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Drop";
        //cfg.useGL20 = true;
        cfg.width = 800;
        cfg.height = 480;
        //new LwjglApplication(new LiquidfunTest()/*RainGame.getInstance()*/, cfg);
        new LwjglApplication(RainGame.getInstance(), cfg);

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }
}
