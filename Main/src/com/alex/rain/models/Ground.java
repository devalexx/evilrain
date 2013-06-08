package com.alex.rain.models;

import com.alex.rain.helpers.Box2DSeparatorHelper;
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

    public Ground(World physicsWorld) {
        super();
        type = TYPE.GROUND;
    }

    @Override
    public void createPhysicsActor(World physicsWorld) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        //bodyDef.position.set(200, 100);

        body = physicsWorld.createBody(bodyDef);

        /*PolygonShape polygonShape1 = new PolygonShape();
        PolygonShape polygonShape2 = new PolygonShape();
        PolygonShape polygonShape3 = new PolygonShape();*/
        PolygonShape polygonShape = new PolygonShape();

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        /*fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;*/

        /*List<Vector2> vertices = new ArrayList<Vector2>();
        vertices.add(new Vector2(0, 0));
        vertices.add(new Vector2(100, 0));
        vertices.add(new Vector2(100, 20));
        vertices.add(new Vector2(90, 10));
        vertices.add(new Vector2(10, 10));
        vertices.add(new Vector2(0, 20));*/

        Box2DSeparatorHelper separatorHelper = new Box2DSeparatorHelper();
        System.out.println(separatorHelper.Validate(vertices));
        separatorHelper.Separate(body, fixtureDef, vertices, 30);

        //polygonShape.set(vertices.toArray(new Vector2[vertices.size()]));

        polygonShape.dispose();
    }

    public void addVertex(float x, float y) {
        vertices.add(new Vector2(x, y));
    }
}
