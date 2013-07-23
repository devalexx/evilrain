package com.alex.rain.models;

import com.alex.rain.RainGame;
import com.alex.rain.managers.TextureManager;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

/**
 * @author: Alexander Shubenkov
 * @since: 05.07.13
 */

public class Cloud extends KinematicActor {
    Animation animation;
    TextureRegion leftTextureRegion;
    TextureRegion rightTextureRegion;
    TextureRegion stayTextureRegion;

    final int FRAME_ROWS = 2;
    final int FRAME_COLS = 3;

    int direction;

    public Cloud() {
        texture = TextureManager.getInstance().getTexture("cloud.png");

        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / FRAME_COLS, texture.getHeight() / FRAME_ROWS);

        leftTextureRegion = tmp[0][1];
        rightTextureRegion = tmp[0][2];
        stayTextureRegion = tmp[0][0];
        TextureRegion[] animTextureRegion = new TextureRegion[3];
        for(int i = 1; i < FRAME_ROWS; i++) {
            for(int j = 0; j < FRAME_COLS; j++) {
                animTextureRegion[j] = tmp[i][j];
            }
        }
        animation = new Animation(0.25f, animTextureRegion);

        sprite = new Sprite(texture);
        offset.set(-100, -50);
        type = SimpleActor.TYPE.CLOUD;
        setBodyBox(200, 100);
    }

    @Override
    public void createPhysicsActor(World physicsWorld) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(getPhysicsWidth() / 2, getPhysicsHeight() / 2);

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

        setWidth(200);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        if(direction < 0) {
            batch.draw(animation.getKeyFrame(RainGame.getTime(), true), pos.x + offset.x, pos.y + offset.y);
        } else if(direction == 1) {
            batch.draw(leftTextureRegion, pos.x + offset.x, pos.y + offset.y);
        } else if(direction == 2) {
            batch.draw(rightTextureRegion, pos.x + offset.x, pos.y + offset.y);
        } else {
            batch.draw(stayTextureRegion, pos.x + offset.x, pos.y + offset.y);
        }
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
