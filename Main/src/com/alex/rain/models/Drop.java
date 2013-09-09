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
import com.badlogic.gdx.physics.box2d.*;

public class Drop extends SimpleActor {
    public final float RADIUS;

    public Drop() {
        super();
        type = TYPE.DROP;
        RADIUS = RainGame.isLightVersion() ? 0.4f : 0.3f;
    }

    @Override
    public void createPhysicsActor(World physicsWorld) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(210, 140);

        body = physicsWorld.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(RADIUS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.filter.categoryBits = CATEGORY_ALL;

        Fixture fixture = body.createFixture(fixtureDef);

        circle.dispose();
    }
}
