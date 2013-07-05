package com.alex.rain.stages;

import com.alex.rain.RainGame;
import com.alex.rain.helpers.LiquidHelper;
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
    private List<Drop> dropList = new ArrayList<Drop>();
    private LiquidHelper liquidHelper;
    private LuaFunction luaOnCreateFunc;
    private LuaFunction luaOnCheckFunc;
    private boolean wonGame;
    private Table table;
    private ShaderProgram shader;
    private Texture textureDrop;
    private final Box2DDebugRenderer debugRenderer;
    private boolean debugRendererEnabled;
    private final SpriteBatch sbS;
    private final FrameBuffer m_fbo;
    private final TextureRegion m_fboRegion;
    private final float m_fboScaler = 1.5f;
    private float time;
    private float timeLastDrop;
    private boolean itRain;
    private Cloud cloud;

    public GameWorld(String name) {
        liquidHelper = new LiquidHelper(dropList);

        ScriptEngineManager sem     = new ScriptEngineManager();
        ScriptEngine        engine  = sem.getEngineByExtension(".lua");
        ScriptEngineFactory factory = engine.getFactory();
        CompiledScript cs;
        String filename = "data/" + name + ".lua";
        try {
            Reader reader = new FileReader(filename);
            cs = ((Compilable)engine).compile(reader);
            SimpleBindings sb = new SimpleBindings();
            cs.eval(sb);
            luaOnCheckFunc = (LuaFunction) sb.get("onCheck");
            luaOnCreateFunc = (LuaFunction) sb.get("onCreate");
        } catch (Exception e) {
            //LogHandler.log.error(e.getMessage(), e);
            System.out.println("error: " + filename);
        }

        final String VERTEX = Gdx.files.internal("data/drop_shader.vert").readString();
        final String FRAGMENT = Gdx.files.internal("data/drop_shader.frag").readString();

        shader = new ShaderProgram(VERTEX, FRAGMENT);
        if(!shader.isCompiled())
            System.out.println(shader.getLog());

        textureDrop = TextureManager.getInstance().getTexture("forward.png");

        sbS = new SpriteBatch();
        sbS.setShader(shader);

        m_fbo = new FrameBuffer(Pixmap.Format.RGB565, (int)(Gdx.graphics.getWidth() * m_fboScaler), (int)(Gdx.graphics.getHeight() * m_fboScaler), false);
        m_fboRegion = new TextureRegion(m_fbo.getColorBufferTexture());
        m_fboRegion.flip(false, true);

        debugRenderer = new Box2DDebugRenderer();
    }

    public void add(SimpleActor actor) {
        actor.createPhysicsActor(physicsWorld);
        actor.prepareActor();
        actorList.add(actor);

        if(actor.getType() == SimpleActor.TYPE.CLOUD)
            cloud = (Cloud)actor;

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
        luaOnCreateFunc.call(luaWorld);
    }

    @Override
    public void act(float delta) {
        time += Gdx.graphics.getDeltaTime();
        liquidHelper.applyLiquidConstraint(1/60f); // TODO: check this shit?
        physicsWorld.step(1/15f, 6, 3);
        /*for(SimpleActor actor : actorList)
            actor.act(delta);*/
        super.act(delta);


        LuaValue luaDrop = CoerceJavaToLua.coerce(dropList);
        LuaValue retvals = luaOnCheckFunc.call(luaDrop);
        if(retvals.toboolean(1) && !wonGame) {
            wonGame = true;
            showWinnerMenu();
        }

        if(itRain && !wonGame && cloud != null) {
            if(time - timeLastDrop > 0.05) {
                Drop drop = new Drop();
                Random r = new Random();
                float offset = r.nextFloat() * cloud.getWidth() * 2/3;
                add(drop);
                drop.setPosition(new Vector2(cloud.getPosition().x - cloud.getWidth() / 3 + offset, cloud.getPosition().y));
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

        final Label label = new Label("Victory!", skin);
        table.add(label);
        label.setPosition(0, -100);

        table.row().width(400).padTop(10);

        final TextButton button = new TextButton("Next", skin);
        table.add(button);
        button.setPosition(0, -100);

        table.row().width(400).padTop(10);

        final TextButton button2 = new TextButton("Options", skin);
        table.add(button2);
        button2.setPosition(0, 0);

        table.row().width(400).padTop(10);

        final TextButton button3 = new TextButton("Back to main menu", skin);
        table.add(button3);
        button3.setPosition(0, 100);

        button.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                RainGame.getInstance().setLevel("level2");
            }
        });

        button2.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                //RainGame.getInstance().setScreen(new LevelsMenu());
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
        if(wonGame || cloud != null)
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
        if(keyCode == Input.Keys.F4)
            debugRendererEnabled = !debugRendererEnabled;
        else if(keyCode == Input.Keys.ESCAPE)
            showWinnerMenu();
        else if(keyCode == Input.Keys.LEFT) {
            if(cloud != null)
                cloud.setLinearVelocity(new Vector2(-20, 0));
        } else if(keyCode == Input.Keys.RIGHT) {
            if(cloud != null)
                cloud.setLinearVelocity(new Vector2(20, 0));
        } else if(keyCode == Input.Keys.SPACE)
            itRain = true;

        return true;
    }

    @Override
    public boolean keyUp(int keyCode) {
        if(keyCode == Input.Keys.LEFT) {
            if(cloud != null)
                cloud.setLinearVelocity(new Vector2(0, 0));
        } else if(keyCode == Input.Keys.RIGHT) {
            if(cloud != null)
                cloud.setLinearVelocity(new Vector2(0, 0));
        } else if(keyCode == Input.Keys.SPACE)
            itRain = false;

        return true;
    }

    public int getDropsNumber() {
        return dropList.size();
    }

    @Override
    public void draw() {
        m_fbo.begin();
            getSpriteBatch().begin();
            Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
            for (Drop drop : dropList) {
                getSpriteBatch().draw(textureDrop,
                        drop.getPosition().x - textureDrop.getWidth() / 2, drop.getPosition().y - textureDrop.getWidth() / 2);
            }
            getSpriteBatch().end();
        m_fbo.end();

        sbS.begin();
            shader.setUniformf("u_time", time);
            sbS.draw(m_fboRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sbS.end();

        super.draw();

        if(debugRendererEnabled)
            debugRenderer.render(physicsWorld, getCamera().combined);
    }
}
