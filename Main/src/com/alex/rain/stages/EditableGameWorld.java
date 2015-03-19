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
package com.alex.rain.stages;

import com.alex.rain.RainGame;
import com.alex.rain.listeners.GameContactListener;
import com.alex.rain.managers.EditorManager;
import com.alex.rain.managers.ResourceManager;
import com.alex.rain.managers.TextureManager;
import com.alex.rain.models.*;
import com.alex.rain.renderer.ParticleRenderer;
import com.alex.rain.screens.LevelsMenuScreen;
import com.alex.rain.screens.MainMenuScreen;
import com.alex.rain.ui.EditorUI;
import com.alex.rain.viewports.GameViewport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import finnstr.libgdx.liquidfun.ParticleDebugRenderer;
import finnstr.libgdx.liquidfun.ParticleSystem;
import finnstr.libgdx.liquidfun.ParticleSystemDef;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.script.LuaScriptEngine;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.List;

public class EditableGameWorld extends GameWorld {
    private final EditorUI editorUI;
    private SimpleActor selectedActor;
    private EditorManager editorManager;
    private Vector2 moveActor, nowActorPos, rotateActor;
    private Float nowActorRot;

    public EditableGameWorld(String name) {
        super(name);

        editorManager = new EditorManager(this, RainGame.shapeRenderer);
        editorUI = new EditorUI(this, editorManager);
        editorManager.setEditorUI(editorUI);
        addActor(editorUI);
    }

    public SimpleActor getSelectedActor() {
        return selectedActor;
    }

    public void setSelectedActor(SimpleActor selectedActor) {
        this.selectedActor = selectedActor;
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
        if(editorUI != null)
            editorUI.toFront();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if(editorUI != null)
            editorUI.setPosition(-gameViewport.offsetX, -gameViewport.offsetY);
    }

    @Override
    public void draw() {
        super.draw();

        if(selectedActor != null || editorManager.hasCreatingObject()) {
            Gdx.gl.glLineWidth(3);
            RainGame.shapeRenderer.setProjectionMatrix(getCamera().combined);
            RainGame.shapeRenderer.setColor(Color.ORANGE);
            RainGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            if(selectedActor != null) {
                RainGame.shapeRenderer.identity();
                RainGame.shapeRenderer.translate(selectedActor.getX() + selectedActor.getWidth() / 2,
                        selectedActor.getY() + selectedActor.getHeight() / 2, 0);
                RainGame.shapeRenderer.rotate(0, 0, 1, selectedActor.getRotation());
                RainGame.shapeRenderer.rect(-selectedActor.getWidth() / 2, -selectedActor.getHeight() / 2,
                        selectedActor.getWidth(), selectedActor.getHeight());
            }

            if(editorManager.hasCreatingObject())
                editorManager.draw();

            RainGame.shapeRenderer.end();
            Gdx.gl.glLineWidth(1);
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean result = super.touchDown(screenX, screenY, pointer, button);

        Vector2 cursorPosition = getCursorPosition(screenX, screenY);

        if(button == 0) {
            for(SimpleActor actor : actorList) {
                if(actor.isInAABB(cursorPosition)) {
                    selectedActor = actor;
                    break;
                }
            }
        }

        if(!editorManager.hasCreatingObject()) {
            if(button == 1) {

            } else if(button == 0 && selectedActor != null) {
                moveActor = new Vector2(screenX, -screenY);
                nowActorPos = new Vector2(selectedActor.getX(), selectedActor.getY());
            } else if(button == 2 && selectedActor != null) {
                nowActorRot = selectedActor.getRotation();
                rotateActor = new Vector2(screenX, screenY);
            }
        }

        return result;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(moveActor != null) {
            Vector2 pos = nowActorPos.cpy().add(
                    new Vector2(screenX, -screenY).sub(moveActor));
            selectedActor.setPosition(pos.x, pos.y);
        }

        if(rotateActor != null) {
            Vector2 pos = rotateActor.cpy().sub(new Vector2(screenX, screenY));
            selectedActor.setRotation(nowActorRot + pos.y);
        }

        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        editorManager.touchUp(screenX, screenY, button);

        if(button == 1) {

        } else if(button == 0 && moveActor != null) {
            moveActor = null;
            nowActorPos = null;
        } else if(button == 2 && rotateActor != null) {
            rotateActor = null;
        }

        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        editorUI.setCursorPositionValue(screenToStageCoordinates(new Vector2(screenX, screenY)));

        return super.mouseMoved(screenX, screenY);
    }
}
