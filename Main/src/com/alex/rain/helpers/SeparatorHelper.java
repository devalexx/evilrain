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
package com.alex.rain.helpers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.List;

public interface SeparatorHelper {
    public static AntoanAngelovSeparatorHelper antoanAngelovSeparatorHelper = new AntoanAngelovSeparatorHelper();
    public static EarClippingSeparatorHelper earClippingSeparatorHelper = new EarClippingSeparatorHelper();
    public static SeparatorHelper defaultSeparatorHelper = antoanAngelovSeparatorHelper;

    public void separate(Body body, FixtureDef fixtureDef, List<Vector2> verticesVec, float scale);

    public List<List<Vector2>> getSeparated(List<Vector2> verticesVec, float scale);

    public int validate(List<Vector2> verticesVec);
}
