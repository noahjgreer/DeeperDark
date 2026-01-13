/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.AddressMode
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.GpuSampler
 *  net.minecraft.client.texture.AbstractTexture
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GpuSampler;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractTexture
implements AutoCloseable {
    protected @Nullable GpuTexture glTexture;
    protected @Nullable GpuTextureView glTextureView;
    protected GpuSampler sampler = RenderSystem.getSamplerCache().get(AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.NEAREST, FilterMode.LINEAR, false);

    @Override
    public void close() {
        if (this.glTexture != null) {
            this.glTexture.close();
            this.glTexture = null;
        }
        if (this.glTextureView != null) {
            this.glTextureView.close();
            this.glTextureView = null;
        }
    }

    public GpuTexture getGlTexture() {
        if (this.glTexture == null) {
            throw new IllegalStateException("Texture does not exist, can't get it before something initializes it");
        }
        return this.glTexture;
    }

    public GpuTextureView getGlTextureView() {
        if (this.glTextureView == null) {
            throw new IllegalStateException("Texture view does not exist, can't get it before something initializes it");
        }
        return this.glTextureView;
    }

    public GpuSampler getSampler() {
        return this.sampler;
    }
}

