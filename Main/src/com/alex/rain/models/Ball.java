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
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import finnstr.libgdx.liquidfun.ParticleSystem;

public class Ball extends SimpleActor {
    public Ball() {
        sprite = TextureManager.getSpriteFromDefaultAtlas("button");
        setBodyBox(20, 20);
        setSpriteBox(40, 40);
        type = TYPE.BALL;
    }

    @Override
    public void createPhysicsActor(ParticleSystem particleSystem, World physicsWorld) {
        super.createPhysicsActor(particleSystem, physicsWorld);

        offset.set(-getWidth(), -getHeight());
        sprite.setOrigin(getWidth(), getHeight());
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(getPhysicsWidth());

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 0.3f;
        fixtureDef.friction = 1f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(pos.cpy().scl(GameWorld.WORLD_TO_BOX));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = physicsWorld.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.resetMassData();

        circleShape.dispose();
    }

    @Override
    public boolean isInAABB(Vector2 v) {
        Vector2 tmp = new Vector2(v);
        tmp.sub(pos);
        return tmp.len() < getWidth();
    }
}
