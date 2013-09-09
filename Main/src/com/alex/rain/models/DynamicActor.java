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
package com.alex.rain.models;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class DynamicActor extends SimpleActor {
    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        sprite.setPosition(pos.x + offset.x, pos.y + offset.y);
        sprite.setRotation(rot);
        sprite.draw(batch, parentAlpha);
    }
}
