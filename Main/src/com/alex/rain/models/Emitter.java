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

import com.alex.rain.managers.TextureManager;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import finnstr.libgdx.liquidfun.ParticleSystem;

public class Emitter extends KinematicActor {
    public Emitter() {
        sprite = TextureManager.getSpriteFromDefaultAtlas("emitter");
        offset.set(-32, -32);
        type = TYPE.EMITTER;
        setBodyBox(64, 64);
    }

    @Override
    public void createPhysicsActor(ParticleSystem particleSystem, World physicsWorld) {
        PolygonShape polygonShape = new PolygonShape();
        //polygonShape.setAsBox(getPhysicsWidth() / 2, getPhysicsHeight() / 2);
        offset.set(-getWidth() / 2, -getHeight() / 2);
        sprite.setOrigin(getWidth() / 2, getHeight() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1;
        fixtureDef.friction = 10.4f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        body = physicsWorld.createBody(bodyDef);
        polygonShape.set(new float[] {
                -getPhysicsWidth() / 2.5f, getPhysicsHeight() / 3, // top
                -getPhysicsWidth() / 2.5f, getPhysicsHeight() / 2f,
                getPhysicsWidth() / 2.5f, getPhysicsHeight() / 2f,
                getPhysicsWidth() / 2.5f, getPhysicsHeight() / 3});
        body.createFixture(fixtureDef);
        polygonShape.set(new float[] {
                -getPhysicsWidth() / 2f, -getPhysicsHeight() / 2.5f, // left
                -getPhysicsWidth() / 2f, getPhysicsHeight() / 2.5f,
                -getPhysicsWidth() / 3, getPhysicsHeight() / 2.5f,
                -getPhysicsWidth() / 3, -getPhysicsHeight() / 2.5f});
        body.createFixture(fixtureDef);
        polygonShape.set(new float[] {
                -getPhysicsWidth() / 2.5f, -getPhysicsHeight() / 3, // bottom
                -getPhysicsWidth() / 2.5f, -getPhysicsHeight() / 2f,
                getPhysicsWidth() / 2.5f, -getPhysicsHeight() / 2f,
                getPhysicsWidth() / 2.5f, -getPhysicsHeight() / 3});
        body.createFixture(fixtureDef);

        polygonShape.dispose();

        setWidth(64);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setPosition(pos.x + offset.x, pos.y + offset.y);
        sprite.setRotation(rot);
        sprite.draw(batch, parentAlpha);
    }
}
