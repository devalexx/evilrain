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
package com.alex.rain.managers;

import com.alex.rain.helpers.SeparatorHelper;
import com.alex.rain.listeners.GameContactListener;
import com.alex.rain.models.Ground;
import com.alex.rain.models.SimpleActor;
import com.alex.rain.stages.EditableGameWorld;
import com.alex.rain.ui.EditorUI;
import com.alex.rain.viewports.GameViewport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.script.LuaScriptEngine;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class EditorManager {
    private EditableGameWorld stage;
    private int counter;
    private SimpleActor selectedActor;
    private EditorUI editorUI;
    private SimpleActor.TYPE creatingObject = SimpleActor.TYPE.NONE;
    private ShapeRenderer shapeRenderer;
    private List<Vector2> newMeshVertices = new ArrayList<Vector2>();
    public String onBeginContactStr, onEndContactStr, onCheckStr, onCreateStr;

    public EditorManager(EditableGameWorld stage, ShapeRenderer shapeRenderer) {
        this.stage = stage;
        this.shapeRenderer = shapeRenderer;
    }

    public void setEditorUI(EditorUI editorUI) {
        this.editorUI = editorUI;
    }

    public void moveToCenter(SimpleActor actor) {
        actor.setPosition(GameViewport.WIDTH / 2, GameViewport.HEIGHT / 2);
    }

    public void setSelectedActor(SimpleActor selectedActor) {
        this.selectedActor = selectedActor;
        editorUI.setSelectedActor(selectedActor);
    }

    public void save(String text) {
        FileHandle file = Gdx.files.local("data/levels/editor/" + text);
        file.writeString(new ExportManager(stage, onCreateStr, onCheckStr, onBeginContactStr, onEndContactStr).export(), false);
    }

    public boolean load(String text) {
        FileHandle file = Gdx.files.local("data/levels/editor/" + text);
        if(!file.exists())
            return false;

        String filenameMain = "data/levels/main.lua";
        ScriptEngine engine = new LuaScriptEngine();
        CompiledScript cs;
        LuaFunction luaOnCreateFunc = null;
        try {
            //Reader reader = new FileReader(filename);
            Reader reader = new StringReader(
                    Gdx.files.internal(filenameMain).readString() + file.readString());
            cs = ((Compilable)engine).compile(reader);
            SimpleBindings sb = new SimpleBindings();
            cs.eval(sb);
            //LuaFunction luaOnCheckFunc = (LuaFunction) sb.get("onCheck");
            luaOnCreateFunc = (LuaFunction) sb.get("onCreate");
            LuaFunction luaOnBeginContactFunc = (LuaFunction) sb.get("onBeginContact");
            LuaFunction luaOnEndContactFunc = (LuaFunction) sb.get("onEndContact");

            GameContactListener contactListener = new GameContactListener(luaOnBeginContactFunc, luaOnEndContactFunc, stage.getGameActors());
            stage.getPhysicsWorld().setContactListener(contactListener);
        } catch (Exception e) {
            System.out.println("error: " + text + ". " + e);
        }

        String accumStr = file.readString();
        accumStr = accumStr.replace("\r", "");
        onBeginContactStr = accumStr.substring(accumStr.indexOf("function onBeginContact(contact)\n") + 33,
                accumStr.indexOf("end\nfunction", accumStr.indexOf("function onBeginContact(contact)\n")));
        onEndContactStr = accumStr.substring(accumStr.indexOf("function onEndContact(contact)\n") + 31,
                accumStr.indexOf("end\nfunction", accumStr.indexOf("function onEndContact(contact)\n")));
        onCheckStr = accumStr.substring(accumStr.indexOf("function onCheck()\n") + 19,
                accumStr.length() - 3);
        onCreateStr = accumStr.substring(accumStr.indexOf("function onCreate()\n") + 20,
                accumStr.indexOf("end\nfunction", accumStr.indexOf("function onCreate()\n")));

        stage.reset();
        if(luaOnCreateFunc != null)
            luaOnCreateFunc.call();

        return true;
    }

    public void addGround() {
        creatingObject = SimpleActor.TYPE.GROUND;
    }

    public void touchUp(int screenX, int screenY, int button) {
        if(creatingObject == SimpleActor.TYPE.GROUND) {
            if(button == 0) {
                Vector2 pos = stage.screenToStageCoordinates(new Vector2(screenX, screenY));
                newMeshVertices.add(pos);
            } else if(button == 2) {
                Ground ground = new Ground(newMeshVertices);
                stage.add(ground);
                creatingObject = SimpleActor.TYPE.NONE;
                newMeshVertices.clear();
                ground.setName(ground.getClass().getSimpleName() + "_" + counter++);
                selectedActor = ground;
                editorUI.setSelectedActor(selectedActor);
                stage.setSelectedActor(selectedActor);
            } else {
                if(newMeshVertices.size() == 0)
                    creatingObject = SimpleActor.TYPE.NONE;
                else
                    newMeshVertices.remove(newMeshVertices.size() - 1);
            }
        }
    }

    public boolean hasCreatingObject() {
        return creatingObject != SimpleActor.TYPE.NONE;
    }

    public void draw() {
        shapeRenderer.identity();

        if(SeparatorHelper.defaultSeparatorHelper.validate(newMeshVertices) == 0) {
            shapeRenderer.setColor(Color.GREEN);
            List<List<Vector2>> listOfList = SeparatorHelper.defaultSeparatorHelper.getSeparated(newMeshVertices, 30);

            for(List<Vector2> polygonVertices : listOfList) {
                for (int i = 0; i < polygonVertices.size(); i++) {
                    Vector2 v1 = polygonVertices.get(i);
                    Vector2 v2 = i >= polygonVertices.size() - 1 ? polygonVertices.get(0) : polygonVertices.get(i + 1);
                    shapeRenderer.line(v1.x, v1.y, v2.x, v2.y);
                }
            }
        } else {
            shapeRenderer.setColor(Color.RED);
            for (int i = 0; i < newMeshVertices.size(); i++) {
                Vector2 v1 = newMeshVertices.get(i);
                Vector2 v2 = i >= newMeshVertices.size() - 1 ? newMeshVertices.get(0) : newMeshVertices.get(i + 1);
                shapeRenderer.line(v1.x, v1.y, v2.x, v2.y);
            }
        }
    }

    public SimpleActor add(Class c) {
        SimpleActor sa = null;
        try {
            sa = (SimpleActor)c.newInstance();
            moveToCenter(sa);
            sa.setName(c.getSimpleName().toLowerCase() + "_" + counter++);
            selectedActor = sa;
            editorUI.setSelectedActor(selectedActor);
            stage.setSelectedActor(selectedActor);
            stage.add(sa);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return sa;
    }
    public void removeSelectedActor() {
        stage.removeActor(selectedActor);
        selectedActor = null;
        editorUI.setSelectedActor(null);
        stage.setSelectedActor(null);
    }
}
