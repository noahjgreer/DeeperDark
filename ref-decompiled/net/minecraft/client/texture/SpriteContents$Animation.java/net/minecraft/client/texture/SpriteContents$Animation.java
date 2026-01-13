/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.SpriteContents;

@Environment(value=EnvType.CLIENT)
class SpriteContents.Animation {
    final List<SpriteContents.AnimationFrame> frames;
    private final int frameCount;
    final boolean interpolated;

    SpriteContents.Animation(List<SpriteContents.AnimationFrame> frames, int frameCount, boolean interpolated) {
        this.frames = frames;
        this.frameCount = frameCount;
        this.interpolated = interpolated;
    }

    int getFrameX(int frame) {
        return frame % this.frameCount;
    }

    int getFrameY(int frame) {
        return frame / this.frameCount;
    }

    public SpriteContents.Animator createAnimator(GpuBufferSlice bufferSlice, int animationInfoSize) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap();
        GpuBufferSlice[] gpuBufferSlices = new GpuBufferSlice[SpriteContents.this.mipmapLevelsImages.length];
        for (int i : this.getDistinctFrameCount().toArray()) {
            GpuTexture gpuTexture = gpuDevice.createTexture(() -> String.valueOf(SpriteContents.this.id) + " animation frame " + i, 5, TextureFormat.RGBA8, SpriteContents.this.width, SpriteContents.this.height, 1, SpriteContents.this.mipmapLevelsImages.length + 1);
            int j = this.getFrameX(i) * SpriteContents.this.width;
            int k = this.getFrameY(i) * SpriteContents.this.height;
            for (int l = 0; l < SpriteContents.this.mipmapLevelsImages.length; ++l) {
                RenderSystem.getDevice().createCommandEncoder().writeToTexture(gpuTexture, SpriteContents.this.mipmapLevelsImages[l], l, 0, 0, 0, SpriteContents.this.width >> l, SpriteContents.this.height >> l, j >> l, k >> l);
            }
            int2ObjectMap.put(i, (Object)RenderSystem.getDevice().createTextureView(gpuTexture));
        }
        for (int m = 0; m < SpriteContents.this.mipmapLevelsImages.length; ++m) {
            gpuBufferSlices[m] = bufferSlice.slice(m * animationInfoSize, animationInfoSize);
        }
        return new SpriteContents.Animator(SpriteContents.this, this, (Int2ObjectMap<GpuTextureView>)int2ObjectMap, gpuBufferSlices);
    }

    public IntStream getDistinctFrameCount() {
        return this.frames.stream().mapToInt(frame -> frame.index).distinct();
    }
}
