package com.alex.rain.models;

import com.alex.rain.helpers.Box2DSeparatorHelper;
import com.alex.rain.managers.TextureManager;
import com.alex.rain.stages.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.*;

/**
 * @author: Alexander Shubenkov
 * @since: 30.05.13
 */

public class Ground extends SimpleActor {
    List<Vector2> vertices = new ArrayList<Vector2>();

    PolygonSprite poly;
    PolygonSpriteBatch polyBatch;
    TextureRegion textureRegion;

    public Ground(World physicsWorld) {
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
        PolygonRegion polyReg = new PolygonRegion(textureRegion, verticesFloat);
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
            v.mul(GameWorld.WORLD_TO_BOX);

        Box2DSeparatorHelper separatorHelper = new Box2DSeparatorHelper();
        separatorHelper.Separate(body, fixtureDef, vertices, 30);

        polygonShape.dispose();

        for(Vector2 v : vertices)
            v.mul(GameWorld.BOX_TO_WORLD);
    }

    public void addVertex(float x, float y) {
        vertices.add(new Vector2(x, y));
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.end();
        polyBatch.begin();
        poly.draw(polyBatch);
        polyBatch.end();
        batch.begin();
    }
}
