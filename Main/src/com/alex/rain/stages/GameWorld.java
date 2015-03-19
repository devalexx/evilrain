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
import com.alex.rain.managers.ResourceManager;
import com.alex.rain.managers.TextureManager;
import com.alex.rain.models.*;
import com.alex.rain.renderer.ParticleRenderer;
import com.alex.rain.screens.LevelsMenuScreen;
import com.alex.rain.screens.MainMenuScreen;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
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
    public static int MIN_DROP_X_POS = (int)(-GameViewport.WIDTH * 2 * WORLD_TO_BOX);
    public static int MAX_DROP_X_POS = (int)(GameViewport.WIDTH * 2 * WORLD_TO_BOX);
    public static int MIN_DROP_Y_POS = (int)(-GameViewport.HEIGHT * WORLD_TO_BOX);

    private World physicsWorld = new World(new Vector2(0, -9.8f), true);
    private ParticleSystem particleSystem;
    private LuaFunction luaOnCreateFunc, luaOnCheckFunc, luaOnBeginContactFunc, luaOnEndContactFunc;
    private Texture backgroundTexture;
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

    private List<Drop> selectedDrops, dropsToCreate = new LinkedList<Drop>(), dropsToDelete = new LinkedList<Drop>();
    protected List<SimpleActor> actorList = new ArrayList<SimpleActor>();
    private ArrayList<Drop> dropList = new ArrayList<Drop>();
    private final HashMap<Long, SimpleActor> actorsMap = new HashMap<>();
    private Cloud cloud;
    private Emitter emitter;
    private Table tableUI;
    private Window winnerWindow;
    private ImageButton actionButton, arrowUpButton, arrowDownButton, arrowLeftButton ,arrowRightButton;
    private Label hintLabel;
    private List<Zone> drawingZones = new LinkedList<Zone>();
    private float[] dropsXYPositions;
    private Body groundBody;
    private MouseJoint mouseJoint;

    private boolean wonGame;
    private boolean debugRendererEnabled;
    private float time;
    private float timeLastDrop;
    private boolean itRain;
    private int levelNumber = 0;
    private String winHint;
    private final boolean lightVersion;
    private int dropsMax;
    private boolean physicsEnabled = true;
    private boolean liquidForcesEnabled = true;
    private boolean useShader = true;
    private TouchType pressingAction = TouchType.NONE;
    private Vector2 cursorPosition;
    private boolean drawingDrops;
    private boolean keepDropsForever = false;

    public GameWorld(String name) {
        setViewport(gameViewport);
        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        ParticleSystemDef particleSystemDef = new ParticleSystemDef();
        particleSystemDef.radius = PARTICLE_RADIUS * GameWorld.WORLD_TO_BOX;
        //particleSystemDef.pressureStrength = 0.4f;
        particleSystemDef.dampingStrength = 0.2f;

        particleSystem = new ParticleSystem(physicsWorld, particleSystemDef);

        //super(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, new SpriteBatch(3000, 10));
        lightVersion = RainGame.isLightVersion();
        dropsMax = lightVersion ? 2000 : 2000;
        dropsXYPositions = new float[dropsMax * 2];

        String filename = "data/levels/" + name + ".lua";

        if(name.replaceAll("[\\D]", "").length() > 0)
            levelNumber = Integer.parseInt(name.replaceAll("[\\D]", ""));
        String filenameMain = "data/levels/main.lua";
        ScriptEngine engine = new LuaScriptEngine();
        CompiledScript cs;

        try {
            //Reader reader = new FileReader(filename);
            if(!Gdx.files.internal(filename).exists())
                filename = "data/levels/test.lua";
            Reader reader = new StringReader(
                    Gdx.files.internal(filenameMain).readString() + Gdx.files.internal(filename).readString());
            cs = ((Compilable)engine).compile(reader);
            SimpleBindings sb = new SimpleBindings();
            cs.eval(sb);
            luaOnCheckFunc = (LuaFunction) sb.get("onCheck");
            luaOnCreateFunc = (LuaFunction) sb.get("onCreate");
            luaOnBeginContactFunc = (LuaFunction) sb.get("onBeginContact");
            luaOnEndContactFunc = (LuaFunction) sb.get("onEndContact");
        } catch (Exception e) {
            //LogHandler.log.error(e.getMessage(), e);
            System.out.println("error: " + filename + ". " + e);
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

        createUI();
    }

    private void createUI() {
        tableUI = new Table();
        tableUI.setFillParent(true);
        tableUI.debug();
        addActor(tableUI);

        hintLabel = new Label("", skin);
        tableUI.add(hintLabel).left();

        Button menuButton = new TextButton("Main Menu", skin);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showWinnerMenu();
            }
        });
        tableUI.add(menuButton).right().top();

        /*if(cloud == null && emitter == null)
            return;*/

        tableUI.row();

        tableUI.add().expand();

        tableUI.row();

        Table controlButtonsTable = new Table();
        controlButtonsTable.debug();
        tableUI.add(controlButtonsTable).left();
        Sprite arrowLeftSprite = TextureManager.getSpriteFromDefaultAtlas("arrow");
        Sprite arrowDownSprite = TextureManager.getSpriteFromDefaultAtlas("arrow");
        Sprite arrowRightSprite = TextureManager.getSpriteFromDefaultAtlas("arrow");
        actionButton = new ImageButton(new SpriteDrawable(TextureManager.getSpriteFromDefaultAtlas("button")));
        arrowUpButton = new ImageButton(new SpriteDrawable(TextureManager.getSpriteFromDefaultAtlas("arrow")));
        arrowDownButton = new ImageButton(new SpriteDrawable(arrowDownSprite));
        arrowLeftButton = new ImageButton(new SpriteDrawable(arrowLeftSprite));
        arrowRightButton = new ImageButton(new SpriteDrawable(arrowRightSprite));
        actionButton.setVisible(false);
        arrowUpButton.setVisible(false);
        arrowDownButton.setVisible(false);
        arrowLeftButton.setVisible(false);
        arrowRightButton.setVisible(false);
        arrowLeftButton.getImage().setOrigin(arrowLeftSprite.getWidth() / 2, arrowLeftSprite.getHeight() / 2);
        arrowDownButton.getImage().setOrigin(arrowLeftSprite.getWidth() / 2, arrowLeftSprite.getHeight() / 2);
        arrowRightButton.getImage().setOrigin(arrowLeftSprite.getWidth() / 2, arrowLeftSprite.getHeight() / 2);
        arrowLeftButton.getImage().setRotation(90);
        arrowDownButton.getImage().setRotation(180);
        arrowRightButton.getImage().setRotation(-90);
        controlButtonsTable.setFillParent(true);
        controlButtonsTable.bottom();
        controlButtonsTable.defaults().width(100).height(100);
        controlButtonsTable.add(arrowUpButton).colspan(3);
        controlButtonsTable.row();
        controlButtonsTable.add(arrowLeftButton);
        controlButtonsTable.add(arrowDownButton);
        controlButtonsTable.add(arrowRightButton);
        controlButtonsTable.add(actionButton).expandX().right();
        arrowLeftButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                handleAction(Input.Keys.LEFT, false);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleAction(Input.Keys.LEFT, true);
                return true;
            }
        });
        arrowRightButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                handleAction(Input.Keys.RIGHT, false);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleAction(Input.Keys.RIGHT, true);
                return true;
            }
        });
        arrowUpButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                handleAction(Input.Keys.UP, false);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleAction(Input.Keys.UP, true);
                return true;
            }
        });
        arrowDownButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                handleAction(Input.Keys.DOWN, false);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleAction(Input.Keys.DOWN, true);
                return true;
            }
        });
        actionButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                handleAction(Input.Keys.SPACE, false);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleAction(Input.Keys.SPACE, true);
                return true;
            }
        });
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
    }

    public void createWorld() {
        LuaValue luaWorld = CoerceJavaToLua.coerce(this);
        if(luaOnCreateFunc != null)
            luaOnCreateFunc.call(luaWorld);

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

        super.act(delta);

        for(int i = 0; i < src.length; i++)
            dropsXYPositions[i] = src[i] * BOX_TO_WORLD;

        LuaValue luaDropsCount = CoerceJavaToLua.coerce(particleSystem.getParticleCount());
        LuaValue luaWorld = CoerceJavaToLua.coerce(this);
        LuaValue retVal = luaOnCheckFunc.call(luaWorld/*luaDropsPosArray*/, luaDropsCount);
        if(retVal.toboolean(1) && !wonGame) {
            wonGame = true;
            showWinnerMenu();
        }

        if(dropList.size() < dropsMax && winnerWindow == null) {
            if((itRain || emitter != null && emitter.isAutoFire()) &&
                    !wonGame && (emitter != null || cloud != null) &&
                    time - timeLastDrop > 0.01) {
                Drop drop = new Drop();
                Random r = new Random();
                SimpleActor actor = cloud != null ? cloud : emitter;
                float offset = r.nextFloat() * actor.getWidth() * 0.1f;
                drop.setPosition(new Vector2(actor.getPosition().x + offset, actor.getPosition().y));
                dropsToCreate.add(drop);
                timeLastDrop = time;
            }

            if(drawingDrops && time - timeLastDrop > 0.04) {
                boolean inZone = drawingZones.isEmpty();
                for(Zone zone : drawingZones) {
                    if(zone.rectangle.contains(cursorPosition)) {
                        inZone = true;
                        break;
                    }
                }

                if(inZone) {
                    Drop drop = new Drop();
                    Random r = new Random();
                    int offset = r.nextInt(20);
                    drop.setPosition(cursorPosition.cpy().add(offset, offset));
                    drop.setLinearVelocity(new Vector2(100000, 0));
                    dropsToCreate.add(drop);
                    timeLastDrop = time;
                }
            }
        }

        if(pressingAction == TouchType.PICKING_DROPS && cursorPosition != null) {
            for(int i = 0, selectedDropsSize = selectedDrops.size(); i < selectedDropsSize; i++) {
                Drop d = selectedDrops.get(i);
                Vector2 pos = d.getPosition();
                d.particleGroup.applyForce(new Vector2((cursorPosition.x - pos.x),
                        (cursorPosition.y - pos.y)).scl(0.01f));
            }
        }
    }

    public void setKeepDropsForever(boolean state) {
        keepDropsForever = state;
    }

    private void deleteUnnecessaryDrops() {
        if(keepDropsForever)
            return;

        float[] pos = particleSystem.getParticlePositionBufferArray(false);
        for(int i = particleSystem.getParticleCount() - 1; i >= 0; i--)
            if(pos[i*2] < MIN_DROP_X_POS || pos[i*2] > MAX_DROP_X_POS || pos[i*2+1] < MIN_DROP_Y_POS)
                dropsToDelete.add(dropList.get(i));
    }

    private void showWinnerMenu() {
        if(winnerWindow != null)
            return;

        winnerWindow = new Window(wonGame ? "Victory!" : "Menu", skin);
        winnerWindow.setSize(GameViewport.WIDTH / 1.5f, GameViewport.HEIGHT / 1.5f);
        winnerWindow.setPosition(GameViewport.WIDTH / 2f - winnerWindow.getWidth() / 2f,
                GameViewport.HEIGHT / 2f - winnerWindow.getHeight() / 2f);
        winnerWindow.setModal(true);
        winnerWindow.setMovable(false);
        winnerWindow.setKeepWithinStage(false);
        winnerWindow.debug();
        addActor(winnerWindow);

        winnerWindow.row().width(400).padTop(10);

        final TextButton nextOrContinueButton = new TextButton(!wonGame ? "Continue" : "Next", skin);
        winnerWindow.add(nextOrContinueButton);

        winnerWindow.row().width(400).padTop(10);

        final TextButton restartButton = new TextButton("Restart", skin);
        winnerWindow.add(restartButton);

        winnerWindow.row().width(400).padTop(10);

        final TextButton mainMenuButton = new TextButton("Back to levels", skin);
        winnerWindow.add(mainMenuButton);

        nextOrContinueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(wonGame)
                    RainGame.getInstance().setLevel("level" + (levelNumber + 1));
                else {
                    winnerWindow.remove();
                    winnerWindow = null;
                }
            }
        });

        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RainGame.getInstance().setLevel("level" + levelNumber);
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RainGame.getInstance().setMenu(new LevelsMenuScreen());
            }
        });

        winnerWindow.toFront();
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        cursorPosition = getCursorPosition(screenX, screenY).cpy();
        if(pressingAction == TouchType.PICKING_DROPS) {
            cursorPosition.scl(WORLD_TO_BOX);
            selectedDrops = new ArrayList<Drop>();
            float[] pos = particleSystem.getParticlePositionBufferArray(false);
            for(int i = 0; i < pos.length; i += 2) {
                if(Math.abs(cursorPosition.x - pos[i]) < 1 && Math.abs(cursorPosition.y - pos[i + 1]) < 1)
                    selectedDrops.add(dropList.get(i / 2));
            }
            cursorPosition.scl(BOX_TO_WORLD);
        } else if(pressingAction == TouchType.DRAWING) {
            drawingDrops = true;
        } else if(pressingAction == TouchType.PICKING_BODIES) {
            SimpleActor targetActor = null;
            for(SimpleActor actor : actorList) {
                if((actor.getType() == SimpleActor.TYPE.BALL || actor.getType() == SimpleActor.TYPE.TRIGGER) && actor.isInAABB(cursorPosition)) {
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
                mouseJointDef.dampingRatio = 0.2f;
                mouseJointDef.frequencyHz = 30;
                mouseJointDef.maxForce = 200.0f * targetActor.getBody().getMass();
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
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(winnerWindow != null)
            return true;

        if(cursorPosition != null) {
            cursorPosition.set(getCursorPosition(screenX, screenY));

            if(mouseJoint != null) {
                cursorPosition.scl(WORLD_TO_BOX);
                mouseJoint.setTarget(cursorPosition);
                cursorPosition.scl(BOX_TO_WORLD);
            }
        }

        if(wonGame || cloud != null || emitter != null || dropList.size() > dropsMax)
            return true;

        return false;
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
            if(winnerWindow != null)
                RainGame.getInstance().setMenu(new MainMenuScreen());
            showWinnerMenu();
        } else if(keyCode == Input.Keys.LEFT || keyCode == Input.Keys.RIGHT || keyCode == Input.Keys.UP ||
                keyCode == Input.Keys.DOWN || keyCode == Input.Keys.SPACE)
            handleAction(keyCode, true);

        return false;
    }

    @Override
    public boolean keyUp(int keyCode) {
        if(keyCode == Input.Keys.LEFT || keyCode == Input.Keys.RIGHT || keyCode == Input.Keys.UP ||
                keyCode == Input.Keys.DOWN || keyCode == Input.Keys.SPACE)
            handleAction(keyCode, false);

        return true;
    }

    private void handleAction(int keyCode, boolean pressed) {
        if(keyCode == Input.Keys.LEFT) {
            if(cloud != null) {
                if(pressed) {
                    cloud.setLinearVelocity(new Vector2(-150, 0));
                    cloud.setDirection(1);
                } else {
                    cloud.setLinearVelocity(new Vector2(0, 0));
                    cloud.setDirection(0);
                }
            }
        } else if(keyCode == Input.Keys.RIGHT) {
            if(cloud != null) {
                if(pressed) {
                    cloud.setLinearVelocity(new Vector2(150, 0));
                    cloud.setDirection(2);
                } else {
                    cloud.setLinearVelocity(new Vector2(0, 0));
                    cloud.setDirection(0);
                }
            }
        } else if(keyCode == Input.Keys.UP) {
            if(emitter != null) {
                if(pressed)
                    emitter.setLinearVelocity(new Vector2(0, 150));
                else
                    emitter.setLinearVelocity(new Vector2(0, 0));
            }
        } else if(keyCode == Input.Keys.DOWN) {
            if(emitter != null) {
                if(pressed)
                    emitter.setLinearVelocity(new Vector2(0, -150));
                else
                    emitter.setLinearVelocity(new Vector2(0, 0));
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

        for(Zone zone : drawingZones) {
            RainGame.shapeRenderer.setColor(zone.color);
            RainGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                RainGame.shapeRenderer.line(zone.rectangle.x, zone.rectangle.y, zone.rectangle.x, zone.rectangle.y + zone.rectangle.height);
                RainGame.shapeRenderer.line(zone.rectangle.x, zone.rectangle.y + zone.rectangle.height, zone.rectangle.x + zone.rectangle.width, zone.rectangle.y + zone.rectangle.height);
                RainGame.shapeRenderer.line(zone.rectangle.x + zone.rectangle.width, zone.rectangle.y, zone.rectangle.x + zone.rectangle.width, zone.rectangle.y + zone.rectangle.height);
                RainGame.shapeRenderer.line(zone.rectangle.x, zone.rectangle.y, zone.rectangle.x + zone.rectangle.width, zone.rectangle.y);
            RainGame.shapeRenderer.end();
        }

        sb.begin();
            getRoot().draw(sb, 1);
        sb.end();

        if(debugRendererEnabled) {
            debugRenderer.render(physicsWorld, getCamera().combined.cpy().scale(BOX_TO_WORLD, BOX_TO_WORLD, 1));
            particleDebugRendererCircle.render(particleSystem, PARTICLE_RADIUS / 2.5f * BOX_TO_WORLD * gameViewport.scale, getCamera().combined.cpy().scale(BOX_TO_WORLD, BOX_TO_WORLD, 1));
            particleDebugRendererDot.render(particleSystem, PARTICLE_RADIUS / 10f * BOX_TO_WORLD * gameViewport.scale, getCamera().combined.cpy().scale(BOX_TO_WORLD, BOX_TO_WORLD, 1));

            hintLabel.setText((winHint != null ? "Hint: " + winHint + "\n" : "") +
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
        }

        if(debugRendererEnabled) {
            RainGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            if(winnerWindow != null)
                winnerWindow.drawDebug(RainGame.shapeRenderer);
            if(tableUI != null)
                tableUI.drawDebug(RainGame.shapeRenderer);
            RainGame.shapeRenderer.end();
        }
    }

    public void setWinHint(String winHint) {
        this.winHint = winHint;
        hintLabel.setText("Hint: " + winHint);
    }

    public PolygonSpriteBatch getPolygonSpriteBatch() {
        return polygonSpriteBatch;
    }

    public void setPressingAction(int action) {
        pressingAction = TouchType.values()[action];
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
        for(SimpleActor sa : actorList)
            getRoot().removeActor(sa);
        actorList.clear();
        emitter = null;
        cloud = null;
    }

    public HashMap<Long, SimpleActor> getGameActors() {
        return actorsMap;
    }

    public void removeActor(SimpleActor selectedActor) {
        getRoot().removeActor(selectedActor);
        if(emitter == selectedActor)
            emitter = null;

        if(cloud == selectedActor)
            cloud = null;
    }
}
