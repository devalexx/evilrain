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
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import finnstr.libgdx.liquidfun.ParticleSystem;

public class House extends SimpleActor {
    private Polygon p = new Polygon();

    public House() {
        this(1);
    }

    public House(int n) {
        sprite = TextureManager.getSpriteFromDefaultAtlas("home" + n);
        setBodyBox(32, 100);
        type = TYPE.HOUSE;
    }

    @Override
    public void createPhysicsActor(ParticleSystem particleSystem, World physicsWorld) {
        super.createPhysicsActor(particleSystem, physicsWorld);
        pos.sub(getWidth() / 2, getHeight() / 2);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(getPhysicsWidth() / 2, getPhysicsHeight() / 2);
        offset.set(-getWidth() / 2, -getHeight() / 2);
        sprite.setOrigin(getWidth() / 2, getHeight() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 2;
        fixtureDef.friction = 1f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(pos.cpy().scl(GameWorld.WORLD_TO_BOX));
        bodyDef.type = bodyType != null ? bodyType : BodyDef.BodyType.DynamicBody;
        body = physicsWorld.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.resetMassData();

        polygonShape.dispose();
    }

    @Override
    public boolean isInAABB(Vector2 v) {
        calculateAABB();
        return p.contains(v.x, v.y);
    }

    private void calculateAABB() {
        float[] f = {
                -getWidth() / 2, -getHeight() / 2,
                getWidth() / 2, -getHeight() / 2,
                getWidth() / 2, getHeight() / 2,
                -getWidth() / 2, getHeight() / 2};
        p.setRotation(0);
        p.setVertices(f);
        p.rotate(getRotation());
        p.setPosition(getX(), getY());
    }
}
