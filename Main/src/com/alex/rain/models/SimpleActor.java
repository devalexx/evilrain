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

import com.alex.rain.stages.GameWorld;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class SimpleActor extends Actor {
    protected Body body;
    protected TYPE type = TYPE.NONE;
    protected Vector2 pos = new Vector2();
    protected Vector2 offset = new Vector2();
    protected float rot;
    protected Vector2 linVel = new Vector2();
    protected Sprite sprite;
    protected Texture texture;

    public enum TYPE {
        NONE,
        DROP,
        GROUND,
        CLOUD,
        EMITTER
    }

    public final short CATEGORY_ALL = 0x0001;
    public final short CATEGORY_CLOUD = 0x0002;

    public final short MASK_ALL = -1;
    public final short MASK_NONE = 0;

    public abstract void createPhysicsActor(World physicsWorld);

    public void prepareActor() {

    }

    public void setRotation(float a) {
        body.setTransform(getPosition(), (float)Math.toRadians(a));
        rot = a;
    }

    public float getRotation() {
        return rot;
    }

    public void setPosition(Vector2 vec) {
        body.setTransform(vec.cpy().mul(GameWorld.WORLD_TO_BOX), body.getAngle());
        pos.set(vec);
    }

    public Vector2 getPosition() {
        return pos;
    }

    public void setLinearVelocity(Vector2 vec) {
        body.setLinearVelocity(vec.cpy().mul(GameWorld.WORLD_TO_BOX));
        linVel.set(vec);
    }

    public Vector2 getLinearVelocity() {
        return linVel;
    }

    @Override
    public void act(float delta) {
        pos = body.getPosition();
        rot = (float)Math.toDegrees(body.getAngle());
        linVel = body.getLinearVelocity();
        pos.mul(GameWorld.BOX_TO_WORLD);
        linVel.mul(GameWorld.BOX_TO_WORLD);
    }

    public void applyForceToCenter(Vector2 vec) {
        body.applyForceToCenter(vec);
    }

    public void applyLinearImpulse(Vector2 pos, Vector2 point) {
        body.applyLinearImpulse(pos, point);
    }

    public Body getBody() {
        return body;
    }

    public TYPE getType() {
        return type;
    }

    public String getStringType() {
        return type.toString();
    }

    public void setBodyBox(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    public void setSpriteBox(float width, float height) {
        sprite.setSize(width, height);
    }

    public float getPhysicsWidth() {
        return getWidth() * GameWorld.WORLD_TO_BOX;
    }

    public float getPhysicsHeight() {
        return getHeight() * GameWorld.WORLD_TO_BOX;
    }
}
