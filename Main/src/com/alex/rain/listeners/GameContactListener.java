/*******************************************************************************
 * Copyright 2013 See AUTHORS file.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE V3
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.alex.rain.listeners;

import com.alex.rain.models.SimpleActor;
import com.badlogic.gdx.physics.box2d.*;
import finnstr.libgdx.liquidfun.ParticleBodyContact;
import finnstr.libgdx.liquidfun.ParticleBodyContactListener;
import finnstr.libgdx.liquidfun.ParticleContact;
import finnstr.libgdx.liquidfun.ParticleSystem;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.HashMap;

public class GameContactListener implements ContactListener, ParticleBodyContactListener {
    LuaFunction luaOnBeginContactFunc;
    LuaFunction luaOnEndContactFunc;
    HashMap<Long, SimpleActor> actorsMap;

    public GameContactListener(LuaFunction luaOnBeginContactFunc, LuaFunction luaOnEndContactFunc,
            HashMap<Long, SimpleActor> actorsMap) {
        this.luaOnBeginContactFunc = luaOnBeginContactFunc;
        this.luaOnEndContactFunc = luaOnEndContactFunc;
        this.actorsMap = actorsMap;
    }

    @Override
    public void endContact(Contact contact) {
        if(luaOnEndContactFunc == null)
            return;

        LuaValue luaContact = CoerceJavaToLua.coerce(contact);
        luaOnEndContactFunc.call(luaContact);
    }

    @Override
    public void beginParticleBodyContact(ParticleSystem particleSystem, ParticleBodyContact particleBodyContact) {
        return;
    }

    @Override
    public void endParticleBodyContact(Fixture fixture, ParticleSystem particleSystem, int i) {
        return;
    }

    @Override
    public void beginParticleContact(ParticleSystem particleSystem, ParticleContact particleContact) {
        return;
    }

    @Override
    public void endParticleContact(ParticleSystem particleSystem, int i, int i2) {
        return;
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

    @Override
    public void beginContact(long bodyAddr, int index) {
        if(luaOnBeginContactFunc == null)
            return;

        LuaValue luaContact = CoerceJavaToLua.coerce(actorsMap.get(bodyAddr));
        luaOnBeginContactFunc.call(luaContact);
    }
}
