package com.alex.rain.models;

import com.alex.rain.managers.TextureManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * @author: Alexander Shubenkov
 * @since: 16.07.13
 */

public class Hammer extends DynamicActor {
    public Hammer() {
        texture = TextureManager.getInstance().getTexture("hammer.png");
        sprite = new Sprite(texture);
        setBodyBox(128, 128);
    }

    @Override
    public void createPhysicsActor(World physicsWorld) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(getWidth() / 7, getHeight() / 2);
        offset.set(-getWidth() / 2, -getHeight() / 2);
        sprite.setOrigin(getWidth() / 2, getHeight() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1;
        fixtureDef.friction = 10.4f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = physicsWorld.createBody(bodyDef);
        body.createFixture(fixtureDef);

        polygonShape.setAsBox(getWidth() / 2, getHeight() / 7, new Vector2(0, getHeight() / 3), 0);
        offset.set(-getWidth() / 2, -getHeight() / 2);

        body.createFixture(fixtureDef);
        body.resetMassData();

        polygonShape.dispose();
    }
}
