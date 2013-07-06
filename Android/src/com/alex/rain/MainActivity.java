package com.alex.rain;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import org.luaj.vm2.lib.jse.LuajavaLib;

public class MainActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = false;
        cfg.useCompass = false;
        //cfg.useWakelock = true;
        cfg.useGL20 = true;
        LuajavaLib.classLoader = this.getApplicationContext().getClassLoader();
        initialize(RainGame.getInstance(), cfg);
    }
}