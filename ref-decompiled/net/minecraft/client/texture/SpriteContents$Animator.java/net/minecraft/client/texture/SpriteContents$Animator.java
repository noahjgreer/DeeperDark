/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.texture.SpriteContents;

@Environment(value=EnvType.CLIENT)
public class SpriteContents.Animator
implements AutoCloseable {
    private int frame;
    private int elapsedTimeInFrame;
    private final SpriteContents.Animation animation;
    private final Int2ObjectMap<GpuTextureView> textureViewsByFrame;
    private final GpuBufferSlice[] animationInfosByFrame;
    private boolean changedFrame = true;

    SpriteContents.Animator(SpriteContents spriteContents, SpriteContents.Animation animation, Int2ObjectMap<GpuTextureView> textureViewsByFrame, GpuBufferSlice[] bufferSlices) {
        this.animation = animation;
        this.textureViewsByFrame = textureViewsByFrame;
        this.animationInfosByFrame = bufferSlices;
    }

    public void tick() {
        ++this.elapsedTimeInFrame;
        this.changedFrame = false;
        SpriteContents.AnimationFrame animationFrame = this.animation.frames.get(this.frame);
        if (this.elapsedTimeInFrame >= animationFrame.time) {
            int i = animationFrame.index;
            this.frame = (this.frame + 1) % this.animation.frames.size();
            this.elapsedTimeInFrame = 0;
            int j = this.animation.frames.get((int)this.frame).index;
            if (i != j) {
                this.changedFrame = true;
            }
        }
    }

    public GpuBufferSlice getBufferSlice(int frame) {
        return this.animationInfosByFrame[frame];
    }

    public boolean isDirty() {
        return this.animation.interpolated || this.changedFrame;
    }

    public void upload(RenderPass renderPass, GpuBufferSlice bufferSlice) {
        GpuSampler gpuSampler = RenderSystem.getSamplerCache().get(FilterMode.NEAREST, true);
        List<SpriteContents.AnimationFrame> list = this.animation.frames;
        int i = list.get((int)this.frame).index;
        float f = (float)this.elapsedTimeInFrame / (float)this.animation.frames.get((int)this.frame).time;
        int j = (int)(f * 1000.0f);
        if (this.animation.interpolated) {
            int k = list.get((int)((this.frame + 1) % list.size())).index;
            renderPass.setPipeline(RenderPipelines.ANIMATE_SPRITE_INTERPOLATE);
            renderPass.bindTexture("CurrentSprite", (GpuTextureView)this.textureViewsByFrame.get(i), gpuSampler);
            renderPass.bindTexture("NextSprite", (GpuTextureView)this.textureViewsByFrame.get(k), gpuSampler);
        } else if (this.changedFrame) {
            renderPass.setPipeline(RenderPipelines.ANIMATE_SPRITE_BLIT);
            renderPass.bindTexture("Sprite", (GpuTextureView)this.textureViewsByFrame.get(i), gpuSampler);
        }
        renderPass.setUniform("SpriteAnimationInfo", bufferSlice);
        renderPass.draw(j << 3, 6);
    }

    @Override
    public void close() {
        for (GpuTextureView gpuTextureView : this.textureViewsByFrame.values()) {
            gpuTextureView.texture().close();
            gpuTextureView.close();
        }
    }
}
