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

import com.alex.rain.RainGame;
import com.alex.rain.stages.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import finnstr.libgdx.liquidfun.ParticleDef;
import finnstr.libgdx.liquidfun.ParticleGroup;
import finnstr.libgdx.liquidfun.ParticleGroupDef;
import finnstr.libgdx.liquidfun.ParticleSystem;

public class Drop extends SimpleActor {
    private static float RADIUS;
    private static CircleShape circleShape;
    private static BodyDef bodyDef;
    private Color color = Color.BLUE;
    private int particleIndex;
    public ParticleGroup particleGroup;
    private ParticleSystem particleSystem;

    public Drop() {
        super();
        type = TYPE.DROP;
        RADIUS = RainGame.isLightVersion() ? 0.4f : 0.3f;
    }

    @Override
    public void createPhysicsActor(ParticleSystem particleSystem, World physicsWorld) {
        super.createPhysicsActor(particleSystem, physicsWorld);

        this.particleSystem = particleSystem;

        ParticleDef particleDef = new ParticleDef();
        particleDef.flags.add(ParticleDef.ParticleType.b2_waterParticle);
        particleDef.position.set(pos).scl(GameWorld.WORLD_TO_BOX);
        particleDef.color.set(color);
        particleDef.velocity.set(linVel);

        ParticleGroupDef particleGroupDef = new ParticleGroupDef();
        particleGroupDef.flags.add(ParticleDef.ParticleType.b2_waterParticle);
        //particleGroupDef.flags.add(ParticleDef.ParticleType.b2_fixtureContactListenerParticle);
        particleGroupDef.position.set(pos).scl(GameWorld.WORLD_TO_BOX);
        particleGroupDef.color.set(color);
        PolygonShape parShape = new PolygonShape();
        parShape.setAsBox(GameWorld.WORLD_TO_BOX, GameWorld.WORLD_TO_BOX);
        particleGroupDef.shape = parShape;
        //particleIndex = particleSystem.createParticle(particleDef);
        particleGroup = particleSystem.createParticleGroup(particleGroupDef);
        particleIndex = particleGroup.getBufferIndex();
    }

    private static CircleShape getCircleShape() {
        if(circleShape == null) {
            circleShape = new CircleShape();
            circleShape.setRadius(RADIUS);
        }

        return circleShape;
    }

    private static BodyDef getBodyDef() {
        if(bodyDef == null) {
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        }

        return bodyDef;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void setPosition(Vector2 vec) {
        pos.set(vec);
    }

    @Override
    public Vector2 getPosition() {
        float posArr[] = particleSystem.getParticlePositionBufferArray(false);
        if(particleSystem != null)
            return pos.set(posArr[particleIndex * 2] * GameWorld.BOX_TO_WORLD,
                    posArr[particleIndex * 2 + 1] * GameWorld.BOX_TO_WORLD);

        return pos;
    }

    @Override
    public void act(float delta) {}

    public void decrementIndex() {
        particleIndex--;
    }
}
