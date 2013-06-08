package com.alex.rain.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * @author: Alexander Shubenkov
 * @since: 30.05.13
 */

public abstract class SimpleActor extends Actor {
    protected Body body;
    protected Texture texture;
    protected TYPE type = TYPE.NONE;
    protected Vector2 pos = new Vector2();

    public enum TYPE {
        NONE,
        DROP,
        GROUND
    }

    public abstract void createPhysicsActor(World physicsWorld);

    public void setPosition(Vector2 vec) {
        body.setTransform(vec, body.getAngle());
        pos.set(vec);
    }

    public static int counter = 0;
    public Vector2 getPosition() {
        counter++;
        return pos;
    }

    public void setLinearVelocity(Vector2 vec) {
        body.setLinearVelocity(vec);
    }

    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public void act(float delta) {
        pos = body.getPosition();
    }

    public void applyForceToCenter(Vector2 vec) {
        body.applyForceToCenter(vec);
    }

    public Body getBody() {
        return body;
    }

    public TYPE getType() {
        return type;
    }

    public String getStringType() {
        return type.toString();
    }
}
