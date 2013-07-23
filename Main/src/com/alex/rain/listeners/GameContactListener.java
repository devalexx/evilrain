package com.alex.rain.listeners;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * @author: Alexander Shubenkov
 * @since: 16.07.13
 */

public class GameContactListener implements ContactListener {
    LuaFunction luaOnBeginContactFunc;
    LuaFunction luaOnEndContactFunc;

    public GameContactListener(LuaFunction luaOnBeginContactFunc, LuaFunction luaOnEndContactFunc) {
        this.luaOnBeginContactFunc = luaOnBeginContactFunc;
        this.luaOnEndContactFunc = luaOnEndContactFunc;
    }

    @Override
    public void endContact(Contact contact) {
        if(luaOnEndContactFunc == null)
            return;

        LuaValue luaContact = CoerceJavaToLua.coerce(contact);
        luaOnEndContactFunc.call(luaContact);
    }

    @Override
    public void beginContact(Contact contact) {
        if(luaOnBeginContactFunc == null)
            return;

        LuaValue luaContact = CoerceJavaToLua.coerce(contact);
        luaOnBeginContactFunc.call(luaContact);
    }

    @Override
    public void preSolve (Contact contact, Manifold oldManifold){
    }

    @Override
    public void postSolve (Contact contact, ContactImpulse impulse){
    }
}
