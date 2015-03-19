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
import com.alex.rain.models.SimpleActor;
import com.alex.rain.stages.EditableGameWorld;
import com.alex.rain.stages.GameWorld;
import com.alex.rain.ui.EditorUI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.script.LuaScriptEngine;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import java.io.IOException;
import java.io.InputStream;
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

    /**public Wall addWall() {
        Wall wall = new Wall();
        moveToCenter(wall);
        wall.setName("wall_" + counter++);
        selectedActor = wall;
        editorUI.setSelectedActor(selectedActor);
        stage.setSelectedActor(selectedActor);
        stage.addActor(wall);

        return wall;
    }*/

    public void moveToCenter(SimpleActor actor) {
        Vector2 pos = stage.screenToStageCoordinates(
                new Vector2(Gdx.graphics.getWidth() / 2 + actor.getWidth() / 2,
                        Gdx.graphics.getHeight() / 2 - actor.getHeight() / 2));
        actor.setPosition(pos.x, pos.y);
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

    public void addMesh() {
        creatingObject = SimpleActor.TYPE.GROUND;
    }

    public void touchUp(int screenX, int screenY, int button) {
        /*if(creatingObject == SimpleActor.TYPE.MESH) {
            if(button == 0) {
                Vector2 pos = stage.screenToStageCoordinates(new Vector2(screenX, screenY));
                newMeshVertices.add(pos);
            } else if(button == 2) {
                Mesh mesh = new Mesh(newMeshVertices);
                stage.addActor(mesh);
                creatingObject = SimpleActor.TYPE.NONE;
                newMeshVertices.clear();
                mesh.setName("mesh_" + counter++);
                selectedActor = mesh;
                editorUI.setSelectedActor(selectedActor);
                stage.setSelectedActor(selectedActor);
            } else {
                if(newMeshVertices.size() == 0)
                    creatingObject = SimpleActor.TYPE.NONE;
                else
                    newMeshVertices.remove(newMeshVertices.size() - 1);
            }
        }*/
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

    /*public Player addPlayer() {
        Player player = new Player();
        moveToCenter(player);
        player.setName("player_" + counter++);
        selectedActor = player;
        editorUI.setSelectedActor(selectedActor);
        stage.setSelectedActor(selectedActor);
        stage.addActor(player);

        return player;
    }

    public Skate addSkate() {
        Skate skate = new Skate();
        moveToCenter(skate);
        skate.setName("skate_" + counter++);
        selectedActor = skate;
        editorUI.setSelectedActor(selectedActor);
        stage.setSelectedActor(selectedActor);
        stage.addActor(skate);

        return skate;
    }

    public Coin addCoin() {
        Coin coin = new Coin();
        moveToCenter(coin);
        coin.setName("coin_" + counter++);
        selectedActor = coin;
        editorUI.setSelectedActor(selectedActor);
        stage.setSelectedActor(selectedActor);
        stage.addActor(coin);

        return coin;
    }*/

    public void removeSelectedActor() {
        stage.removeActor(selectedActor);
        selectedActor = null;
        editorUI.setSelectedActor(null);
        stage.setSelectedActor(null);
    }
}
