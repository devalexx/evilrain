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
import com.alex.rain.helpers.SeparatorHelper;
import com.alex.rain.managers.TextureManager;
import com.alex.rain.stages.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import finnstr.libgdx.liquidfun.ParticleSystem;

import java.util.ArrayList;
import java.util.List;

public class Ground extends SimpleActor {
    List<Vector2> vertices = new ArrayList<Vector2>();

    PolygonSprite poly;
    PolygonSpriteBatch polyBatch = RainGame.polyBatch;
    ShapeRenderer shapeRenderer = RainGame.shapeRenderer;
    TextureRegion textureRegion;
    Rectangle aabbRectangle = new Rectangle();

    public Ground() {
        super();
        type = TYPE.GROUND;

        textureRegion = TextureManager.getRegionFromDefaultAtlas("grass");
    }

    public Ground(List<Vector2> vertices) {
        this();
        this.vertices.addAll(vertices);
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
    public void createPhysicsActor(ParticleSystem particleSystem, World physicsWorld) {
        super.createPhysicsActor(particleSystem, physicsWorld);

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

        SeparatorHelper.defaultSeparatorHelper.separate(body, fixtureDef, vertices, 30);

        polygonShape.dispose();

        for(Vector2 v : vertices)
            v.scl(GameWorld.BOX_TO_WORLD);
    }

    private void calculateAABB() {
        float minx = 0, miny = 0, maxx = 0, maxy = 0;
        if(!vertices.isEmpty()) {
            minx = maxx = vertices.get(0).x + getX();
            miny = maxy = vertices.get(0).y + getY();
        }
        for(Vector2 v : vertices) {
            if(v.x + getX() < minx)
                minx = v.x + getX();
            if(v.x + getX() > maxx)
                maxx = v.x + getX();
            if(v.y + getY() < miny)
                miny = v.y + getY();
            if(v.y + getY() > maxy)
                maxy = v.y + getY();
        }
        aabbRectangle.set(minx, miny, maxx - minx, maxy - miny);
    }

    public void addVertex(float x, float y) {
        vertices.add(new Vector2(x, y));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        poly.setPosition(pos.x, pos.y);
        polyBatch.begin();
            poly.draw(polyBatch);
        polyBatch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for(int i = 0; i < vertices.size() ; i++) {
                shapeRenderer.setColor(Color.BLUE);
                Vector2 v1 = vertices.get(i);
                Vector2 v2 = i == vertices.size() - 1 ? vertices.get(0) : vertices.get(i + 1);
                shapeRenderer.line(v1.x + pos.x, v1.y + pos.y, v2.x + pos.x, v2.y + pos.y);
            }
        shapeRenderer.end();
        batch.begin();
    }

    public List<Vector2> getVertices() {
        return vertices;
    }

    @Override
    public boolean isInAABB(Vector2 v) {
        calculateAABB();
        return aabbRectangle.contains(v);
    }
}
