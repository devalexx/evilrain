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
import com.alex.rain.managers.EditorManager;
import com.alex.rain.models.SimpleActor;
import com.alex.rain.ui.EditorUI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class EditableGameWorld extends GameWorld {
    private final EditorUI editorUI;
    private SimpleActor selectedActor;
    private EditorManager editorManager;
    private Vector2 moveActor, nowActorPos, rotateActor, scaleActor, nowActorScale;
    private Float nowActorRot;

    public EditableGameWorld(String name) {
        super(name);

        editorManager = new EditorManager(this, RainGame.shapeRenderer);
        editorUI = new EditorUI(this, editorManager);
        editorManager.setEditorUI(editorUI);
        addActor(editorUI);
    }

    @Override
    public void createWorld() {
        super.createWorld();
        pressingAction = TouchType.NONE;
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
    public void add(SimpleActor actor) {
        super.add(actor);
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
            //RainGame.shapeRenderer.setProjectionMatrix(getCamera().combined);
            RainGame.shapeRenderer.setColor(Color.ORANGE);
            RainGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            if(selectedActor != null) {
                RainGame.shapeRenderer.identity();
                RainGame.shapeRenderer.translate(selectedActor.getX(),
                        selectedActor.getY(), 0);
                RainGame.shapeRenderer.rotate(0, 0, 1, selectedActor.getRotation());
                RainGame.shapeRenderer.rect(-selectedActor.getWidth() / 2,
                        -selectedActor.getHeight() / 2,
                        selectedActor.getWidth(),
                        selectedActor.getHeight());
                RainGame.shapeRenderer.identity();
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
        if(editorUI.isVisible() && editorUI.hit(screenX, Gdx.graphics.getHeight() - screenY, true) != null)
            return true;

        Vector2 cursorPosition = getCursorPosition(screenX, screenY);

        if(button == 0) {
            selectedActor = null;

            for(SimpleActor actor : actorList) {
                if(actor.isInAABB(cursorPosition)) {
                    selectedActor = actor;
                    break;
                }
            }
        }
        editorUI.setSelectedActor(selectedActor);

        if(!editorManager.hasCreatingObject()) {
            if(button == 1) {
                scaleActor = new Vector2(screenX, -screenY);
                nowActorScale = new Vector2(selectedActor.getScaleX(), selectedActor.getScaleX());
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

        if(scaleActor != null) {
            Vector2 scale = nowActorScale.cpy().add(
                    new Vector2(screenX, -screenY).sub(moveActor));
            selectedActor.setScale(scale.x, scale.y);
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

    @Override
    public boolean keyDown(int keyCode) {
        if(keyCode == Input.Keys.E)
            editorUI.setVisible(!editorUI.isVisible());
        else if(keyCode == Input.Keys.W)
            for(SimpleActor sa : actorList)
                if(sa.getBody() != null)
                    sa.getBody().setAwake(true);

        return super.keyDown(keyCode);
    }
}
