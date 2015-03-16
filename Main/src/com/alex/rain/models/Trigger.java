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
import com.alex.rain.stages.GameWorld;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import finnstr.libgdx.liquidfun.ParticleSystem;

public class Trigger extends SimpleActor {
    private DistanceJoint distanceJoint;

    public Trigger() {
        sprite = TextureManager.getSpriteFromDefaultAtlas("game_button_off");
        setBodyBox(64, 64);
        type = TYPE.TRIGGER;
    }

    @Override
    public void createPhysicsActor(ParticleSystem particleSystem, World physicsWorld) {
        super.createPhysicsActor(particleSystem, physicsWorld);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(getPhysicsWidth() / 2, getPhysicsHeight() / 2.4f,
                new Vector2(0, getPhysicsHeight() / -15f), 0);

        offset.set(-getWidth() / 2, -getHeight() / 2);
        sprite.setOrigin(getWidth() / 2, getHeight() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1;
        fixtureDef.friction = 10.4f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(pos.cpy().scl(GameWorld.WORLD_TO_BOX));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = physicsWorld.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.resetMassData();

        polygonShape.dispose();

        polygonShape = new PolygonShape();
        polygonShape.setAsBox(getPhysicsWidth() / 3, getPhysicsHeight() / 20f,
                new Vector2(0, getPhysicsHeight() / 1.33f), 0);

        offset.set(-getWidth() / 2, -getHeight() / 2);
        sprite.setOrigin(getWidth() / 2, getHeight() / 2);

        fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1;
        fixtureDef.friction = 10.4f;

        bodyDef = new BodyDef();
        bodyDef.position.set(pos.cpy().scl(GameWorld.WORLD_TO_BOX));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body topBody = physicsWorld.createBody(bodyDef);
        topBody.createFixture(fixtureDef);
        topBody.resetMassData();

        polygonShape.dispose();

        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.initialize(body, topBody,
                pos.cpy().scl(GameWorld.WORLD_TO_BOX), pos.cpy().add(0, getPhysicsHeight() / 2).scl(GameWorld.WORLD_TO_BOX));
        distanceJointDef.collideConnected = true;
        distanceJoint = (DistanceJoint)physicsWorld.createJoint(distanceJointDef);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        /*System.out.println(getBody().getJointList().get(0).joint.);
        Joint*/
    }
}
