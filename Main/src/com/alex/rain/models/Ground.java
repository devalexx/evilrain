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

import com.alex.rain.helpers.Box2DSeparatorHelper;
import com.alex.rain.managers.TextureManager;
import com.alex.rain.stages.GameWorld;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;

public class Ground extends SimpleActor {
    List<Vector2> vertices = new ArrayList<Vector2>();

    PolygonSprite poly;
    PolygonSpriteBatch polyBatch;
    TextureRegion textureRegion;

    public Ground() {
        super();
        type = TYPE.GROUND;

        textureRegion = TextureManager.getInstance().getRegionFromDefaultAtlas("grass");
        polyBatch = new PolygonSpriteBatch();
    }

    @Override
    public void prepareActor() {
        float[] verticesFloat = new float[vertices.size() * 2];
        for(int i = 0; i < vertices.size(); i++) {
            verticesFloat[i*2] = vertices.get(i).x;
            verticesFloat[i*2+1] = vertices.get(i).y;
        }

        PolygonRegion polyReg = new PolygonRegion(textureRegion, verticesFloat,
                new EarClippingTriangulator().computeTriangles(verticesFloat).toArray());

        poly = new PolygonSprite(polyReg);
        poly.setOrigin(200, 200);
    }

    @Override
    public void createPhysicsActor(World physicsWorld) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = physicsWorld.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.friction = 10.4f;
        /*fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;*/

        for(Vector2 v : vertices)
            v.scl(GameWorld.WORLD_TO_BOX);

        Box2DSeparatorHelper separatorHelper = new Box2DSeparatorHelper();
        separatorHelper.Separate(body, fixtureDef, vertices, 30);

        polygonShape.dispose();

        for(Vector2 v : vertices)
            v.scl(GameWorld.BOX_TO_WORLD);
    }

    public void addVertex(float x, float y) {
        vertices.add(new Vector2(x, y));
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.end();
        polyBatch.setProjectionMatrix(batch.getProjectionMatrix());
        polyBatch.begin();
            poly.draw(polyBatch);
        polyBatch.end();
        batch.begin();
    }
}
