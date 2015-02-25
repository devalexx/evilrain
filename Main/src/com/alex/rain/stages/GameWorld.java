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
import com.alex.rain.models.Cloud;
import com.alex.rain.models.Drop;
import com.alex.rain.models.Emitter;
import com.alex.rain.models.SimpleActor;
import com.alex.rain.renderer.ParticleRenderer;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWorld extends Stage {
    private World physicsWorld = new World(new Vector2(0, -9.8f), true);
    private ParticleSystem particleSystem;
    private List<SimpleActor> actorList = new ArrayList<SimpleActor>();
    private ArrayList<Drop> dropList = new ArrayList<Drop>();
    private LuaFunction luaOnCreateFunc;
    private LuaFunction luaOnCheckFunc;
    private LuaFunction luaOnBeginContactFunc;
    private LuaFunction luaOnEndContactFunc;
    private boolean wonGame;
    private Table tableUI;
    private Window winnerWindow;
    private ShaderProgram shader;
    private Texture backgroundTexture;
    private final Box2DDebugRenderer debugRenderer;
    private final ParticleDebugRenderer particleDebugRendererCircle;
    private final ParticleDebugRenderer particleDebugRendererDot;
    private final ParticleRenderer particleRenderer;
    private boolean debugRendererEnabled;
    private final SpriteBatch spriteBatchShadered;
    private final PolygonSpriteBatch polygonSpriteBatch;
    private FrameBuffer m_fbo;
    private TextureRegion m_fboRegion;
    private final Batch sb;
    private float time;
    private float timeLastDrop;
    private boolean itRain;
    private Cloud cloud;
    private Emitter emitter;
    private int levelNumber = 0;
    private String winHint;
    private final boolean lightVersion;
    private int dropsMax;
    private boolean physicsEnabled = true;
    private boolean liquidForcesEnabled = true;
    private boolean useShader = true;
    private GameContactListener contactListener;
    public static final float WORLD_TO_BOX = 0.01f;
    public static final float BOX_TO_WORLD = 1 / WORLD_TO_BOX;
    private Skin skin = ResourceManager.getSkin();
    private int pressingAction = 0;
    private Vector2 cursorPosition;
    private List<Drop> selectedDrops;
    private float PARTICLE_RADIUS = 7f;
    private GameViewport gameViewport = new GameViewport();
    private Label hintLabel;

    public GameWorld(String name) {
        setViewport(gameViewport);
        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        ParticleSystemDef particleSystemDef = new ParticleSystemDef();
        particleSystemDef.radius = PARTICLE_RADIUS * GameWorld.WORLD_TO_BOX;
        //particleSystemDef.pressureStrength = 0.4f;
        particleSystemDef.dampingStrength = 0.5f;

        particleSystem = new ParticleSystem(physicsWorld, particleSystemDef);

        //super(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, new SpriteBatch(3000, 10));
        lightVersion = RainGame.isLightVersion();
        dropsMax = lightVersion ? 2000 : 2000;

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

        shader = new ShaderProgram(VERTEX, FRAGMENT);
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

        contactListener = new GameContactListener(luaOnBeginContactFunc, luaOnEndContactFunc);
        physicsWorld.setContactListener(contactListener);

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
        ImageButton arrowUpButton = new ImageButton(new SpriteDrawable(TextureManager.getSpriteFromDefaultAtlas("arrow")));
        ImageButton arrowDownButton = new ImageButton(new SpriteDrawable(arrowDownSprite));
        ImageButton arrowLeftButton = new ImageButton(new SpriteDrawable(arrowLeftSprite));
        ImageButton arrowRightButton = new ImageButton(new SpriteDrawable(arrowRightSprite));
        arrowLeftButton.getImage().setOrigin(arrowLeftSprite.getWidth() / 2, arrowLeftSprite.getHeight() / 2);
        arrowDownButton.getImage().setOrigin(arrowLeftSprite.getWidth() / 2, arrowLeftSprite.getHeight() / 2);
        arrowRightButton.getImage().setOrigin(arrowLeftSprite.getWidth() / 2, arrowLeftSprite.getHeight() / 2);
        arrowLeftButton.getImage().setRotation(90);
        arrowDownButton.getImage().setRotation(180);
        arrowRightButton.getImage().setRotation(-90);
        ImageButton actionButton = new ImageButton(new SpriteDrawable(TextureManager.getSpriteFromDefaultAtlas("button")));
        controlButtonsTable.left();
        controlButtonsTable.bottom();
        controlButtonsTable.defaults().width(100).height(100);
        controlButtonsTable.add(arrowUpButton).colspan(3);
        controlButtonsTable.row();
        controlButtonsTable.add(arrowLeftButton);
        controlButtonsTable.add(arrowDownButton);
        controlButtonsTable.add(arrowRightButton);
        controlButtonsTable.add(actionButton);
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

        if(actor.getType() == SimpleActor.TYPE.CLOUD)
            cloud = (Cloud)actor;
        else if(actor.getType() == SimpleActor.TYPE.EMITTER)
            emitter = (Emitter)actor;

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
    }

    @Override
    public void act(float delta) {
        final float dt = Gdx.graphics.getDeltaTime();
        final float step = 1 / 60f;
        time += dt;

        /*if(dt < 1/120f)
            dt = 1/120f;
        else if(dt > 1/60f)
            dt = 1/60f;*/

        /*if(liquidForcesEnabled)
            liquidHelper.applyLiquidConstraint(dt);*/
        if(physicsEnabled)
            physicsWorld.step(step, 4, 2, 6);
        particleSystem.getParticlePositionBufferArray(true);

        removeUnnecessaryDrops();

        super.act(delta);

        LuaValue luaDrop = CoerceJavaToLua.coerce(dropList);
        LuaValue retvals = luaOnCheckFunc.call(luaDrop);
        if(retvals.toboolean(1) && !wonGame) {
            wonGame = true;
            showWinnerMenu();
        }

        if((itRain || emitter != null && emitter.isAutoFire()) &&
                !wonGame && (emitter != null || cloud != null) && dropList.size() < dropsMax &&
                time - timeLastDrop > 0.05) {
            Drop drop = new Drop();
            Random r = new Random();
            float offset = r.nextFloat() * emitter.getWidth() * 0.1f;
            drop.setPosition(new Vector2(emitter.getPosition().x + offset, emitter.getPosition().y));
            add(drop);
            timeLastDrop = time;
        }

        if(pressingAction == 2 && cursorPosition != null) {
            for(Drop d : selectedDrops) {
                d.particleGroup.applyForce(new Vector2((cursorPosition.x - d.getPosition().x) ,
                        (cursorPosition.y - d.getPosition().y)).nor().scl(0.8f));
            }
        }
    }

    private void removeUnnecessaryDrops() {
        float[] pos = particleSystem.getParticlePositionBufferArray(false);
        int minX = (int)(100 * WORLD_TO_BOX);
        int maxX = (int)(800 * WORLD_TO_BOX);
        int minY = (int)(100 * WORLD_TO_BOX);
        for(int i = 0; i < particleSystem.getParticleCount(); i++) {
            if(pos[i*2] < minX || pos[i*2] > maxX || pos[i*2+1] < minY) {
                particleSystem.destroyParticle(i);
                //if(i < dropList.size())
                Drop removedDrop = dropList.remove(i);
                if(selectedDrops != null)
                    selectedDrops.remove(removedDrop);

                for(int j = i; j < dropList.size(); j++) {
                    Drop drop = dropList.get(j);
                    drop.decrementIndex();
                }
            }
        }
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

        final TextButton mainMenuButton = new TextButton("Back to main menu", skin);
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
                RainGame.getInstance().setMenu(new MainMenuScreen());
            }
        });

        winnerWindow.toFront();
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        cursorPosition = new Vector2(screenX * (float)GameViewport.WIDTH / Gdx.graphics.getWidth(),
                (Gdx.graphics.getHeight() - screenY) * (float)GameViewport.HEIGHT / Gdx.graphics.getHeight());
        if(pressingAction == 2) {
            cursorPosition.scl(WORLD_TO_BOX);
            selectedDrops = new ArrayList<Drop>();
            float[] pos = particleSystem.getParticlePositionBufferArray(false);
            for(int i = 0; i < pos.length; i += 2) {
                if(Math.abs(cursorPosition.x - pos[i]) < 1 && Math.abs(cursorPosition.y - pos[i + 1]) < 1)
                    selectedDrops.add(dropList.get(i / 2));
            }
            cursorPosition.scl(BOX_TO_WORLD);

            for(Drop d : selectedDrops) {
                d.particleGroup.applyForce(new Vector2((cursorPosition.x - d.getPosition().x) ,
                        (cursorPosition.y - d.getPosition().y)).nor().scl(0.8f));
            }
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        cursorPosition = null;
        selectedDrops = null;
        //return super.touchUp(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
        return super.touchUp(screenX, screenY, pointer, button);
    }

    private Vector2 lastCreatedDropPos = new Vector2();
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(winnerWindow != null)
            return true;

        if(cursorPosition != null)
            cursorPosition.set(getCursorPosition(screenX, screenY));

        if(wonGame || cloud != null || emitter != null || dropList.size() > dropsMax)
            return true;

        Vector2 cp = getCursorPosition(screenX, screenY);
        if(pressingAction == 1 && lastCreatedDropPos.dst(cp) > 10) {
            Drop drop = new Drop();
            Random r = new Random();
            int offset = r.nextInt(10);
            lastCreatedDropPos.set(cp);
            drop.setPosition(lastCreatedDropPos);
            drop.setLinearVelocity(new Vector2(100000, 0));
            add(drop);
        }

        return false;
    }

    private Vector2 getCursorPosition(int screenX, int screenY) {
        Vector3 v = getCamera().unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(v.x, v.y);
    }

    @Override
    public boolean keyDown(int keyCode) {
        if(keyCode == Input.Keys.F4 || keyCode == Input.Keys.D)
            debugRendererEnabled = !debugRendererEnabled;
        else if(keyCode == Input.Keys.F5 || keyCode == Input.Keys.P)
            physicsEnabled = !physicsEnabled;
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
                    cloud.setLinearVelocity(new Vector2(-20, 0));
                    cloud.setDirection(1);
                } else {
                    cloud.setLinearVelocity(new Vector2(0, 0));
                    cloud.setDirection(0);
                }
            }
        } else if(keyCode == Input.Keys.RIGHT) {
            if(cloud != null) {
                if(pressed) {
                    cloud.setLinearVelocity(new Vector2(20, 0));
                    cloud.setDirection(2);
                } else {
                    cloud.setLinearVelocity(new Vector2(0, 0));
                    cloud.setDirection(0);
                }
            }
        } else if(keyCode == Input.Keys.UP) {
            if(emitter != null) {
                if(pressed)
                    emitter.setLinearVelocity(new Vector2(0, 20));
                else
                    emitter.setLinearVelocity(new Vector2(0, 0));
            }
        } else if(keyCode == Input.Keys.DOWN) {
            if(emitter != null) {
                if(pressed)
                    emitter.setLinearVelocity(new Vector2(0, -20));
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
        pressingAction = action;
    }

    @Override
    public void dispose() {
        particleSystem.destroyParticleSystem();
        physicsWorld.dispose();

        super.dispose();
    }
}
