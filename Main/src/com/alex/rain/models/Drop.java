package com.alex.rain.models;

import com.alex.rain.RainGame;
import com.badlogic.gdx.physics.box2d.*;

/**
 * @author: Alexander Shubenkov
 * @since: 29.05.13
 */

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
