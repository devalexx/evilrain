package com.alex.rain.stages;

import com.alex.rain.RainGame;
import com.alex.rain.helpers.LiquidHelper;
import com.alex.rain.models.*;
import com.alex.rain.screens.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
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
    }

    public void add(SimpleActor actor) {
        actor.createPhysicsActor(physicsWorld);
        actor.prepareActor();
        actorList.add(actor);
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
        liquidHelper.applyLiquidConstraint(/*delta*3*/1/60f); // TODO: wrong?
        physicsWorld.step(delta*3, 8, 3);
        /*for(SimpleActor actor : actorList)
            actor.act(delta);*/
        super.act(delta);


        LuaValue luaDrop = CoerceJavaToLua.coerce(dropList);
        LuaValue retvals = luaOnCheckFunc.call(luaDrop);
        if(retvals.toboolean(1) && !wonGame) {
            wonGame = true;
            showWinnerMenu();
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
            public void changed (ChangeEvent event, Actor actor) {
                RainGame.getInstance().setMenu(new MainMenuScreen());
            }
        });
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(wonGame)
            return true;
        Drop drop = new Drop();
        Random r = new Random();
        int offset = r.nextInt(10) - 10;
        add(drop);
        drop.setPosition(new Vector2(screenX + offset, Gdx.graphics.getHeight() - screenY + offset));

        return true;
    }

    public int getDropsNumber() {
        return dropList.size();
    }
}
