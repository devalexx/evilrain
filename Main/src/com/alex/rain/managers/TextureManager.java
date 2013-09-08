package com.alex.rain.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FileTextureData;

import java.util.*;

/**
 * @author: Alexander Shubenkov
 * @since: 05.07.13
 */

public class TextureManager {
    private Map<String, Texture> textureMap = new HashMap<String, Texture>();
    private Map<Texture, TextureData> textureDataMap = new HashMap<Texture, TextureData>();

    private Map<String, TextureAtlas> textureAtlasMap = new HashMap<String, TextureAtlas>();
    private Map<String, TextureAtlas> textureAtlasNameMap = new HashMap<String, TextureAtlas>();
    private Map<TextureAtlas, TextureAtlas.TextureAtlasData> textureAtlasDataMap =
            new HashMap<TextureAtlas, TextureAtlas.TextureAtlasData>();

    private static TextureManager manager = new TextureManager();
    
    private TextureManager() {}

    public static TextureManager getInstance() {
        return manager;
    }

    public Sprite getSpriteFromDefaultAtlas(String textureName) {
        return getSpriteFromAtlas("pack.atlas", textureName);
    }

    public Sprite getSpriteFromAtlas(String atlasName, String textureName) {
        if(atlasName == null)
            for(TextureAtlas textureAtlas : textureAtlasMap.values()) {
                Sprite s = textureAtlas.createSprite(textureName);
                if(s != null)
                    return s;
            }

        if(textureAtlasMap.containsKey(atlasName)) {
            return textureAtlasMap.get(atlasName).createSprite(textureName);
        }

        return null;
    }

    public Sprite getRegionFromDefaultAtlas(String textureName) {
        return getSpriteFromAtlas("pack.atlas", textureName);
    }

    public TextureAtlas.AtlasRegion getRegionFromAtlas(String atlasName, String textureName) {
        if(atlasName == null)
            for(TextureAtlas textureAtlas : textureAtlasMap.values()) {
                TextureAtlas.AtlasRegion ar = textureAtlas.findRegion(textureName);
                if(ar != null)
                    return ar;
            }

        if(textureAtlasMap.containsKey(atlasName)) {
            return textureAtlasMap.get(atlasName).findRegion(textureName);
        }

        return null;
    }

    public TextureAtlas getAtlas(String path) {
        if(textureAtlasMap.containsKey(path)) {
            return textureAtlasMap.get(path);
        } else {
            TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("data/" + path));
            textureAtlasMap.put(path, textureAtlas);
            for(Texture texture : textureAtlas.getTextures()) {
                String name = "" + texture;
                if(texture.getTextureData() instanceof FileTextureData)
                    name = ((FileTextureData)texture.getTextureData()).getFileHandle().name();

                textureMap.put(name, texture);
                textureDataMap.put(texture, texture.getTextureData());
            }

            return textureAtlas;
        }
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
