package com.alex.rain.models;

import com.alex.rain.managers.TextureManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

/**
 * @author: Alexander Shubenkov
 * @since: 27.06.13
 */

public abstract class DynamicActor extends SimpleActor {
    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        sprite.setPosition(pos.x + offset.x, pos.y + offset.y);
        sprite.setRotation(rot);
        sprite.draw(batch, parentAlpha);
    }
}
