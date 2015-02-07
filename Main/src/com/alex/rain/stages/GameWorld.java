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
import com.alex.rain.helpers.LiquidHelper;
import com.alex.rain.listeners.GameContactListener;
import com.alex.rain.managers.TextureManager;
import com.alex.rain.models.Cloud;
import com.alex.rain.models.Drop;
import com.alex.rain.models.Emitter;
import com.alex.rain.models.SimpleActor;
import com.alex.rain.renderer.ParticleRenderer;
import com.alex.rain.screens.MainMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
    private List<Actor> uiActorList = new ArrayList<Actor>();
    private ArrayList<Drop> dropList = new ArrayList<Drop>();
    private LiquidHelper liquidHelper;
    private LuaFunction luaOnCreateFunc;
    private LuaFunction luaOnCheckFunc;
    private LuaFunction luaOnBeginContactFunc;
    private LuaFunction luaOnEndContactFunc;
    private boolean wonGame;
    private Table table, tableControl;
    private ShaderProgram shader;
    private Sprite backgroundSprite;
    private final Box2DDebugRenderer debugRenderer;
    private final ParticleDebugRenderer particleDebugRendererCircle;
    private final ParticleDebugRenderer particleDebugRendererDot;
    private final ParticleRenderer particleRenderer;
    private boolean debugRendererEnabled;
    private final SpriteBatch spriteBatchShadered;
    private final PolygonSpriteBatch polygonSpriteBatch;
    private final FrameBuffer m_fbo;
    private final TextureRegion m_fboRegion;
    private final Batch sb;
    private final Camera cam;
    private float time;
    private float timeLastDrop;
    private boolean itRain;
    private Cloud cloud;
    private Emitter emitter;
    private BitmapFont font = new BitmapFont();
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
    private Skin skin = new Skin();
    private int pressingAction = 0;
    private Vector2 cursorPosition;
    private List<Drop> selectedDrops;
    private ParticleSystemDef particleSystemDef;
    private float PARTICLE_RADIUS = 7f;

    public GameWorld(String name) {
        particleSystemDef = new ParticleSystemDef();
        particleSystemDef.radius = PARTICLE_RADIUS * GameWorld.WORLD_TO_BOX;
        //particleSystemDef.pressureStrength = 0.4f;
        particleSystemDef.dampingStrength = 0.2f;

        particleSystem = new ParticleSystem(physicsWorld, particleSystemDef);

        //super(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, new SpriteBatch(3000, 10));
        lightVersion = RainGame.isLightVersion();
        dropsMax = lightVersion ? 1000 : 1000;
        liquidHelper = new LiquidHelper(dropList, lightVersion);

        String filename = "data/" + name + ".lua";

        if(name.replaceAll("[\\D]", "").length() > 0)
            levelNumber = Integer.parseInt(name.replaceAll("[\\D]", ""));
        String filenameMain = "data/main.lua";
        ScriptEngine engine = new LuaScriptEngine();
        CompiledScript cs;

        try {
            //Reader reader = new FileReader(filename);
            if(!Gdx.files.internal(filename).exists())
                filename = "data/test.lua";
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

        final String VERTEX = Gdx.files.internal("data/drop_shader.vert").readString();
        final String FRAGMENT = lightVersion ?
                Gdx.files.internal("data/drop_shader_light.frag").readString() :
                Gdx.files.internal("data/drop_shader.frag").readString();

        shader = new ShaderProgram(VERTEX, FRAGMENT);
        if(!shader.isCompiled())
            System.out.println(shader.getLog());

        backgroundSprite = TextureManager.getInstance().getSpriteFromDefaultAtlas("background");

        spriteBatchShadered = new SpriteBatch();
        spriteBatchShadered.setShader(shader);

        sb = getBatch();
        cam = getCamera();

        polygonSpriteBatch = new PolygonSpriteBatch();

        m_fbo = new FrameBuffer(Pixmap.Format.RGBA4444, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        m_fboRegion = new TextureRegion(m_fbo.getColorBufferTexture());
        m_fboRegion.flip(false, true);

        debugRenderer = new Box2DDebugRenderer();
        particleDebugRendererDot = new ParticleDebugRenderer(Color.RED, 100000);
        particleDebugRendererCircle = new ParticleDebugRenderer(Color.BLUE, 100000);
        particleRenderer = new ParticleRenderer(Color.RED, 100000);

        contactListener = new GameContactListener(luaOnBeginContactFunc, luaOnEndContactFunc);
        physicsWorld.setContactListener(contactListener);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        skin.add("default", new BitmapFont());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        skin.add("default", labelStyle);
    }

    private void createControls() {
        tableControl = new Table();
        tableControl.setFillParent(true);
        tableControl.debug();
        addUI(tableControl);
        Sprite arrowLeftSprite = TextureManager.getInstance().getSpriteFromDefaultAtlas("arrow");
        Sprite arrowDownSprite = TextureManager.getInstance().getSpriteFromDefaultAtlas("arrow");
        Sprite arrowRightSprite = TextureManager.getInstance().getSpriteFromDefaultAtlas("arrow");
        arrowLeftSprite.setRotation(90);
        arrowDownSprite.setRotation(180);
        arrowRightSprite.setRotation(-90);
        ImageButton arrowUpButton = new ImageButton(new SpriteDrawable(TextureManager.getInstance().getSpriteFromDefaultAtlas("arrow")));
        ImageButton arrowDownButton = new ImageButton(new SpriteDrawable(arrowDownSprite));
        ImageButton arrowLeftButton = new ImageButton(new SpriteDrawable(arrowLeftSprite));
        ImageButton arrowRightButton = new ImageButton(new SpriteDrawable(arrowRightSprite));
        ImageButton actionButton = new ImageButton(new SpriteDrawable(TextureManager.getInstance().getSpriteFromDefaultAtlas("button")));
        tableControl.left();
        tableControl.bottom();
        tableControl.add(arrowUpButton).colspan(3);
        tableControl.row();
        tableControl.add(arrowLeftButton);
        tableControl.add(arrowDownButton);
        tableControl.add(arrowRightButton);
        tableControl.add(actionButton);
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

        if((cloud != null || emitter != null) && lightVersion)
            createControls();
        if(tableControl != null)
            tableControl.toFront();
    }

    public void addUI(Actor actor) {
        addActor(actor);
        uiActorList.add(actor);
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

        super.act(delta);

        LuaValue luaDrop = CoerceJavaToLua.coerce(dropList);
        LuaValue retvals = luaOnCheckFunc.call(luaDrop);
        if(retvals.toboolean(1) && !wonGame) {
            wonGame = true;
            showWinnerMenu();
        }

        if(itRain && !wonGame && cloud != null && dropList.size() < dropsMax) {
            if(time - timeLastDrop > (lightVersion ? 0.09 : 0.05)) {
                Drop drop = new Drop();
                Random r = new Random();
                float offset = r.nextFloat() * cloud.getWidth() * 2/3;
                drop.setPosition(new Vector2(cloud.getPosition().x - cloud.getWidth() / 3 + offset, cloud.getPosition().y));
                add(drop);
                //drop.getBody().applyForceToCenter(new Vector2(0, -drop.getBody().getMass() * 20 / delta), true);
                timeLastDrop = time;
            }
        }

        if(itRain && !wonGame && emitter != null && dropList.size() < dropsMax) {
            if(time - timeLastDrop > (lightVersion ? 0.09 : 0.05)) {
                Drop drop = new Drop();
                Random r = new Random();
                float offset = r.nextFloat() * emitter.getWidth() * 2/3;
                drop.setPosition(new Vector2(emitter.getPosition().x - emitter.getWidth() / 3 + offset, emitter.getPosition().y));
                drop.setLinearVelocity(new Vector2(100000, 0));
                add(drop);
                //drop.getBody().applyForceToCenter(new Vector2(drop.getBody().getMass() * 30 / delta, 0), true);
                timeLastDrop = time;
            }
        }

        if((pressingAction == 1 || pressingAction == 2) && cursorPosition != null) {
            if(pressingAction == 2)
                selectedDrops = liquidHelper.getDrops(cursorPosition, 50f);
            for(Drop d : selectedDrops) {
                d.particleGroup.applyForce(new Vector2((cursorPosition.x - d.getPosition().x) ,
                        (cursorPosition.y - d.getPosition().y)).nor().scl(0.8f));
            }
        }
    }

    private void showWinnerMenu() {
        if(table != null)
            return;

        table = new Table();
        table.setFillParent(true);
        table.debug();
        addUI(table);

        table.row().width(100).padTop(10);

        final Label label = new Label(wonGame ? "Victory!" : "Menu", skin);
        table.add(label);

        table.row().width(400).padTop(10);

        final TextButton button = new TextButton("Next", skin);
        table.add(button);

        table.row().width(400).padTop(10);

        final TextButton button2 = new TextButton("Restart", skin);
        table.add(button2);

        table.row().width(400).padTop(10);

        final TextButton button3 = new TextButton("Back to main menu", skin);
        table.add(button3);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RainGame.getInstance().setLevel("level" + (levelNumber + 1));
            }
        });

        button2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RainGame.getInstance().setLevel("level" + levelNumber);
            }
        });

        button3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RainGame.getInstance().setMenu(new MainMenuScreen());
            }
        });
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        cursorPosition = new Vector2(screenX * 800f / Gdx.graphics.getWidth(),
                (Gdx.graphics.getHeight() - screenY) * 480f / Gdx.graphics.getHeight());
        cursorPosition.scl(WORLD_TO_BOX);
        selectedDrops = new ArrayList<Drop>();
        float[] pos = particleSystem.getParticlePositionBufferArray(false);
        for(int i = 0; i < pos.length; i += 2) {
            if(Math.abs(cursorPosition.x - pos[i]) < 1 && Math.abs(cursorPosition.y - pos[i + 1]) < 1)
                selectedDrops.add(dropList.get(i / 2));
        }
        cursorPosition.scl(BOX_TO_WORLD);
        //selectedDrops = liquidHelper.getDrops(cursorPosition, 50f);
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
        if(cursorPosition != null)
            cursorPosition.set(getCursorPosition(screenX, screenY));

        if(wonGame || cloud != null || emitter != null || dropList.size() > dropsMax)
            return true;

        Vector2 cp = getCursorPosition(screenX, screenY);
        if(pressingAction == 0 && lastCreatedDropPos.dst(cp) > 10) {
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
        return new Vector2(screenX * 800f / Gdx.graphics.getWidth(),
                (Gdx.graphics.getHeight() - screenY) * 480f / Gdx.graphics.getHeight());
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
            if(table != null)
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

    private void drawDrops() {
        particleRenderer.render(particleSystem, PARTICLE_RADIUS / 2 * BOX_TO_WORLD, cam.combined.cpy().scale(BOX_TO_WORLD, BOX_TO_WORLD, 1));

    }

    @Override
    public void draw() {
        cam.viewportHeight = 480;
        cam.viewportWidth = 800;
        cam.position.set(cam.viewportWidth * .5f, cam.viewportHeight * .5f, 0f);
        cam.update();
        sb.setProjectionMatrix(cam.combined);
        polygonSpriteBatch.setProjectionMatrix(cam.combined);
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
            sb.draw(backgroundSprite, 0, 0);
        sb.end();

        if(m_fbo != null && useShader) {
            m_fbo.begin();
                sb.begin();
                    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                    drawDrops();
                sb.end();
            m_fbo.end();

            spriteBatchShadered.begin();
                /*if(!lightVersion)
                    shader.setUniformf("u_time", time);*/
                spriteBatchShadered.draw(m_fboRegion, 0, 0, m_fboRegion.getRegionWidth(), m_fboRegion.getRegionHeight());
            spriteBatchShadered.end();
        } else {
            sb.begin();
                drawDrops();
            sb.end();
        }

        sb.begin();
            if(table!=null)
                table.setVisible(false);
            getRoot().draw(sb, 1);
            if(table!=null)
                table.setVisible(true);
        sb.end();

        if(debugRendererEnabled) {
            cam.viewportHeight *= WORLD_TO_BOX;
            cam.viewportWidth *= WORLD_TO_BOX;
            cam.position.set(cam.viewportWidth * .5f, cam.viewportHeight * .5f, 0f);
            cam.update();
            //sb.setProjectionMatrix(cam.combined);

            liquidHelper.drawDebug();
            debugRenderer.render(physicsWorld, cam.combined);
            particleDebugRendererCircle.render(particleSystem, PARTICLE_RADIUS / 2.5f * BOX_TO_WORLD, cam.combined);
            particleDebugRendererDot.render(particleSystem, PARTICLE_RADIUS / 10f * BOX_TO_WORLD, cam.combined);
        }

        cam.viewportHeight = Gdx.graphics.getHeight();
        cam.viewportWidth = Gdx.graphics.getWidth();
        cam.position.set(cam.viewportWidth * .5f, cam.viewportHeight * .5f, 0f);
        cam.update();
        sb.setProjectionMatrix(cam.combined);

        sb.begin();
            if(table != null) {
                table.setPosition(getRoot().getX(), getRoot().getY());
                table.draw(sb, 1f);
            }
            font.draw(sb, "FPS: "+Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight()-20);
            font.draw(sb, "Drops: "+getDropsNumber(), 10, Gdx.graphics.getHeight()-40);
            if(winHint != null)
                font.draw(sb, "Hint: "+winHint, 10, Gdx.graphics.getHeight()-60);
        sb.end();

        /*if(debugRendererEnabled)
            Table.drawDebug(this);*/
    }

    public void setWinHint(String winHint) {
        this.winHint = winHint;
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
