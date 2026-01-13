/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MapColor;
import net.minecraft.client.texture.MapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
class MapTextureManager.MapTexture
implements AutoCloseable {
    private MapState state;
    private final NativeImageBackedTexture texture;
    private boolean needsUpdate = true;
    final Identifier textureId;

    MapTextureManager.MapTexture(MapTextureManager mapTextureManager, int id, MapState state) {
        this.state = state;
        this.texture = new NativeImageBackedTexture(() -> "Map " + id, 128, 128, true);
        this.textureId = Identifier.ofVanilla("map/" + id);
        mapTextureManager.textureManager.registerTexture(this.textureId, this.texture);
    }

    void setState(MapState state) {
        boolean bl = this.state != state;
        this.state = state;
        this.needsUpdate |= bl;
    }

    public void setNeedsUpdate() {
        this.needsUpdate = true;
    }

    void updateTexture() {
        if (this.needsUpdate) {
            NativeImage nativeImage = this.texture.getImage();
            if (nativeImage != null) {
                for (int i = 0; i < 128; ++i) {
                    for (int j = 0; j < 128; ++j) {
                        int k = j + i * 128;
                        nativeImage.setColorArgb(j, i, MapColor.getRenderColor(this.state.colors[k]));
                    }
                }
            }
            this.texture.upload();
            this.needsUpdate = false;
        }
    }

    @Override
    public void close() {
        this.texture.close();
    }
}
