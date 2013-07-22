package com.alex.rain.models;

import com.alex.rain.managers.TextureManager;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

/**
 * @author: Alexander Shubenkov
 * @since: 22.07.13
 */

public class Emitter extends KinematicActor {
    public Emitter() {
        texture = TextureManager.getInstance().getTexture("emitter.png");
        sprite = new Sprite(texture);
        offset.set(-32, -32);
        type = TYPE.EMITTER;
        setBodyBox(64, 64);
    }

    @Override
    public void createPhysicsActor(World physicsWorld) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(getWidth() / 2, getHeight() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1;
        fixtureDef.friction = 10.4f;
        fixtureDef.filter.categoryBits = CATEGORY_CLOUD;
        fixtureDef.filter.maskBits = MASK_NONE;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        body = physicsWorld.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.resetMassData();

        polygonShape.dispose();

        setWidth(64);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        sprite.setPosition(pos.x + offset.x, pos.y + offset.y);
        sprite.setRotation(rot);
        sprite.draw(batch, parentAlpha);
    }
}
