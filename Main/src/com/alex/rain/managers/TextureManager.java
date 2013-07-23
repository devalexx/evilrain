package com.alex.rain.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;

import java.util.*;

/**
 * @author: Alexander Shubenkov
 * @since: 05.07.13
 */

public class TextureManager {
    private Map<String, Texture> textureMap = new HashMap<String, Texture>();
    private Map<Texture, TextureData> textureDataMap = new HashMap<Texture, TextureData>();
    private static TextureManager manager = new TextureManager();
    
    private TextureManager() {}

    public static TextureManager getInstance() {
        return manager;
    }

    public Texture getTexture(String path) {
        if(textureMap.containsKey(path)) {
            return textureMap.get(path);
        } else {
            Texture texture = new Texture(Gdx.files.internal("data/" + path));
            textureMap.put(path, texture);
            textureDataMap.put(texture, texture.getTextureData());
            return texture;
        }
    }

    public void reload() {
        for(Texture texture : textureMap.values()) {
            texture.load(textureDataMap.get(texture));
        }
    }
}
