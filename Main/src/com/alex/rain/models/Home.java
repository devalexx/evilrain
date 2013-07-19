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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;

/**
 * @author: Alexander Shubenkov
 * @since: 16.07.13
 */

public class Home extends DynamicActor {
    public Home() {
        texture = TextureManager.getInstance().getTexture("home1.png");
        sprite = new Sprite(texture);
        setBodyBox(32, 100);
    }

    @Override
    public void createPhysicsActor(World physicsWorld) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(getWidth() / 2, getHeight() / 2);
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
        body.resetMassData();

        polygonShape.dispose();
    }
}
