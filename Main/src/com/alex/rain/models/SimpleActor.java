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
    protected TYPE type = TYPE.NONE;
    protected Vector2 pos = new Vector2();
    protected Vector2 offset = new Vector2();
    protected float rot;
    protected Vector2 linVel = new Vector2();

    public enum TYPE {
        NONE,
        DROP,
        GROUND,
        CLOUD
    }

    public final short CATEGORY_ALL = 0x0001;
    public final short CATEGORY_CLOUD = 0x0002;

    public final short MASK_ALL = -1;
    public final short MASK_NONE = 0;

    public abstract void createPhysicsActor(World physicsWorld);

    public void prepareActor() {

    }

    public void setRotation(float a) {
        body.setTransform(getPosition(), (float)Math.toRadians(a));
        rot = a;
    }

    public float getRotation() {
        return rot;
    }

    public void setPosition(Vector2 vec) {
        body.setTransform(vec, body.getAngle());
        pos.set(vec);
    }

    public Vector2 getPosition() {
        return pos;
    }

    public void setLinearVelocity(Vector2 vec) {
        body.setLinearVelocity(vec);
        linVel.set(vec);
    }

    public Vector2 getLinearVelocity() {
        return linVel;
    }

    @Override
    public void act(float delta) {
        pos = body.getPosition();
        rot = (float)Math.toDegrees(body.getAngle());
        linVel = body.getLinearVelocity();
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
