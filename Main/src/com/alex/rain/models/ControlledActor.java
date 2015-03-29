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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public abstract class ControlledActor extends SimpleActor {
    Rectangle minMaxPosRectangle;
    boolean hasControl = true;
    Color color = Color.BLUE;

    public void setMinMaxPosRectangle(float x, float y, float width, float height) {
        minMaxPosRectangle = new Rectangle(x, y, width, height);
    }

    public boolean isInMinMaxPosRectangle() {
        return minMaxPosRectangle == null || minMaxPosRectangle.contains(pos);
    }

    @Override
    public void preAct(float delta) {
        if(linVel.x == 0 && linVel.y == 0 || isInMinMaxPosRectangle())
            return;

        if(linVel.x < 0 && pos.x < minMaxPosRectangle.x ||
                linVel.x > 0 && pos.x > minMaxPosRectangle.x + minMaxPosRectangle.width)
            linVel.x = 0;
        if(linVel.y < 0 && pos.y < minMaxPosRectangle.y ||
                linVel.y > 0 && pos.y > minMaxPosRectangle.y + minMaxPosRectangle.height)
            linVel.y = 0;

        setLinearVelocity(linVel);
    }

    public boolean hasControl() {
        return hasControl;
    }

    public void setHasControl(boolean hasControl) {
        this.hasControl = hasControl;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
