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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import finnstr.libgdx.liquidfun.ParticleSystem;

public abstract class SimpleActor extends Actor {
    protected Body body;
    protected TYPE type = TYPE.NONE;
    protected Vector2 pos = new Vector2();
    protected Vector2 offset = new Vector2();
    protected float rot;
    protected Vector2 linVel = new Vector2();
    protected Sprite sprite;
    protected Texture texture;
    protected World physicsWorld;
    protected BodyDef.BodyType bodyType;

    public enum TYPE {
        NONE,
        DROP,
        GROUND,
        CLOUD,
        EMITTER,
        HOUSE,
        HAMMER,
        BALL,
        TRIGGER
    }

    public void createPhysicsActor(ParticleSystem particleSystem, World physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    public void prepareActor() {

    }

    @Override
    public void setRotation(float a) {
        if(body != null)
            body.setTransform(getPosition().cpy().scl(GameWorld.WORLD_TO_BOX), (float)Math.toRadians(a));
        rot = a;
    }

    @Override
    public float getRotation() {
        return rot;
    }

    @Override
    public void setPosition(float x, float y) {
        setPosition(new Vector2(x, y));
    }

    public void setPosition(Vector2 vec) {
        if(body != null)
            body.setTransform(vec.cpy().scl(GameWorld.WORLD_TO_BOX), body.getAngle());
        pos.set(vec);
    }

    public Vector2 getPosition() {
        return pos;
    }

    @Override
    public float getX() {
        return pos.x;
    }

    @Override
    public float getY() {
        return pos.y;
    }

    public void setLinearVelocity(float x, float y) {
        if(body != null)
            body.setLinearVelocity(x * GameWorld.WORLD_TO_BOX, y * GameWorld.WORLD_TO_BOX);
        linVel.set(x, y);
    }

    public void setLinearVelocity(Vector2 vec) {
        if(body != null)
            body.setLinearVelocity(vec.cpy().scl(GameWorld.WORLD_TO_BOX));
        linVel.set(vec);
    }

    public Vector2 getLinearVelocity() {
        linVel = body.getLinearVelocity();
        linVel.scl(GameWorld.BOX_TO_WORLD);

        return linVel;
    }

    @Override
    public void act(float delta) {
        pos = body.getPosition();
        rot = (float)Math.toDegrees(body.getAngle());
        pos.scl(GameWorld.BOX_TO_WORLD);
    }

    public void applyForceToCenter(Vector2 vec) {
        body.applyForceToCenter(vec, true);
    }

    public void applyForceToCenter(float x, float y, boolean wake) {
        body.applyForceToCenter(x, y, wake);
    }

    public void applyLinearImpulse(Vector2 pos, Vector2 point) {
        body.applyLinearImpulse(pos, point, true);
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

    public void preAct(float delta) {}

    public void setUseParticleBodyContactListener(boolean state) {
        if(body != null)
            body.setUseParticleBodyContactListener(state);
    }

    public boolean isInAABB(Vector2 v) {
        Rectangle r = new Rectangle(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
        return r.contains(v);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(sprite != null) {
            sprite.setPosition(pos.x + offset.x, pos.y + offset.y);
            sprite.setRotation(rot);
            sprite.draw(batch, parentAlpha);
        }
    }

    public void setBodyType(int bodyType) {
        setBodyType(BodyDef.BodyType.values()[bodyType]);
    }

    public void setBodyType(BodyDef.BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public BodyDef.BodyType getBodyType() {
        return bodyType;
    }
}
