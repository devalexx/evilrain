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

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

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
