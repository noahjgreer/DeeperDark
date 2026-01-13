/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.texture.MapTextureManager
 *  net.minecraft.client.texture.MapTextureManager$MapTexture
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.component.type.MapIdComponent
 *  net.minecraft.item.map.MapState
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.texture;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.MapTextureManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class MapTextureManager
implements AutoCloseable {
    private final Int2ObjectMap<MapTexture> texturesByMapId = new Int2ObjectOpenHashMap();
    final TextureManager textureManager;

    public MapTextureManager(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void setNeedsUpdate(MapIdComponent mapIdComponent, MapState mapState) {
        this.getMapTexture(mapIdComponent, mapState).setNeedsUpdate();
    }

    public Identifier getTextureId(MapIdComponent mapIdComponent, MapState mapState) {
        MapTexture mapTexture = this.getMapTexture(mapIdComponent, mapState);
        mapTexture.updateTexture();
        return mapTexture.textureId;
    }

    public void clear() {
        for (MapTexture mapTexture : this.texturesByMapId.values()) {
            mapTexture.close();
        }
        this.texturesByMapId.clear();
    }

    private MapTexture getMapTexture(MapIdComponent mapId, MapState mapState) {
        return (MapTexture)this.texturesByMapId.compute(mapId.id(), (id, mapTexture) -> {
            if (mapTexture == null) {
                return new MapTexture(this, id.intValue(), mapState);
            }
            mapTexture.setState(mapState);
            return mapTexture;
        });
    }

    @Override
    public void close() {
        this.clear();
    }
}

