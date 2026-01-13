/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderLayerSet;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.entity.player.SkinTextures;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class PlayerSkinCache.Entry {
    private final GameProfile profile;
    private final SkinTextures textures;
    private @Nullable RenderLayer renderLayer;
    private @Nullable GpuTextureView textureView;
    private @Nullable TextRenderLayerSet textRenderLayers;

    public PlayerSkinCache.Entry(GameProfile profile, SkinTextures textures, SkinTextures.SkinOverride skinOverride) {
        this.profile = profile;
        this.textures = textures.withOverride(skinOverride);
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    public SkinTextures getTextures() {
        return this.textures;
    }

    public RenderLayer getRenderLayer() {
        if (this.renderLayer == null) {
            this.renderLayer = PlayerSkinCache.getRenderLayer(this.textures);
        }
        return this.renderLayer;
    }

    public GpuTextureView getTextureView() {
        if (this.textureView == null) {
            this.textureView = PlayerSkinCache.this.textureManager.getTexture(this.textures.body().texturePath()).getGlTextureView();
        }
        return this.textureView;
    }

    public TextRenderLayerSet getTextRenderLayers() {
        if (this.textRenderLayers == null) {
            this.textRenderLayers = TextRenderLayerSet.of(this.textures.body().texturePath());
        }
        return this.textRenderLayers;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerSkinCache.Entry)) return false;
        PlayerSkinCache.Entry entry = (PlayerSkinCache.Entry)o;
        if (!this.profile.equals((Object)entry.profile)) return false;
        if (!this.textures.equals(entry.textures)) return false;
        return true;
    }

    public int hashCode() {
        int i = 1;
        i = 31 * i + this.profile.hashCode();
        i = 31 * i + this.textures.hashCode();
        return i;
    }
}
