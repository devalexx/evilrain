/*
 *   Copyright(c) 2001-2012, Latista Technologies Inc, All Rights Reserved.
 *
 *   The software and information contained herein are copyrighted and
 *   proprietary to Latista Technologies Inc. This software is furnished
 *   pursuant to a written license agreement and may be used, copied,
 *   transmitted, and stored only in accordance with the terms of such
 *   license and with the inclusion of the above copyright notice. Please
 *   refer to the file "LICENSE" for further copyright and licensing
 *   information. This software and information or any other copies
 *   thereof may not be provided or otherwise made available to any other
 *   person.
 *
 *   LATISTA TECHNOLOGIES INC MAKES NO REPRESENTATIONS AND EXTENDS NO
 *   WARRANTIES, EXPRESS OR IMPLIED, WITH RESPECT TO THE SOFTWARE, INCLUDING,
 *   BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *   FOR ANY PARTICULAR PURPOSE, AND THE WARRANTY AGAINST INFRINGEMENT OF
 *   PATENTS OR OTHER INTELLECTUAL PROPERTY RIGHTS. THE SOFTWARE IS PROVIDED
 *   "AS IS", AND IN NO EVENT SHALL LATISTA TECHNOLOGIES INC OR ANY OF ITS
 *   AFFILIATES BE LIABLE FOR ANY DAMAGES, INCLUDING ANY LOST PROFITS OR OTHER
 *   INCIDENTAL OR CONSEQUENTIAL DAMAGES RELATING TO THE SOFTWARE.
 *
 *   Please note that this software and information are protected by copyright
 *   law and international treaties. Unauthorized use, copy and/or modification
 *   of this software and information, may result in severe civil and criminal
 *   penalties, and will be prosecuted to the maximum extent possible under the
 *   law.
 */
package com.alex.rain.models;

import com.alex.rain.managers.TextureManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

/**
 * @author: Alexander Shubenkov
 * @since: 05.07.13
 */

public class Cloud extends KinematicActor {
    public Cloud() {
        texture = TextureManager.getInstance().getTexture("cloud.png");
        sprite = new Sprite(texture);
        offset.set(-100, -50);
        type = SimpleActor.TYPE.CLOUD;
        setBodyBox(200, 100);
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

        setWidth(200);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        sprite.setPosition(pos.x + offset.x, pos.y + offset.y);
        sprite.setRotation(rot);
        sprite.draw(batch, parentAlpha);
    }
}
