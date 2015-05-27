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
import com.alex.rain.managers.*;
import com.alex.rain.mics.ColorAndCount;
import com.alex.rain.models.*;
import com.alex.rain.renderer.ParticleRenderer;
import com.alex.rain.screens.MainMenuScreen;
import com.alex.rain.ui.GameUI;
import com.alex.rain.ui.HintWindow;
import com.alex.rain.ui.MenuWindow;
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
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import finnstr.libgdx.liquidfun.ParticleDebugRenderer;
import finnstr.libgdx.liquidfun.ParticleSystem;
import finnstr.libgdx.liquidfun.ParticleSystemDef;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.script.LuaScriptEngine;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameWorld extends Stage {
    enum TouchType {
        NONE,
        DRAWING,
        PICKING_DROPS,
        PICKING_BODIES
    }

    public static final float WORLD_TO_BOX = 0.01f;
    public static final float BOX_TO_WORLD = 1 / WORLD_TO_BOX;
    public static float PARTICLE_RADIUS = 6f;
    public static final int MIN_DROP_X_POS = (int)(-GameViewport.WIDTH * 2 * WORLD_TO_BOX);
    public static final int MAX_DROP_X_POS = (int)(GameViewport.WIDTH * 2 * WORLD_TO_BOX);
    public static final int MIN_DROP_Y_POS = (int)(-GameViewport.HEIGHT * WORLD_TO_BOX);

    private World physicsWorld = new World(new Vector2(0, -9.8f), true);
    private ParticleSystem particleSystem;
    private LuaFunction luaOnCreateFunc, luaOnCheckFunc, luaOnBeginContactFunc, luaOnEndContactFunc;
    private Texture backgroundTexture;
    private Sprite zoneSprite;
    private final Box2DDebugRenderer debugRenderer;
    private final ParticleDebugRenderer particleDebugRendererCircle, particleDebugRendererDot;
    private final ParticleRenderer particleRenderer;
    private final SpriteBatch spriteBatchShadered;
    private final PolygonSpriteBatch polygonSpriteBatch;
    private FrameBuffer m_fbo;
    private TextureRegion m_fboRegion;
    private final Batch sb;
    private Skin skin = ResourceManager.getSkin();
    protected GameViewport gameViewport = new GameViewport();

    private List<Drop> selectedDrops, dropsToCreate = new LinkedList<>(), dropsToDelete = new LinkedList<>();
    private int[] dropsToPreDelete = new int[100];
    private List<SimpleActor> actorsToRemove = new LinkedList<>();
    protected List<SimpleActor> actorList = new ArrayList<>();
    private ArrayList<Drop> dropList = new ArrayList<>();
    private final HashMap<Long, SimpleActor> actorsMap = new HashMap<>();
    private Cloud cloud;
    private Emitter emitter;
    private GameUI tableUI;
    private HintWindow hintWindow;
    private MenuWindow menuWindow;
    private ImageButton actionButton, arrowUpButton, arrowDownButton, arrowLeftButton ,arrowRightButton;
    private Label hintLabel;
    private List<Zone> drawingZones = new LinkedList<>();
    private float[] dropsXYPositions, dropsXYRGBA;
    private Body groundBody;
    private MouseJoint mouseJoint;
    private List<SimpleActor.TYPE> interactTypes = new ArrayList<>();
    private EventListener clickListener;

    private boolean wonGame;
    private boolean debugRendererEnabled;
    private float time;
    private float timeLastDrop;
    private boolean itRain;
    private int levelNumber = 0;
    private String winHint;
    private int MAX_DROPS = 2000;
    private boolean physicsEnabled = true;
    private boolean liquidForcesEnabled = true;
    private boolean useShader = SettingsManager.isHighGraphics();
    protected TouchType pressingAction = TouchType.NONE;
    private Vector2 cursorPosition;
    private boolean drawingDrops;
    private boolean keepDropsForever = false;
    private boolean dropsColorMixing;

    public GameWorld(String name) {
        Arrays.fill(dropsToPreDelete, -1);

        setViewport(gameViewport);
        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        ParticleSystemDef particleSystemDef = new ParticleSystemDef();
        particleSystemDef.radius = PARTICLE_RADIUS * GameWorld.WORLD_TO_BOX;

        particleSystem = new ParticleSystem(physicsWorld, particleSystemDef);

        dropsXYPositions = new float[MAX_DROPS * 2];

        String filename = "data/levels/" + name + ".lua";

        if(name.replaceAll("[\\D]", "").length() > 0)
            levelNumber = Integer.parseInt(name.replaceAll("[\\D]", ""));
        String filenameMain = "data/levels/main.lua";
        ScriptEngine engine = new LuaScriptEngine();
        CompiledScript cs;

        try {
            //Reader reader = new FileReader(filename);
            if(!Gdx.files.internal(filename).exists()) {
                filename = "data/levels/test.lua";
                levelNumber = 0;
            }
            Reader reader = new StringReader(
                    Gdx.files.internal(filenameMain).readString() + Gdx.files.internal(filename).readString());
            cs = ((Compilable)engine).compile(reader);
            SimpleBindings sb = new SimpleBindings();
            cs.eval(sb);
            luaOnCheckFunc = (LuaFunction) sb.get("onCheck");
            luaOnCreateFunc = (LuaFunction) sb.get("onCreate");
            luaOnBeginContactFunc = (LuaFunction) sb.get("onBeginContact");
            luaOnEndContactFunc = (LuaFunction) sb.get("onEndContact");
            sb.put("world", CoerceJavaToLua.coerce(this));
        } catch(Exception e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        }

        final String VERTEX = Gdx.files.internal("data/shaders/drop_shader.vert").readString();
        final String FRAGMENT = /*lightVersion ?
                Gdx.files.internal("data/shaders/drop_shader_light.frag").readString() :*/
                Gdx.files.internal("data/shaders/drop_shader.frag").readString();

        ShaderProgram shader = new ShaderProgram(VERTEX, FRAGMENT);
        if(!shader.isCompiled())
            System.out.println(shader.getLog());

        backgroundTexture = TextureManager.getTexture("background.png");
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        zoneSprite = TextureManager.getSpriteFromDefaultAtlas("zone");

        spriteBatchShadered = new SpriteBatch();
        spriteBatchShadered.setShader(shader);

        sb = getBatch();

        polygonSpriteBatch = new PolygonSpriteBatch();

        debugRenderer = new Box2DDebugRenderer();
        particleDebugRendererDot = new ParticleDebugRenderer(Color.RED, 100000);
        particleDebugRendererCircle = new ParticleDebugRenderer(Color.BLUE, 100000);
        particleRenderer = new ParticleRenderer(Color.RED, 100000);

        GameContactListener contactListener = new GameContactListener(luaOnBeginContactFunc, luaOnEndContactFunc, actorsMap);
        physicsWorld.setContactListener(contactListener);
        //physicsWorld.setParticleBodyContactListener(contactListener);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        tableUI = new GameUI(this, skin);

        actionButton = tableUI.actionButton;
        arrowUpButton = tableUI.arrowUpButton;
        arrowDownButton = tableUI.arrowDownButton;
        arrowLeftButton = tableUI.arrowLeftButton;
        arrowRightButton = tableUI.arrowRightButton;
        addActor(tableUI);
        showHintWindow();

        if(levelNumber != 0)
            setWinHint(I18nManager.getString("LEVEL" + levelNumber + "_HINT"));
        else
            setWinHint(I18nManager.getString("TEST_HINT"));
    }

    public void add(SimpleActor actor) {
        actor.createPhysicsActor(particleSystem, physicsWorld);
        actor.prepareActor();
        actorList.add(actor);
        if(actor.getBody() != null)
            actorsMap.put(actor.getBody().getAddress(), actor);

        if(actor.getType() == SimpleActor.TYPE.CLOUD) {
            cloud = (Cloud)actor;
            actionButton.setVisible(true);
            arrowLeftButton.setVisible(true);
            arrowRightButton.setVisible(true);
        } else if(actor.getType() == SimpleActor.TYPE.EMITTER) {
            emitter = (Emitter)actor;
            if(((ControlledActor)actor).hasControl()) {
                actionButton.setVisible(true);
                arrowUpButton.setVisible(true);
                arrowDownButton.setVisible(true);
            }
        }

        if(actor.getType() == SimpleActor.TYPE.DROP) {
            getRoot().addActorAt(0, actor);
            dropList.add((Drop)actor);
        } else {
            addActor(actor);
        }

        if(tableUI != null)
            tableUI.toFront();
        if(hintWindow != null)
            hintWindow.toFront();
        if(menuWindow != null)
            menuWindow.toFront();
    }

    public void createWorld() {
        if(luaOnCreateFunc != null)
            luaOnCreateFunc.call();

        particleSystem.getParticlePositionBufferArray(true);
    }

    @Override
    public void act(float delta) {
        final float step = 1 / 60f;
        time += Gdx.graphics.getDeltaTime();

        deleteUnnecessaryDrops();

        for(int i = 0, dropsToDeleteSize = dropsToDelete.size(); i < dropsToDeleteSize; i++) {
            Drop drop = dropsToDelete.get(i);
            int idx = dropList.indexOf(drop);
            particleSystem.destroyParticle(idx);
            Drop removedDrop = dropList.remove(idx);
            if(selectedDrops != null)
                selectedDrops.remove(removedDrop);

            for(int j = idx; j < dropList.size(); j++)
                dropList.get(j).decrementIndex();
        }
        dropsToDelete.clear();

        for(SimpleActor sa : actorsToRemove)
            removeActor(sa);
        actorsToRemove.clear();

        for(int i = 0, actorListSize = actorList.size(); i < actorListSize; i++) {
            SimpleActor sa = actorList.get(i);
            sa.preAct(delta);
        }

        if(physicsEnabled)
            physicsWorld.step(step, 4, 2, 6);

        for(int i = 0, dropsToCreateSize = dropsToCreate.size(); i < dropsToCreateSize; i++) {
            Drop drop = dropsToCreate.get(i);
            add(drop);
        }
        dropsToCreate.clear();

        float[] src = particleSystem.getParticlePositionBufferArray(true);
        dropsXYRGBA = null;

        super.act(delta);

        for(int i = 0; i < src.length; i++)
            dropsXYPositions[i] = src[i] * BOX_TO_WORLD;

        if(luaOnCheckFunc != null && luaOnCheckFunc.call().toboolean(1) && !wonGame)
            win();

        if(dropList.size() < MAX_DROPS) {
            if((itRain && isControllable() || emitter != null && emitter.isAutoFire()) &&
                    (emitter != null || cloud != null) &&
                    time - timeLastDrop > 0.01) {
                Drop drop = new Drop(dropsColorMixing);
                Random r = new Random();
                ControlledActor actor = cloud != null ? cloud : emitter;
                drop.setColor(actor.getColor());
                float offset = r.nextFloat() * actor.getWidth() * 0.1f;
                drop.setPosition(new Vector2(actor.getPosition().x + offset, actor.getPosition().y));
                dropsToCreate.add(drop);
                timeLastDrop = time;
            }

            if(isControllable() && drawingDrops && time - timeLastDrop > 0.04) {
                boolean inZone = drawingZones.isEmpty();
                for(Zone zone : drawingZones) {
                    if(zone.rectangle.contains(cursorPosition)) {
                        inZone = true;
                        break;
                    }
                }

                if(inZone) {
                    Drop drop = new Drop(dropsColorMixing);
                    Random r = new Random();
                    int offsetX = r.nextInt(30);
                    int offsetY = r.nextInt(30);
                    drop.setPosition(cursorPosition.cpy().add(offsetX - 15, offsetY - 15));
                    dropsToCreate.add(drop);
                    timeLastDrop = time;
                }
            }
        }

        if(!dropsToCreate.isEmpty())
            SoundManager.playWaterSound();

        if(!itRain && !drawingDrops && (emitter == null || !emitter.isAutoFire()))
            SoundManager.stopWaterSound();

        if(pressingAction == TouchType.PICKING_DROPS && cursorPosition != null && selectedDrops != null) {
            for(int i = 0, selectedDropsSize = selectedDrops.size(); i < selectedDropsSize; i++) {
                Drop d = selectedDrops.get(i);
                Vector2 pos = d.getPosition();
                d.particleGroup.applyForce(new Vector2((cursorPosition.x - pos.x),
                        (cursorPosition.y - pos.y)).scl(0.01f));
            }
        }
    }

    private void win() {
        wonGame = true;
        itRain = false;
        drawingDrops = false;
        if(mouseJoint != null) {
            physicsWorld.destroyJoint(mouseJoint);
            physicsWorld.destroyBody(groundBody);
            mouseJoint = null;
            groundBody = null;
        }
        if(emitter != null) {
            emitter.setLinearVelocity(0, 0);
        }
        if(cloud != null) {
            cloud.setLinearVelocity(0, 0);
            cloud.setDirection(0);
        }
        if(SettingsManager.getMaxCompletedLevel() < levelNumber)
            SettingsManager.setMaxCompletedLevel(levelNumber);
        showMenuWindow();
    }

    public void setKeepDropsForever(boolean state) {
        keepDropsForever = state;
    }

    private void deleteUnnecessaryDrops() {
        if(keepDropsForever)
            return;

        float[] pos = particleSystem.getParticlePositionBufferArray(false);
        for(int i = particleSystem.getParticleCount() - 1; i >= 0; i--) {
            boolean delete = false;
            for(int j = 0; j < dropsToPreDelete.length; j++) {
                if(dropsToPreDelete[j] == -1)
                    break;
                if(dropsToPreDelete[j] == i) {
                    delete = true;
                    break;
                }
            }
            if(pos[i*2] < MIN_DROP_X_POS || pos[i*2] > MAX_DROP_X_POS || pos[i*2+1] < MIN_DROP_Y_POS || delete)
                dropsToDelete.add(dropList.get(i));
        }

        Arrays.fill(dropsToPreDelete, -1);
    }

    public void showHintWindow() {
        if(hintWindow != null) {
            hintWindow.setVisible(true);
            hintWindow.toFront();
            return;
        }

        hintWindow = new HintWindow(skin, levelNumber);
        addActor(hintWindow);
        hintLabel = hintWindow.hintLabel;

        hintWindow.toFront();
    }

    public void showMenuWindow() {
        if(menuWindow != null) {
            menuWindow.update(wonGame);
            menuWindow.setVisible(true);
            menuWindow.toFront();
            return;
        }

        menuWindow = new MenuWindow(wonGame, skin, levelNumber);
        addActor(menuWindow);

        menuWindow.toFront();
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(!isControllable())
            return super.touchDown(screenX, screenY, pointer, button);

        cursorPosition = getCursorPosition(screenX, screenY).cpy();

        if(hit(cursorPosition.x, cursorPosition.y, true) != null)
            return super.touchDown(screenX, screenY, pointer, button);

        if(pressingAction == TouchType.NONE) {
            if(clickListener != null)
                clickListener.handle(new InputEvent() {
                    @Override
                    public Actor getTarget() {
                        for(SimpleActor actor : actorList)
                            if(interactTypes.contains(actor.getType()) && actor.isInAABB(cursorPosition))
                                return actor;

                        return null;
                    }
                });

            if(interactTypes.contains(SimpleActor.TYPE.EMITTER)) {
                for(SimpleActor actor : actorList) {
                    if(actor.getType() == SimpleActor.TYPE.EMITTER && actor.isInAABB(cursorPosition)) {
                        emitter = (Emitter)actor;
                        emitter.setAutoFire(true);
                        break;
                    }
                }
            }
        } else if(pressingAction == TouchType.PICKING_DROPS) {
            cursorPosition.scl(WORLD_TO_BOX);
            selectedDrops = new ArrayList<Drop>();
            float[] pos = particleSystem.getParticlePositionBufferArray(false);
            for(int i = 0; i < pos.length; i += 2) {
                if(Math.abs(cursorPosition.x - pos[i]) < 0.5 && Math.abs(cursorPosition.y - pos[i + 1]) < 0.5)
                    selectedDrops.add(dropList.get(i / 2));
            }
            cursorPosition.scl(BOX_TO_WORLD);
        } else if(pressingAction == TouchType.DRAWING) {
            drawingDrops = true;
        } else if(pressingAction == TouchType.PICKING_BODIES) {
            SimpleActor targetActor = null;
            for(SimpleActor actor : actorList) {
                if(interactTypes.contains(actor.getType()) && actor.isInAABB(cursorPosition) &&
                        actor.getBodyType() == BodyDef.BodyType.DynamicBody) {
                    targetActor = actor;
                    break;
                }
            }

            if(targetActor != null) {
                cursorPosition.scl(WORLD_TO_BOX);
                BodyDef groundBodyDef = new BodyDef();
                groundBodyDef.position.set(cursorPosition);
                groundBody = physicsWorld.createBody(groundBodyDef);
                MouseJointDef mouseJointDef = new MouseJointDef();
                mouseJointDef.bodyA = groundBody;
                mouseJointDef.bodyB = targetActor.getBody();
                //mouseJointDef.dampingRatio = 0.2f;
                //mouseJointDef.frequencyHz = 30;
                mouseJointDef.maxForce = 180.0f * targetActor.getBody().getMass();
                mouseJointDef.collideConnected= true;
                mouseJointDef.target.set(cursorPosition);
                mouseJoint = (MouseJoint)physicsWorld.createJoint(mouseJointDef);
                cursorPosition.scl(BOX_TO_WORLD);
            }
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        cursorPosition = null;
        selectedDrops = null;
        drawingDrops = false;
        if(mouseJoint != null) {
            physicsWorld.destroyJoint(mouseJoint);
            physicsWorld.destroyBody(groundBody);
            mouseJoint = null;
            groundBody = null;
        }

        if(emitter != null && emitter.isClickable()) {
            emitter.setAutoFire(false);
            emitter = null;
        }

        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        boolean result = super.touchDragged(screenX, screenY, pointer);

        if(!isControllable())
            return true;

        if(cursorPosition != null) {
            cursorPosition.set(getCursorPosition(screenX, screenY));

            if(mouseJoint != null) {
                cursorPosition.scl(WORLD_TO_BOX);
                mouseJoint.setTarget(cursorPosition);
                cursorPosition.scl(BOX_TO_WORLD);
            }
        }

        if(wonGame || cloud != null || emitter != null || dropList.size() > MAX_DROPS)
            return true;

        return result;
    }

    protected Vector2 getCursorPosition(int screenX, int screenY) {
        Vector3 v = getCamera().unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(v.x, v.y);
    }

    @Override
    public boolean keyDown(int keyCode) {
        if(keyCode == Input.Keys.F4 || keyCode == Input.Keys.D)
            debugRendererEnabled = !debugRendererEnabled;
        else if(keyCode == Input.Keys.F5 || keyCode == Input.Keys.P)
            togglePhysics();
        else if(keyCode == Input.Keys.F6 || keyCode == Input.Keys.L)
            liquidForcesEnabled = !liquidForcesEnabled;
        else if(keyCode == Input.Keys.F7 || keyCode == Input.Keys.S)
            useShader = !useShader;
        else if(keyCode == Input.Keys.ESCAPE || keyCode == Input.Keys.Q || keyCode == Input.Keys.BACK) {
            if(menuWindow != null && menuWindow.isVisible())
                RainGame.getInstance().setMenu(new MainMenuScreen());
            showMenuWindow();
        } else if(keyCode == Input.Keys.LEFT || keyCode == Input.Keys.RIGHT || keyCode == Input.Keys.UP ||
                keyCode == Input.Keys.DOWN || keyCode == Input.Keys.SPACE)
            handleAction(keyCode, true);
        else if(keyCode == Input.Keys.F12)
            win();

        return super.keyDown(keyCode);
    }

    @Override
    public boolean keyUp(int keyCode) {
        if(keyCode == Input.Keys.LEFT || keyCode == Input.Keys.RIGHT || keyCode == Input.Keys.UP ||
                keyCode == Input.Keys.DOWN || keyCode == Input.Keys.SPACE)
            handleAction(keyCode, false);

        return true;
    }

    public void handleAction(int keyCode, boolean pressed) {
        if(!isControllable())
            return;

        if(keyCode == Input.Keys.LEFT) {
            if(cloud != null) {
                if(pressed) {
                    cloud.setLinearVelocity(new Vector2(-150, 0));
                    if(cloud.getDirection() != -1)
                        cloud.setDirection(1);
                } else {
                    if(cloud.getLinearVelocity().x < 0)
                        cloud.setLinearVelocity(new Vector2(0, 0));
                    if(cloud.getDirection() != -1)
                        cloud.setDirection(0);
                }
            }
        } else if(keyCode == Input.Keys.RIGHT) {
            if(cloud != null) {
                if(pressed) {
                    cloud.setLinearVelocity(new Vector2(150, 0));
                    if(cloud.getDirection() != -1)
                        cloud.setDirection(2);
                } else {
                    if(cloud.getLinearVelocity().x > 0)
                        cloud.setLinearVelocity(new Vector2(0, 0));
                    if(cloud.getDirection() != -1)
                        cloud.setDirection(0);
                }
            }
        } else if(keyCode == Input.Keys.UP) {
            if(emitter != null) {
                if(pressed) {
                    emitter.setLinearVelocity(new Vector2(0, 150));
                } else {
                    if(emitter.getLinearVelocity().y > 0)
                        emitter.setLinearVelocity(new Vector2(0, 0));
                }
            }
        } else if(keyCode == Input.Keys.DOWN) {
            if(emitter != null) {
                if(pressed) {
                    emitter.setLinearVelocity(new Vector2(0, -150));
                } else {
                    if(emitter.getLinearVelocity().y < 0)
                        emitter.setLinearVelocity(new Vector2(0, 0));
                }
            }
        } else if(keyCode == Input.Keys.SPACE) {
            if(pressed) {
                itRain = true;
                if(cloud != null) {
                    cloud.setDirection(-1);
                }
            } else {
                itRain = false;
                if(cloud != null) {
                    cloud.setDirection(0);
                }
            }
        }
    }

    public int getDropsNumber() {
        return dropList.size();
    }

    private void drawDrops(boolean useFBO) {
        particleRenderer.render(particleSystem,
                PARTICLE_RADIUS / 2 * BOX_TO_WORLD * (useFBO ? 1 : gameViewport.scale),
                getCamera().combined.cpy().scale(BOX_TO_WORLD, BOX_TO_WORLD, 1), useFBO);
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, true);

        if(m_fbo == null || m_fbo.getWidth() != gameViewport.getWorldWidth() || m_fbo.getHeight() != gameViewport.getWorldHeight()) {
            if(m_fbo != null)
                m_fbo.dispose();

            m_fbo = new FrameBuffer(Pixmap.Format.RGBA4444,
                    (int)gameViewport.getWorldWidth(), (int)gameViewport.getWorldHeight(), false);
            m_fboRegion = new TextureRegion(m_fbo.getColorBufferTexture());
            m_fboRegion.flip(false, true);
        }

        if(tableUI != null) {
            tableUI.setPosition(-gameViewport.offsetX, -gameViewport.offsetY);
        }
    }

    @Override
    public void draw() {
        sb.setProjectionMatrix(getCamera().combined);
        RainGame.polyBatch.setProjectionMatrix(sb.getProjectionMatrix());
        RainGame.shapeRenderer.setProjectionMatrix(sb.getProjectionMatrix());

        sb.begin();
            sb.draw(backgroundTexture, -(int)getViewport().getWorldWidth(), -(int)getViewport().getWorldHeight(), 0, 0, (int)getViewport().getWorldWidth() * 2, (int)getViewport().getWorldHeight() * 2);
        sb.end();

        Gdx.gl.glClearColor(0, 0, 0, 0);
        if(m_fbo != null && useShader) {
            m_fbo.begin();
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                drawDrops(true);
            m_fbo.end();

            spriteBatchShadered.setProjectionMatrix(getCamera().combined);
            spriteBatchShadered.begin();
                spriteBatchShadered.draw(m_fboRegion, -gameViewport.offsetX, -gameViewport.offsetY,
                        gameViewport.getWorldWidth(), gameViewport.getWorldHeight());
            spriteBatchShadered.end();
        } else {
            drawDrops(false);
        }
        Gdx.gl.glClearColor(1, 1, 1, 1);

        sb.begin();
            for(Zone zone : drawingZones) {
                zoneSprite.setPosition(zone.rectangle.x, zone.rectangle.y);
                zoneSprite.setSize(zone.rectangle.width, zone.rectangle.height);
                zoneSprite.draw(sb);
            }
            getRoot().draw(sb, 1);
        sb.end();

        if(debugRendererEnabled) {
            debugRenderer.render(physicsWorld, getCamera().combined.cpy().scale(BOX_TO_WORLD, BOX_TO_WORLD, 1));
            particleDebugRendererCircle.render(particleSystem, PARTICLE_RADIUS / 2.5f * BOX_TO_WORLD * gameViewport.scale, getCamera().combined.cpy().scale(BOX_TO_WORLD, BOX_TO_WORLD, 1));
            particleDebugRendererDot.render(particleSystem, PARTICLE_RADIUS / 10f * BOX_TO_WORLD * gameViewport.scale, getCamera().combined.cpy().scale(BOX_TO_WORLD, BOX_TO_WORLD, 1));

            hintLabel.setText((winHint != null ? I18nManager.getString("HINT") + ": " + winHint + "\n" : "") +
                    "FPS: " + Gdx.graphics.getFramesPerSecond() + "\n" +
                    "Drops: " + getDropsNumber());

            if(selectedDrops != null) {
                for(Drop drop : selectedDrops) {
                    RainGame.shapeRenderer.setColor(1, 0, 0, 1);
                    RainGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                        RainGame.shapeRenderer.line(drop.getPosition().x, drop.getPosition().y, cursorPosition.x, cursorPosition.y);
                    RainGame.shapeRenderer.end();
                }
            }

            RainGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            if(menuWindow != null)
                menuWindow.drawDebug(RainGame.shapeRenderer);
            if(hintWindow != null)
                hintWindow.drawDebug(RainGame.shapeRenderer);
            if(tableUI != null)
                tableUI.drawDebug(RainGame.shapeRenderer);
            RainGame.shapeRenderer.end();
        }
    }

    public void setWinHint(String winHint) {
        this.winHint = winHint;
        hintLabel.setText(winHint);
    }

    public PolygonSpriteBatch getPolygonSpriteBatch() {
        return polygonSpriteBatch;
    }

    public void setPressingAction(int action) {
        pressingAction = TouchType.values()[action];
        touchUp(0, 0, 0, 0);
    }

    @Override
    public void dispose() {
        particleSystem.destroyParticleSystem();
        physicsWorld.dispose();

        super.dispose();
    }

    public void addDrawingZone(float x, float y, float width, float height) {
        addDrawingZone(x, y, width, height, 0, 0, 1);
    }

    public void addDrawingZone(float x, float y, float width, float height, float r, float g, float b) {
        drawingZones.add(new Zone(new Rectangle(x, y, width, height), new Color(r, g, b, 1)));
    }

    public int dropsInRect(float x, float y, float width, float height) {
        float xy[] = dropsXYPositions;
        int count = 0;
        for(int i = 0, max = particleSystem.getParticleCount(); i < max; i++) {
            float dx = xy[i * 2];
            float dy = xy[i * 2 + 1];
            if(dx > x && dx < x + width && dy > y && dy < y + height)
                count++;
        }

        return count;
    }

    public int dropsAboveX(float x) {
        return dropsAboveX(x, 0);
    }

    public int dropsAboveX(float x, int maxCount) {
        float xy[] = dropsXYPositions;
        int count = 0;
        for(int i = 0, max = particleSystem.getParticleCount(); i < max; i++) {
            float dx = xy[i * 2];
            if(dx > x)
                count++;
        }

        return count;
    }

    public int dropsBelowX(float x) {
        return dropsBelowX(x, 0);
    }

    public int dropsBelowX(float x, int maxCount) {
        float xy[] = dropsXYPositions;
        int count = 0;
        for(int i = 0, max = particleSystem.getParticleCount(); i < max; i++) {
            float dx = xy[i * 2];
            if(dx < x)
                count++;
        }

        return count;
    }

    public int dropsAboveY(float y) {
        return dropsAboveY(y, 0);
    }

    public int dropsAboveY(float y, int maxCount) {
        float xy[] = dropsXYPositions;
        int count = 0;
        for(int i = 0, max = particleSystem.getParticleCount(); i < max; i++) {
            float dy = xy[i * 2 + 1];
            if(dy > y)
                count++;
        }

        return count;
    }

    public int dropsBelowY(float y) {
        return dropsBelowY(y, 0);
    }

    public int dropsBelowY(float y, int maxCount) {
        float xy[] = dropsXYPositions;
        int count = 0;
        for(int i = 0, max = particleSystem.getParticleCount(); i < max; i++) {
            float dy = xy[i * 2 + 1];
            if(dy < y)
                count++;
        }

        return count;
    }

    public ColorAndCount summaryDropsColor(float x, float y, float width, float height) {
        if(dropsXYRGBA == null) {
            dropsXYRGBA = particleSystem.getParticlePositionAndColorBufferArray(true);
            for(int i = 0; i < dropsXYRGBA.length / 6; i++) {
                dropsXYRGBA[i * 6] *= BOX_TO_WORLD;
                dropsXYRGBA[i * 6 + 1] *= BOX_TO_WORLD;
            }
        }

        float xyrgba[] = dropsXYRGBA;

        float r = 0, g = 0, b = 0, a = 0;
        int count = 0;
        for(int i = 0, max = particleSystem.getParticleCount(); i < max; i++) {
            float dx = xyrgba[i * 6];
            float dy = xyrgba[i * 6 + 1];
            if(dx > x && dx < x + width && dy > y && dy < y + height) {
                r += xyrgba[i * 6 + 2];
                g += xyrgba[i * 6 + 3];
                b += xyrgba[i * 6 + 4];
                a += xyrgba[i * 6 + 5];
                count++;
            }
        }

        if(r == 0 && g == 0 && b == 0 && a == 0)
            return null;

        return new ColorAndCount(new Color(r / count, g / count, b / count, a / count), count);
    }

    public void setDebug(boolean debug) {
        this.debugRendererEnabled = debug;
    }

    public boolean isDebug() {
        return debugRendererEnabled;
    }

    public void togglePhysics() {
        physicsEnabled = !physicsEnabled;
    }

    public void reset() {
        dropsToDelete.addAll(dropList);
        for(SimpleActor sa : new ArrayList<>(actorList))
            removeActor(sa);
        actorList.clear();
    }

    public HashMap<Long, SimpleActor> getGameActors() {
        return actorsMap;
    }

    public void safeRemoveActor(SimpleActor actor) {
        actorsToRemove.add(actor);
    }

    public void removeActor(SimpleActor actor) {
        getRoot().removeActor(actor);
        actorList.remove(actor);
        if(actor.getBody() != null)
            actorsMap.remove(actor.getBody().getAddress());
        actor.dispose();

        if(emitter == actor)
            emitter = null;

        if(cloud == actor)
            cloud = null;
    }

    public void addInteractType(String type) {
        interactTypes.add(SimpleActor.TYPE.valueOf(type));
    }

    public void setClickListener(EventListener clickListener) {
        this.clickListener = clickListener;
    }

    public int getDropsCount() {
        return particleSystem.getParticleCount();
    }

    public float getTime() {
        return time;
    }

    public void setDropsColorMixing(boolean state) {
        dropsColorMixing = state;
    }

    public void deleteDropsInRect(float x, float y, float width, float height) {
        float xy[] = dropsXYPositions;
        int c = 0;
        for(int i = 0, max = particleSystem.getParticleCount(); i < max; i++) {
            float dx = xy[i * 2];
            float dy = xy[i * 2 + 1];
            if(dx > x && dx < x + width && dy > y && dy < y + height)
                dropsToPreDelete[c++] = i;
        }
    }

    private boolean isControllable() {
        return (menuWindow == null || !menuWindow.isVisible()) && !hintWindow.isVisible();
    }

    public void addDropToCreate(Drop drop) {
        dropsToCreate.add(drop);
    }
}
