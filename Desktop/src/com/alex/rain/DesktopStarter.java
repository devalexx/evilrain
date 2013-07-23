package com.alex.rain;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.*;

/**
 * @author: Alexander Shubenkov
 * @since: 28.05.13
 */

public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Drop";
        cfg.useGL20 = true;
        cfg.width = 800;
        cfg.height = 480;
        new LwjglApplication(RainGame.getInstance(), cfg);

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }
}
