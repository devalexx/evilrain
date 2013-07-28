package com.alex.rain.stages;

import com.alex.rain.RainGame;
import com.alex.rain.helpers.LiquidHelper;
import com.alex.rain.listeners.GameContactListener;
import com.alex.rain.managers.TextureManager;
import com.alex.rain.models.*;
import com.alex.rain.screens.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;
import org.luaj.vm2.script.LuaScriptEngine;

import javax.script.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * @author: Alexander Shubenkov
 * @since: 29.05.13
 */

public class GameWorld extends Stage {
    private World physicsWorld = new World(new Vector2(0, -9.8f), true);
    private List<SimpleActor> actorList = new ArrayList<SimpleActor>();
    private List<Actor> uiActorList = new ArrayList<Actor>();
    private ArrayList<Drop> dropList = new ArrayList<Drop>();
    private LiquidHelper liquidHelper;
    private LuaFunction luaOnCreateFunc;
    private LuaFunction luaOnCheckFunc;
    private LuaFunction luaOnBeginContactFunc;
    private LuaFunction luaOnEndContactFunc;
    private boolean wonGame;
    private Table table;
    private ShaderProgram shader;
    private Texture dropTexture, backgroundTexture;
    private final Box2DDebugRenderer debugRenderer;
    private boolean debugRendererEnabled;
    private final SpriteBatch sbS;
    private final FrameBuffer m_fbo;
    private final TextureRegion m_fboRegion;
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
    private final float dropTextureRadius;
    private boolean physicsEnabled = true;
    private boolean liquidForcesEnabled = true;
    private boolean useShader = true;
    private GameContactListener contactListener;
    public static final float WORLD_TO_BOX = 0.1f;
    public static final float BOX_TO_WORLD = 1 / WORLD_TO_BOX;

    public GameWorld(String name) {
        lightVersion = RainGame.isLightVersion();
        dropsMax = lightVersion ? 100 : 1000;
        liquidHelper = new LiquidHelper(dropList, lightVersion);

        String filename = "data/" + name + ".lua";

        if(name.replaceAll("[\\D]", "").length() > 0)
            levelNumber = Integer.parseInt(name.replaceAll("[\\D]", ""));
        String filenameMain = "data/main.lua";
        ScriptEngine engine = new LuaScriptEngine();
        CompiledScript cs;

        try {
            //Reader reader = new FileReader(filename);
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

        if (Gdx.graphics.isGL20Available()) {
            shader = new ShaderProgram(VERTEX, FRAGMENT);
            if(!shader.isCompiled())
                System.out.println(shader.getLog());
        }

        dropTexture = TextureManager.getInstance().getTexture("drop.png");
        dropTextureRadius = lightVersion ? dropTexture.getWidth() * 2f : dropTexture.getWidth();
        backgroundTexture = TextureManager.getInstance().getTexture("background.png");

        sbS = new SpriteBatch();
        sbS.setShader(shader);

        m_fbo = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        m_fboRegion = new TextureRegion(m_fbo.getColorBufferTexture());
        m_fboRegion.flip(false, true);

        debugRenderer = new Box2DDebugRenderer();

        contactListener = new GameContactListener(luaOnBeginContactFunc, luaOnEndContactFunc);
        physicsWorld.setContactListener(contactListener);
    }

    public void add(SimpleActor actor) {
        actor.createPhysicsActor(physicsWorld);
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
        time += Gdx.graphics.getDeltaTime();
        if(liquidForcesEnabled)
            liquidHelper.applyLiquidConstraint(1/60f); // TODO: check this shit?
        if(physicsEnabled)
            physicsWorld.step(1/60f, 6, 3);

        super.act(delta);

        LuaValue luaDrop = CoerceJavaToLua.coerce(dropList);
        LuaValue retvals = luaOnCheckFunc.call(luaDrop);
        if(retvals.toboolean(1) && !wonGame) {
            wonGame = true;
            showWinnerMenu();
        }

        if(itRain && !wonGame && cloud != null && dropList.size() < dropsMax) {
            if(time - timeLastDrop > (lightVersion ? 0.5 : 0.05)) {
                Drop drop = new Drop();
                Random r = new Random();
                float offset = r.nextFloat() * cloud.getWidth() * 2/3;
                add(drop);
                drop.setPosition(new Vector2(cloud.getPosition().x - cloud.getWidth() / 3 + offset, cloud.getPosition().y));
                timeLastDrop = time;
            }
        }

        if(itRain && !wonGame && emitter != null && dropList.size() < dropsMax) {
            if(time - timeLastDrop > (lightVersion ? 0.5 : 0.05)) {
                Drop drop = new Drop();
                Random r = new Random();
                float offset = r.nextFloat() * emitter.getWidth() * 2/3;
                add(drop);
                drop.setPosition(new Vector2(emitter.getPosition().x - emitter.getWidth() / 3 + offset, emitter.getPosition().y));
                drop.getBody().applyForceToCenter(new Vector2(drop.getBody().getMass() * 20 / delta, 0));
                timeLastDrop = time;
            }
        }
    }

    private void showWinnerMenu() {
        table = new Table();
        table.setFillParent(true);
        addUI(table);

        Skin skin = new Skin();

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

        table.row().width(400).padTop(10);

        final Label label = new Label(wonGame ? "Victory!" : "Menu", skin);
        table.add(label);
        label.setPosition(0, -100);

        table.row().width(400).padTop(10);

        final TextButton button = new TextButton("Next", skin);
        table.add(button);
        button.setPosition(0, -100);

        table.row().width(400).padTop(10);

        final TextButton button2 = new TextButton("Restart", skin);
        table.add(button2);
        button2.setPosition(0, 0);

        table.row().width(400).padTop(10);

        final TextButton button3 = new TextButton("Back to main menu", skin);
        table.add(button3);
        button3.setPosition(0, 100);

        button.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                RainGame.getInstance().setLevel("level" + (levelNumber + 1));
            }
        });

        button2.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                RainGame.getInstance().setLevel("level" + levelNumber);
            }
        });

        button3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RainGame.getInstance().setMenu(new MainMenuScreen());
            }
        });
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(wonGame || cloud != null || emitter != null || dropList.size() > dropsMax)
            return true;
        Drop drop = new Drop();
        Random r = new Random();
        int offset = r.nextInt(10) - 10;
        add(drop);
        drop.setPosition(new Vector2(screenX + offset, Gdx.graphics.getHeight() - screenY + offset));

        return true;
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
        else if(keyCode == Input.Keys.ESCAPE || keyCode == Input.Keys.Q)
            showWinnerMenu();
        else if(keyCode == Input.Keys.LEFT) {
            if(cloud != null) {
                cloud.setLinearVelocity(new Vector2(-20, 0));
                cloud.setDirection(1);
            }
        } else if(keyCode == Input.Keys.RIGHT) {
            if(cloud != null) {
                cloud.setLinearVelocity(new Vector2(20, 0));
                cloud.setDirection(2);
            }
        } else if(keyCode == Input.Keys.UP) {
            if(emitter != null) {
                emitter.setLinearVelocity(new Vector2(0, 20));
            }

        } else if(keyCode == Input.Keys.DOWN) {
            if(emitter != null) {
                emitter.setLinearVelocity(new Vector2(0, -20));
            }
        } else if(keyCode == Input.Keys.SPACE) {
            itRain = true;
            if(cloud != null) {
                cloud.setDirection(-1);
            }
        }

        return true;
    }

    @Override
    public boolean keyUp(int keyCode) {
        if(keyCode == Input.Keys.LEFT) {
            if(cloud != null) {
                cloud.setLinearVelocity(new Vector2(0, 0));
                cloud.setDirection(0);
            }
        } else if(keyCode == Input.Keys.RIGHT) {
            if(cloud != null) {
                cloud.setLinearVelocity(new Vector2(0, 0));
                cloud.setDirection(0);
            }
        } else if(keyCode == Input.Keys.UP) {
            if(emitter != null) {
                emitter.setLinearVelocity(new Vector2(0, 0));
            }
        } else if(keyCode == Input.Keys.DOWN) {
            if(emitter != null) {
                emitter.setLinearVelocity(new Vector2(0, 0));
            }
        } else if(keyCode == Input.Keys.SPACE) {
            itRain = false;
            if(cloud != null) {
                cloud.setDirection(0);
            }
        }

        return true;
    }

    public int getDropsNumber() {
        return dropList.size();
    }

    @Override
    public void draw() {
        getSpriteBatch().begin();
            getSpriteBatch().draw(backgroundTexture, 0, 0);
        getSpriteBatch().end();

        if(useShader) {
            m_fbo.begin();
                getSpriteBatch().begin();
                Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
                for (Drop drop : dropList) {
                    getSpriteBatch().draw(dropTexture, drop.getPosition().x - dropTextureRadius / 2,
                            drop.getPosition().y - dropTextureRadius / 2, dropTextureRadius, dropTextureRadius);
                }
                getSpriteBatch().end();
            m_fbo.end();

            sbS.begin();
                if(!lightVersion)
                    shader.setUniformf("u_time", time);
                sbS.draw(m_fboRegion, 0, 0, m_fboRegion.getRegionWidth(), m_fboRegion.getRegionHeight());
            sbS.end();
        } else {
            getSpriteBatch().begin();
                getSpriteBatch().draw(m_fboRegion, 0, 0, m_fboRegion.getRegionWidth(), m_fboRegion.getRegionHeight());
            getSpriteBatch().end();
        }

        getCamera().update();
        getSpriteBatch().begin();
            getRoot().draw(getSpriteBatch(), 1);
            font.draw(getSpriteBatch(), "FPS: "+Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight()-20);
            font.draw(getSpriteBatch(), "Drops: "+getDropsNumber(), 10, Gdx.graphics.getHeight()-40);
            if(winHint != null)
                font.draw(getSpriteBatch(), "Hint: "+winHint, 10, Gdx.graphics.getHeight()-60);
        getSpriteBatch().end();

        if(debugRendererEnabled) {
            getCamera().viewportHeight *= WORLD_TO_BOX;
            getCamera().viewportWidth *= WORLD_TO_BOX;
            getCamera().position.set(getCamera().viewportWidth * .5f, getCamera().viewportHeight * .5f, 0f);

            getCamera().update();
            debugRenderer.render(physicsWorld, getCamera().combined);

            getCamera().viewportHeight = 480;
            getCamera().viewportWidth = 800;
            getCamera().position.set(getCamera().viewportWidth * .5f, getCamera().viewportHeight * .5f, 0f);
            getCamera().update();
        }
    }

    public void setWinHint(String winHint) {
        this.winHint = winHint;
    }
}
