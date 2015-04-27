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
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import finnstr.libgdx.liquidfun.ParticleSystem;

public class Trigger extends SimpleActor {
    private PrismaticJoint distanceJoint;
    private Body topBody;
    private boolean state;
    private EventListener listener;
    private boolean isReady = false;

    public Trigger() {
        sprite = TextureManager.getSpriteFromDefaultAtlas("game_button_off");
        setBodyBox(64, 64);
        type = TYPE.TRIGGER;
    }

    @Override
    public void createPhysicsActor(ParticleSystem particleSystem, World physicsWorld) {
        super.createPhysicsActor(particleSystem, physicsWorld);
        pos.sub(getWidth() / 2, getHeight() / 2);

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
        bodyDef.type = bodyType != null ? bodyType : BodyDef.BodyType.DynamicBody;
        body = physicsWorld.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.resetMassData();

        polygonShape.dispose();

        polygonShape = new PolygonShape();
        polygonShape.setAsBox(getPhysicsWidth() / 3, getPhysicsHeight() / 20f);

        offset.set(-getWidth() / 2, -getHeight() / 2);
        sprite.setOrigin(getWidth() / 2, getHeight() / 2);

        fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1;
        fixtureDef.friction = 10.4f;

        bodyDef = new BodyDef();
        bodyDef.position.set(pos.cpy().add(0, getHeight() / 2.5f).scl(GameWorld.WORLD_TO_BOX));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        topBody = physicsWorld.createBody(bodyDef);
        topBody.createFixture(fixtureDef);
        topBody.resetMassData();

        polygonShape.dispose();

        PrismaticJointDef jointDef = new PrismaticJointDef();
        jointDef.initialize(body, topBody,
                pos.cpy().scl(GameWorld.WORLD_TO_BOX), new Vector2(0.0f, 1.0f));
        jointDef.enableLimit = true;
        jointDef.upperTranslation = 0.02f;
        //jointDef.referenceAngle = 1;
        jointDef.collideConnected = true;
        jointDef.enableMotor = true;
        jointDef.motorSpeed = 2f;
        jointDef.maxMotorForce = 2f;
        distanceJoint = (PrismaticJoint)physicsWorld.createJoint(jointDef);

        setRotation(rot);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if(!isReady && distanceJoint.getJointTranslation() > 0.023)
            isReady = true;

        if(isReady && distanceJoint.getJointTranslation() < 0.023)
            setState(true);
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        if(this.state != state && listener != null)
            listener.handle(null);
        this.state = state;
        sprite = TextureManager.getSpriteFromDefaultAtlas(state ? "game_button_on" : "game_button_off");
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }

    @Override
    public void dispose() {
        super.dispose();

        if(physicsWorld == null)
            return;

        if(topBody != null)
            physicsWorld.destroyBody(topBody);

        if(distanceJoint != null)
            physicsWorld.destroyJoint(distanceJoint);
    }
}
